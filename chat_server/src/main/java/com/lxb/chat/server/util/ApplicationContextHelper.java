package com.lxb.chat.server.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Slf4j
@Component("applicationContextHelper")
public class ApplicationContextHelper implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    public static <T> T popBean(String name) {
        if (applicationContext == null) {
            return null;
        }
        else {
            return (T)applicationContext.getBean(name);
        }
    }

    public static <T> T popBean(String...name) {

        StringBuilder stringBuilder;

        if (applicationContext == null) {
            return null;
        } else {
            stringBuilder = new StringBuilder();
            int len = name.length;
            for (int i = 0; i < len; i++) {
                stringBuilder.append(name[i]);
                if (i != len - 1) {
                    stringBuilder.append(".");
                }
            }
        }

        return popBean(stringBuilder.toString());
    }
}
