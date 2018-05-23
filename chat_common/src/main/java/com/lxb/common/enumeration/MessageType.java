package com.lxb.common.enumeration;

public enum MessageType {

    LOG_IN(1, "登录"),

    LOG_OUT(2, "下线"),

    PRIVATE_CHAT(3, "私聊"),

    PUBLIC_CHAT(4, "群聊");

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
