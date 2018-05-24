package com.lxb.chat.server.handler.message.impl;

import com.lxb.chat.server.handler.message.MessageHandler;
import com.lxb.chat.server.user.UserManager;
import com.lxb.common.domain.Message;
import com.lxb.common.domain.MessageHeader;
import com.lxb.common.domain.Response;
import com.lxb.common.domain.ResponseHeader;
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
@Component("MessageHandler.private")
public class PrivateMessageHandler extends MessageHandler {

    private final UserManager userManager;

    @Autowired
    public PrivateMessageHandler(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public void handle(Message message,
                       Selector selector,
                       SelectionKey selectionKey,
                       AtomicInteger usersOnline) throws InterruptedException {

        SocketChannel sender = (SocketChannel) selectionKey.channel();
        MessageHeader header = message.getHeader();
        SocketChannel receiver = userManager.getUserChannel(header.getReceiver());

        // 如果对方在线
        if (receiver != null) {
            byte[] response = ProtostuffUtil.serialize(
                    new Response(
                            ResponseHeader.builder()
                                    .type(ResponseType.MESSAGE)
                                    .sender(message.getHeader().getSender())
                                    .timeStamp(message.getHeader().getTimeStamp())
                                    .build(),
                            message.getBody()
                    )
            );

            try {
                receiver.write(ByteBuffer.wrap(response));
                sender.write(ByteBuffer.wrap(response));
                log.info("已发送消息给", receiver);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
