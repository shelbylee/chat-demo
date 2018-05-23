package com.lxb.common.domain;

import com.lxb.common.enumeration.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ResponseHeader {

    private String sender;

    private Integer responseNum;

    private ResponseType type;

    private Long timeStamp;
}
