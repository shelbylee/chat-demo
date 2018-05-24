package com.lxb.chat.server;

import com.lxb.chat.server.exception.InterruptedExceptionHandler;
import com.lxb.chat.server.handler.message.MessageHandler;
import com.lxb.chat.server.util.ApplicationContextHelper;
import com.lxb.common.domain.Message;
import com.lxb.common.domain.MessageHeader;
import com.lxb.common.util.ProtostuffUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Server {

    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final int PORT = 8888;

    private AtomicInteger usersOnline;

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    private ExecutorService threadPool;

    private InterruptedExceptionHandler exceptionHandler;

    private Listener listener;

    private Server() {
        System.out.println("服务器启动中..._(•̀ω•́ 」∠)_");
        init();
        System.out.println("服务器启动完毕~");
    }

    private void init() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.threadPool = new ThreadPoolExecutor(5, 10, 1000,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy());
        this.usersOnline = new AtomicInteger(0);
        this.exceptionHandler = ApplicationContextHelper.popBean("interruptedExceptionHandler");
        this.listener = new Listener();
    }

    /**
     * 侦听线程
     * 负责监听客户端的请求
     *
     * 需要注意的是：
     * 如果要中断一个阻塞的某个地方的线程，这个线程最好是用继承 Thread 的方式创建，
     * 并且先关闭所依赖的资源（selector），再关闭当前线程。
     */
    private class Listener extends Thread {
        @Override
        public void run() {
            try {
                // 在 while 中检查是否中断
                while (!Thread.currentThread().isInterrupted()) {
                    // 阻塞式
                    selector.select();

                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        // 连接就绪
                        if (key.isAcceptable()) {
                            handleAcceptRequest();
                        } else if (key.isReadable()) { // 读就绪
                            // 取消可读触发标记，本次处理完再打开
                            key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
                            threadPool.execute(new Reader(key));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 结束线程最好用中断的方式，不要在外部中断线程
         */
        @Override
        public void interrupt() {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                super.interrupt();
            }
        }

        void shutdown() {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 处理客户端的连接请求
     */
    private void handleAcceptRequest() {
        try {
            SocketChannel client = serverSocketChannel.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            log.info("服务器连接客户端：{}", client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭服务器
     */
    private void stopServer() {
        try {
            listener.shutdown();
            threadPool.shutdown();
            serverSocketChannel.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取事件的线程
     */
    private class Reader implements Runnable {

        private SelectionKey key;
        private SocketChannel clientChannel;
        private ByteBuffer byteBuffer;
        private ByteArrayOutputStream outputStream;

        /**
         * 根据获取的 SelectionKey 构建一个 Reader 线程
         * @param key 获取到的 SelectionKey
         */
        Reader(SelectionKey key) {
            this.key = key;
            this.clientChannel = (SocketChannel) key.channel();
            this.byteBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
            this.outputStream = new ByteArrayOutputStream();
        }

        @Override
        public void run() {
            int size;
            try {
                while ((size = clientChannel.read(byteBuffer)) > 0) {
                    byteBuffer.flip();
                    outputStream.write(byteBuffer.array(), 0, size);
                    byteBuffer.clear();
                }

                if (size == -1)
                    return;

                log.info("服务器读取完毕，继续监听客户端");

                key.interestOps(key.interestOps() | SelectionKey.OP_READ);
                key.selector().wakeup();

                byte[] bytes = outputStream.toByteArray();
                outputStream.close();

                Message message = ProtostuffUtil.deserialize(bytes, Message.class);
                MessageHandler messageHandler = ApplicationContextHelper.popBean("MessageHandler",
                        message.getHeader()
                            .getType()
                            .toString()
                            .toLowerCase());

                try {
                    messageHandler.handle(message,
                            selector,
                            key,
                            usersOnline);
                } catch (InterruptedException e) {
                    log.error("服务器线程被中断");
                    exceptionHandler.handle(clientChannel, message);
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void startServer() {
        new Thread(listener).start();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();

        Scanner scanner = new Scanner(System.in, "UTF-8");
        while (scanner.hasNext()) {
            String next = scanner.next();
            if (next.equalsIgnoreCase("QUIT")) {
                server.stopServer();
                System.out.println("服务器已关闭 _(:з」∠)_");
            }
        }
    }
}
