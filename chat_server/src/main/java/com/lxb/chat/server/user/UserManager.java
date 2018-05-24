package com.lxb.chat.server.user;

import com.google.common.base.Preconditions;
import com.lxb.common.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("userManager")
@Slf4j
public class UserManager {

    private Map<String, User> userMap;

    private Map<SocketChannel, String> usersOnline;

    public UserManager() {

        userMap = new ConcurrentHashMap<>();

        userMap.put("user1", User.builder().username("user1").password("user1").build());
        userMap.put("user2", User.builder().username("user2").password("user2").build());
        userMap.put("user3", User.builder().username("user3").password("user3").build());
        userMap.put("user4", User.builder().username("user4").password("user4").build());
        userMap.put("user5", User.builder().username("user5").password("user5").build());

        usersOnline = new ConcurrentHashMap<>();
    }

    public synchronized boolean login(SocketChannel channel,
                         String username,
                         String password) {

        Preconditions.checkNotNull(userMap);
        if (!userMap.containsKey(username))
            return false;

        User user = userMap.get(username);
        if (!user.getPassword().equals(password)
                || user.getChannel() != null)
            return false;

        user.setChannel(channel);
        usersOnline.put(channel, username);

        return true;
    }

    public synchronized void logout(SocketChannel channel) {
        String username = usersOnline.get(channel);
        userMap.get(username).setChannel(null);
        usersOnline.remove(channel);
        log.info("{}已下线", username);
    }

    public synchronized SocketChannel getUserChannel(String username) {

        User user = userMap.get(username);
        Preconditions.checkNotNull(user, "该用户不存在");

        SocketChannel channel = user.getChannel();
        Preconditions.checkNotNull(usersOnline);
        if (usersOnline.containsKey(channel))
            return channel;
        else
            return null;
    }
}