package com.lxb.chat.client;

import com.lxb.common.domain.Message;
import com.lxb.common.domain.MessageHeader;
import com.lxb.common.domain.Response;
import com.lxb.common.domain.ResponseHeader;
import com.lxb.common.enumeration.MessageType;
import com.lxb.common.enumeration.ResponseNum;
import com.lxb.common.util.DateUtil;
import com.lxb.common.util.ProtostuffUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

@Slf4j
public class Client extends Frame {

    private final static int DEFAULT_BUFFER_SIZE = 1024;

    private Charset charset = StandardCharsets.UTF_8;

    private Selector selector;
    private SocketChannel socketChannel;
    private ByteBuffer byteBuffer;

    private Receiver receiver;

    // 记录发送方
    private String sender;

    private boolean isLogin;
    private boolean isConnected;

    private TextField tfText;
    private TextArea taContent;

    private Client(int x, int y, int w, int h) {
        System.out.println("客户端启动中..._(•̀ω•́ 」∠)_");
        initWindow(x, y, w, h);
        registerChannel();
        System.out.println("客户端启动完毕~");
        login();
    }

    private void initWindow(int x, int y, int w, int h) {

        this.tfText = new TextField();
        this.taContent = new TextArea();
        this.setBounds(x, y, w, h);
        this.setLayout(new BorderLayout());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                disConnect();
                System.exit(0);
            }
        });
        this.taContent.setEditable(false);
        this.add(tfText, BorderLayout.SOUTH);
        this.add(taContent, BorderLayout.NORTH);
        this.tfText.addActionListener((actionEvent) -> {
            String str = tfText.getText().trim();
            tfText.setText("");
            sendMessage(str);
        });
        this.pack();
        this.setVisible(true);

    }

    private void registerChannel() {
        try {
            selector = Selector.open();
            // 连到本地
            socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8888));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            byteBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
            // 连接成功
            isConnected = true;
            System.out.println("客户端成功连接服务器");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送信息
     * @param text 用户发送的信息
     */
    private void sendMessage(String text) {

        // 如果没有登录，不能发送消息
        if (!isLogin) {
            JOptionPane.showMessageDialog(null, "您还没有登录，无法发送消息！");
            return;
        }

        Message message;
        // 私聊，以 "@ + 接收方 + :" 的方式
        if (text.startsWith("@")) {
            String[] strings = text.split(":");
            String receiver = strings[0].substring(1);

            message = new Message(
                    MessageHeader.builder()
                            .type(MessageType.PRIVATE)
                            .sender(sender)
                            .receiver(receiver)
                            .timeStamp(System.currentTimeMillis())
                            .build(),
                    strings[1].getBytes(charset)
            );
        } else { // 群聊
            message = new Message(
                    MessageHeader.builder()
                            .type(MessageType.PUBLIC)
                            .sender(sender)
                            .timeStamp(System.currentTimeMillis())
                            .build(),
                    text.getBytes(charset)
            );
        }

        log.info("聊天发送信息" + message.toString());
        writeMessageIntoChannel(message);

    }

    /**
     * 登录
     */
    private void login() {

        String username = JOptionPane.showInputDialog("请输入用户名");
        String password = JOptionPane.showInputDialog("请输入密码");

        Message message = new Message(
                MessageHeader.builder()
                    .type(MessageType.LOGIN)
                    .sender(username)
                    .timeStamp(System.currentTimeMillis())
                    .build(),
                password.getBytes(charset)
        );

        writeMessageIntoChannel(message);
        this.sender = username;
    }

    /**
     * 用户下线
     */
    private void logout() {

        if (!isLogin)
            return;

        System.out.println("客户端发送下线请求");

        Message message = new Message(
                MessageHeader.builder()
                    .type(MessageType.LOGOUT)
                    .sender(sender)
                    .timeStamp(System.currentTimeMillis())
                    .build(),
                null
        );

        writeMessageIntoChannel(message);
        isConnected = false;
    }

    /**
     * 客户端断开连接
     */
    private void disConnect() {

        if (!isConnected)
            return;

        logout();
        try {
            receiver.shutdown();
            // 防止立马断开连接可能导致之前的消息无法送达
            Thread.sleep(10);
            socketChannel.socket().close();
            socketChannel.close();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
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

        private boolean connected = true;

        @Override
        public void run() {
            try {
                while (connected) {
                    int size;
                    // 阻塞式的
                    selector.select();

                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();

                        // 读就绪
                        if (selectionKey.isReadable()) {
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            while ((size = socketChannel.read(byteBuffer)) > 0) {
                                byteBuffer.flip();
                                outputStream.write(byteBuffer.array(), 0, size);
                                byteBuffer.clear();
                            }
                            byte[] bytes = outputStream.toByteArray();
                            outputStream.close();
                            Response response = ProtostuffUtil.deserialize(bytes, Response.class);
                            handleResponse(response);
                        }
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "服务器已关闭，连接已断开，请重新登录");
                isLogin = false;
            }
        }

        /**
         * 处理服务器的响应信息
         * @param response response
         */
        private void handleResponse(Response response) {

            System.out.println(response);
            ResponseHeader header = response.getHeader();

            switch (header.getType()) {
                case PROMPT:
                    if (header.getResponseNum() != null) {
                        ResponseNum num = ResponseNum.getResponseNumFromMap(header.getResponseNum());
                        if (num == ResponseNum.LOGIN_SUCCESS) {
                            isLogin = true;
                            log.info("登录成功");
                        } else if (num == ResponseNum.LOGOUT) {
                            log.info("下线成功");
                            break;
                        }
                    }
                    String info = new String(response.getBody(), charset);
                    JOptionPane.showMessageDialog(Client.this, info);
                    break;
                case MESSAGE:
                    String text = format(taContent.getText(), response);
                    taContent.setText(text);
                    taContent.setCaretPosition(text.length());
                    break;
                default:
                    break;
            }
        }

        private void shutdown() {
            connected = false;
        }
    }

    private String format(String originalText, Response response) {

        ResponseHeader header = response.getHeader();
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(originalText)
                .append(header.getSender())
                .append(": ")
                .append(new String(response.getBody(), charset))
                .append("    ")
                .append(DateUtil.formatLocalDateTime(header.getTimeStamp()))
                .append("\n");

        return stringBuilder.toString();
    }

    /**
     * 启动线程最好作为一个单独的方法
     * 不要再构造方法里启动，因为可能构造未完成就使用了成员变量
     */
    private void startClient() {
        this.receiver = new Receiver();
        new Thread(receiver).start();
    }

    public static void main(String[] args) {
        Client client = new Client(200, 200, 300, 200);
        client.startClient();
    }
}
