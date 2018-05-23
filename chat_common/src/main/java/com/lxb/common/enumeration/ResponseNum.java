package com.lxb.common.enumeration;

import java.util.HashMap;
import java.util.Map;

public enum ResponseNum {

    LOGIN_SUCCESS(1, "登录成功"),

    LOGIN_FAILURE(3, "登录失败"),

    LOGOUT(3, "下线成功");

    private int num;

    private String description;

    private static Map<Integer, ResponseNum> map = new HashMap<>();

    ResponseNum(int num, String description) {
        this.num = num;
        this.description = description;
    }

    static {
        for (ResponseNum num : values()) {
            map.put(num.getNum(), num);
        }
    }

    public static ResponseNum getResponseNumFromMap(int num) {
        return map.get(num);
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
