package com.lxb.chat.server.handler.message.impl;

import com.lxb.chat.server.handler.message.MessageHandler;
import com.lxb.common.domain.Message;
import com.lxb.common.domain.MessageHeader;
import com.lxb.common.domain.Response;
import com.lxb.common.domain.ResponseHeader;
import com.lxb.common.enumeration.ResponseType;
import com.lxb.common.util.ProtostuffUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.atomic.AtomicInteger;

@Component("MessageHandler.public")
public class PublicMessageHandler extends MessageHandler {
    @Override
    public void handle(Message message,
                       Selector selector,
                       SelectionKey selectionKey,
                       AtomicInteger usersOnline) throws InterruptedException {

        byte[] response = ProtostuffUtil.serialize(
                new Response(
                        ResponseHeader.builder()
                            .type(ResponseType.MESSAGE)
                            .sender(message.getHeader().getSender())
                            .timeStamp(message.getHeader().getTimeStamp()).build(),
                        message.getBody()
                )
        );

        try {
            super.broadcast(response, selector);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
