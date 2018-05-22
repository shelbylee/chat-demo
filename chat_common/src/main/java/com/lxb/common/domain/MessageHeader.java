package com.lxb.common.domain;

import com.lxb.common.enumeration.MessageType;
import lombok.Builder;

@Builder
public class MessageHeader {

    // 信息发送方
    private String sender;
    // 信息接收方
    private String receiver;
    // 信息类型
    private MessageType type;
    // 时间戳
    private Long timeStamp;
}
