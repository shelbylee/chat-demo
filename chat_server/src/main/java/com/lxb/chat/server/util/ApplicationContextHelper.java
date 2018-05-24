package com.lxb.chat.server.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component("applicationContextHelper")
public class ApplicationContextHelper {

    private static ApplicationContext applicationContext;

    static {
        applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    public static <T> T popBean(String name) {
        T bean = null;
        try {
            if (StringUtils.isNoneEmpty(StringUtils.trim(name)))
                bean = (T) applicationContext.getBean(name);
        } catch (NoSuchBeanDefinitionException e) {
            log.error("获取 bean 失败");
            return null;
        }
        return bean;
    }

    public static <T> T popBean(String...name) {

        StringBuilder stringBuilder;

        stringBuilder = new StringBuilder();
        int len = name.length;
        for (int i = 0; i < len; i++) {
            stringBuilder.append(name[i]);
            if (i != len - 1) {
                stringBuilder.append(".");
            }
        }

        return popBean(stringBuilder.toString());
    }
}
