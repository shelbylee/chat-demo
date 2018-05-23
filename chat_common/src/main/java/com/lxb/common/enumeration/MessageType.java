package com.lxb.common.enumeration;

public enum MessageType {

    LOGIN(1, "登录"),

    LOGOUT(2, "下线"),

    PRIVATE(3, "私聊"),

    PUBLIC(4, "群聊");

    private int num;

    private String description;

    MessageType(int num, String description) {
        this.num = num;
        this.description = description;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
