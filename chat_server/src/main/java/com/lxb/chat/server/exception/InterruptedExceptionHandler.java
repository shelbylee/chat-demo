package com.lxb.chat.server.exception;

import com.lxb.chat.server.prompt.PromptConstants;
import com.lxb.common.domain.Message;
import com.lxb.common.domain.Response;
import com.lxb.common.domain.ResponseHeader;
import com.lxb.common.enumeration.ResponseType;
import com.lxb.common.util.ProtostuffUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

@Component("interruptedExceptionHandler")
public class InterruptedExceptionHandler {

    public void handle(SocketChannel channel, Message message) {

        byte[] response = ProtostuffUtil.serialize(
                new Response(
                        ResponseHeader.builder()
                            .type(ResponseType.PROMPT)
                            .sender(message.getHeader().getSender())
                            .timeStamp(message.getHeader().getTimeStamp()).build(),
                        PromptConstants.SERVER_ERROR.getBytes(PromptConstants.CHARSET)
                )
        );

        try {
            channel.write(ByteBuffer.wrap(response));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
