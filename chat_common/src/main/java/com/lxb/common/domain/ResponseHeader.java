package com.lxb.common.domain;

import com.lxb.common.enumeration.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class ResponseHeader {

    private String sender;

    private String reveiver;

    private ResponseType type;

    private Long timeStamp;
}
