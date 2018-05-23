package com.lxb.common.enumeration;

public enum  ResponseType {
    MESSAGE(1, "消息"),

    PROMPT(2, "提示");

    private int num;

    private String description;

    ResponseType(int num, String description) {
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
