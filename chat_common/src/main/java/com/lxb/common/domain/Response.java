package com.lxb.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class Response {

    private ResponseHeader header;

    private byte[] body;
}
