package com.lxb.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 封装一个 Message 方便读写
 */
@Builder
@AllArgsConstructor
public class Message {

    private MessageHeader header;

    private byte[] body;
}
