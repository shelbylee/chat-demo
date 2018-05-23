package com.lxb.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 用于读写的 Message
 */
@Builder
@AllArgsConstructor
public class Message {

    private MessageHeader header;

    private byte[] body;
}
