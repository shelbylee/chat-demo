package com.lxb.common.domain;

import lombok.Builder;

import java.nio.channels.SocketChannel;

@Builder
public class User {

    private String username;

    private String password;

    private SocketChannel channel;
}
