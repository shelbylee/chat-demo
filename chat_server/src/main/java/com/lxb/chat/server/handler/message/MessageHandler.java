package com.lxb.chat.server.handler.message;

import com.lxb.common.domain.Message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MessageHandler {

    protected static final String SYSTEM_PROMPT = "系统提示";

    public abstract void handle(Message message,
                                Selector selector,
                                SelectionKey selectionKey,
                                AtomicInteger usersOnline) throws InterruptedException;

    protected void broadcast(byte[] data, Selector selector) throws IOException {
        for (SelectionKey selectionKey : selector.keys()) {
            Channel channel = selectionKey.channel();
            if (channel instanceof SocketChannel) {
                SocketChannel socketChannel = (SocketChannel) channel;
                if (socketChannel.isConnected())
                    socketChannel.write(ByteBuffer.wrap(data));
            }
        }
    }
}
