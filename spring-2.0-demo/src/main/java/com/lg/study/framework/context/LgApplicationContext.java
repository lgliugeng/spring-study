package com.lg.study.framework.context;

import com.lg.study.framework.beans.support.LgBeanDefinitionReader;

/**
 * 职责：进行Bean创建和DI注入
 * @author lg
 */
public class LgApplicationContext {

    private String[] contextConfigLocations;

    private LgBeanDefinitionReader reader;

    public LgApplicationContext(String...contextConfigLocations) {
        this.contextConfigLocations = contextConfigLocations;
        // 1.加载配置
        reader = new LgBeanDefinitionReader();

        // 2.解析配置,封装成BeanDefinition
        loadBeanDefinitions();

        // 3.缓存BeanDefinition
        doRegisterBeanDefinition();

        // 4.DI注入
        doAutowired();
    }

    public Object getBean(String beanName) {
        return null;
    }

    public Object getBean(Class<?> beanClass) {
        return null;
    }
}
