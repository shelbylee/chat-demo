package com.lxb.chat.server.handler.message.impl;

import com.lxb.chat.server.handler.message.MessageHandler;
import com.lxb.chat.server.prompt.PromptConstants;
import com.lxb.chat.server.user.UserManager;
import com.lxb.common.domain.Message;
import com.lxb.common.domain.MessageHeader;
import com.lxb.common.domain.Response;
import com.lxb.common.domain.ResponseHeader;
import com.lxb.common.enumeration.ResponseNum;
import com.lxb.common.enumeration.ResponseType;
import com.lxb.common.util.ProtostuffUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
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

        try {
            if (userManager.login(channel, username, password)) {
                byte[] response = ProtostuffUtil.serialize(
                        new Response(
                                ResponseHeader.builder()
                                        .type(ResponseType.PROMPT)
                                        .sender(message.getHeader().getSender())
                                        .timeStamp(message.getHeader().getTimeStamp())
                                        .responseNum(ResponseNum.LOGIN_SUCCESS.getNum()).build(),
                                String.format(PromptConstants.LOGIN_SUCCESS,
                                        usersOnline.incrementAndGet()).getBytes(PromptConstants.CHARSET)
                        )
                );

                // TODO: to solve: sticky package
                channel.write(ByteBuffer.wrap(response));

                Thread.sleep(10);

                byte[] loginBroadcast = ProtostuffUtil.serialize(
                        new Response(
                                ResponseHeader.builder()
                                        .type(ResponseType.MESSAGE)
                                        .sender(SYSTEM_PROMPT)
                                        .timeStamp(message.getHeader().getTimeStamp()).build(),
                                String.format(PromptConstants.LOGIN_BROADCAST,
                                        message.getHeader().getSender()).getBytes(PromptConstants.CHARSET)
                        )
                );

                super.broadcast(loginBroadcast, selector);

            } else { // 登录失败
                byte[] response = ProtostuffUtil.serialize(
                        new Response(
                                ResponseHeader.builder()
                                        .type(ResponseType.PROMPT)
                                        .responseNum(ResponseNum.LOGIN_FAILURE.getNum())
                                        .sender(message.getHeader().getSender())
                                        .timeStamp(message.getHeader().getTimeStamp()).build(),
                                PromptConstants.LOGIN_FAILURE.getBytes(PromptConstants.CHARSET)
                        )
                );
                channel.write(ByteBuffer.wrap(response));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
