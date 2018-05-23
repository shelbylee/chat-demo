package com.lxb.chat.server.handler.message.impl;

import com.lxb.chat.server.handler.message.MessageHandler;
import com.lxb.chat.server.prompt.PromptConstants;
import com.lxb.chat.server.user.UserManager;
import com.lxb.common.domain.Message;
import com.lxb.common.domain.MessageHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

@Component("MessageHandler.login")
public class LoginMessageHandler extends MessageHandler {

    private final UserManager userManager;

    @Autowired
    public LoginMessageHandler(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public void handle(Message message,
                       Selector selector,
                       SelectionKey selectionKey,
                       AtomicInteger usersOnline) throws InterruptedException {

        SocketChannel channel = (SocketChannel) selectionKey.channel();
        MessageHeader header = message.getHeader();
        String username = header.getSender();
        String password = new String(message.getBody(), PromptConstants.CHARSET);

        if (userManager.login(channel, username, password)) {

        } else {

        }
    }
}
