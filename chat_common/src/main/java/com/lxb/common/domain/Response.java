package com.lxb.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Response {

    private ResponseHeader header;

    private byte[] body;
}
