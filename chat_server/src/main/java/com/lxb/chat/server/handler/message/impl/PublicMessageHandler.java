package com.lxb.chat.server.handler.message.impl;

import com.lxb.chat.server.handler.message.MessageHandler;
import com.lxb.common.domain.Message;
import com.lxb.common.domain.MessageHeader;
import org.springframework.stereotype.Component;

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

    }
}