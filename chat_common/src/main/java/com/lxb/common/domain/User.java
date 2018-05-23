package com.lxb.common.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.nio.channels.SocketChannel;

@Data
@Builder
public class User {

    private String username;

    private String password;

    private SocketChannel channel;
}
