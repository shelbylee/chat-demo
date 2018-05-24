package com.lxb.chat.server.prompt;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class PromptConstants {

    public static final String LOGIN_SUCCESS = "登陆成功！当前在线用户 %d 人";

    public static final String LOGIN_BROADCAST = "用户 %s 已上线";

    public static final String LOGIN_FAILURE = "登录失败，请检查您的用户名或密码，请不要重复登录";

    public static final String LOGOUT_SUCCESS = "下线成功";

    public static final String LOGOUT_BROADCAST = "用户 %s 已下线";

    public static final String SERVER_ERROR = "抱歉，服务器内部出现错误，请稍后再尝试";

    public static final Charset CHARSET = StandardCharsets.UTF_8;
}
