package com.lxb.chat.server.handler.message.impl;

import com.lxb.chat.server.handler.message.MessageHandler;
import com.lxb.chat.server.prompt.PromptConstants;
import com.lxb.chat.server.user.UserManager;
import com.lxb.common.domain.Message;
import com.lxb.common.domain.Response;
import com.lxb.common.domain.ResponseHeader;
import com.lxb.common.enumeration.ResponseNum;
import com.lxb.common.enumeration.ResponseType;
import com.lxb.common.util.ProtostuffUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component("MessageHandler.logout")
public class LogoutMessageHandler extends MessageHandler {

    private final UserManager userManager;

    @Autowired
    public LogoutMessageHandler(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public void handle(Message message,
                       Selector selector,
                       SelectionKey selectionKey,
                       AtomicInteger usersOnline) throws InterruptedException {

        SocketChannel channel = (SocketChannel) selectionKey.channel();
        userManager.logout(channel);

        try {
            byte[] response = ProtostuffUtil.serialize(
                    new Response(
                            ResponseHeader.builder()
                                    .type(ResponseType.PROMPT)
                                    .responseNum(ResponseNum.LOGOUT.getNum())
                                    .sender(message.getHeader().getSender())
                                    .timeStamp(message.getHeader().getTimeStamp()).build(),
                            PromptConstants.LOGOUT_SUCCESS.getBytes(PromptConstants.CHARSET)
                    )
            );

            channel.write(ByteBuffer.wrap(response));

            usersOnline.decrementAndGet();

            byte[] logoutBroadcast = ProtostuffUtil.serialize(
                    new Response(
                            ResponseHeader.builder()
                                    .type(ResponseType.MESSAGE)
                                    .sender(SYSTEM_PROMPT)
                                    .timeStamp(message.getHeader().getTimeStamp()).build(),
                            String.format(PromptConstants.LOGOUT_BROADCAST,
                                    message.getHeader().getSender()).getBytes(PromptConstants.CHARSET)
                    )
            );

            super.broadcast(logoutBroadcast, selector);

            log.info("客户端已退出");

            // 从 keys 里 cancel 下线的客户端
            selectionKey.cancel();
            channel.close();
            channel.socket().close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
