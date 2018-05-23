package com.lxb.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 用于读写的 Message
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Message {

    private MessageHeader header;

    private byte[] body;
}
