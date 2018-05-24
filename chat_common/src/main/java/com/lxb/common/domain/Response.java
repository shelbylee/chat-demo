package com.lxb.common.domain;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response {

    private ResponseHeader header;

    private byte[] body;
}
