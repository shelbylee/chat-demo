package com.lxb.chat.client;

import com.lxb.common.domain.Message;
import com.lxb.common.domain.MessageHeader;
import com.lxb.common.enumeration.MessageType;
import com.lxb.common.util.ProtostuffUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Client extends Frame {

    private final static int DEFAULT_BUFFER_SIZE = 1024;

    private Charset charset = StandardCharsets.UTF_8;

    private Selector selector;
    private SocketChannel socketChannel;
    private ByteBuffer byteBuffer;

    private String sender;

    private boolean isLogin;

    private Client(int x, int y, int w, int h) {
        initWindow(x, y, w, h);
        registerChannel();
    }

    // TODO: init window
    private void initWindow(int x, int y, int w, int h) {
    }

    private void registerChannel() {
        try {
            selector = Selector.open();
            // 连到本地
            socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8888));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            byteBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录，并将登陆 message 写入 channel
     */
    private void login() {

        String username = JOptionPane.showInputDialog("请输入用户名");
        String password = JOptionPane.showInputDialog("请输入密码");

        Message message = new Message(
                MessageHeader.builder()
                    .type(MessageType.LOG_IN)
                    .sender(username)
                    .timeStamp(System.currentTimeMillis())
                    .build(),
                password.getBytes(charset)
        );

        writeMessageIntoChannel(message);
        this.sender = username;
    }

    // TODO: log out
    /**
     * 用户下线，并将 message 写入 channel
     */
    private void logout() {
    }

    private void writeMessageIntoChannel(Message message) {
        try {
            socketChannel.write(ByteBuffer.wrap(ProtostuffUtil.serialize(message)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接受信息的线程
     */
    private class Receiver implements Runnable {
        @Override
        public void run() {

        }
    }

    public static void main(String[] args) {
    }
}
