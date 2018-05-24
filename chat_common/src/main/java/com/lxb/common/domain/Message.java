package com.lxb.common.domain;

import lombok.*;

/**
 * 用于读写的 Message
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private MessageHeader header;

    private byte[] body;
}
