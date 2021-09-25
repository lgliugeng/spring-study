package com.lg.study.framework.context;

import com.lg.study.framework.annotation.LgAutowired;
import com.lg.study.framework.annotation.LgController;
import com.lg.study.framework.annotation.LgService;
import com.lg.study.framework.aop.LgJdkDynamicAopProxy;
import com.lg.study.framework.aop.config.LgAopConfig;
import com.lg.study.framework.aop.support.LgAdvisedSupport;
import com.lg.study.framework.beans.LgBeanWrapper;
import com.lg.study.framework.beans.config.LgBeanDefinition;
import com.lg.study.framework.beans.support.LgBeanDefinitionReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 职责：进行Bean创建和DI注入
 * @author lg
 */
public class LgApplicationContext {

    private LgBeanDefinitionReader reader;

    private Map<String, LgBeanDefinition> beanDefinitionMap = new HashMap<>(16);

    private Map<String, LgBeanWrapper> factoryBeanInstanceCache = new HashMap<>(16);

    private Map<String,Object> factoryBeanObjectCache = new HashMap<>(16);

    public LgApplicationContext(String...configLocations) {
        // 1.加载配置
        reader = new LgBeanDefinitionReader(configLocations);

        // 2.解析配置,封装成BeanDefinition
        List<LgBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        try {
            // 3.缓存BeanDefinition
            doRegisterBeanDefinition(beanDefinitions);

            // 4.DI注入
            doAutowired();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void doRegisterBeanDefinition(List<LgBeanDefinition> beanDefinitions) throws Exception {
        for (LgBeanDefinition beanDefinition : beanDefinitions) {
            if (this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The" + beanDefinition.getFactoryBeanName() + "is exits");
            }
            beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
            beanDefinitionMap.put(beanDefinition.getBeanClassName(),beanDefinition);
        }
    }

    private void doAutowired() {
        // getBean进行实例化
        for (Map.Entry<String, LgBeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            getBean(beanName);
        }
    }

    /**
     * bean的实例化，DI从此开始
     * @param beanName bean名称
     * @return 结果
     */
    public Object getBean(String beanName) {
        // 1.先拿到beanDefinition配置信息
        LgBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        // 2.反射实例化newInstance()
        Object instance = instantiateBean(beanName,beanDefinition);
        // 3.封装成BeanWrapper对象
        LgBeanWrapper beanWrapper = new LgBeanWrapper(instance);
        // 4.保存到IOC容器
        factoryBeanInstanceCache.put(beanName,beanWrapper);
        // 5.执行依赖注入
        populateBean(beanName,beanDefinition,beanWrapper);
        return beanWrapper.getWrapperInstance();
    }

    private void populateBean(String beanName, LgBeanDefinition beanDefinition, LgBeanWrapper beanWrapper) {
        // 循环依赖
        // 使用两个缓存处理
        //1、把第一次读取结果为空的BeanDefinition存到第一个缓存
        //2、等第一次循环之后，第二次循环再检查第一次的缓存，再进行赋值
        Object instance = beanWrapper.getWrapperInstance();
        Class<?> clazz = beanWrapper.getWrapperClass();
        if (!(clazz.isAnnotationPresent(LgController.class) || clazz.isAnnotationPresent(LgService.class))) {return;}
        // 把所有的包括private/protected/default/public 修饰字段都取出来
        for (Field field : clazz.getDeclaredFields()) {
            // 没有注解不进行注入
            if (!field.isAnnotationPresent(LgAutowired.class)) {continue;}
            LgAutowired autowired = field.getAnnotation(LgAutowired.class);
            // 获取注解自定义beanName
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName.trim())) {
                // 通过类型去获取beanName
                autowiredBeanName = field.getType().getName();
            }
            // 暴力访问
            field.setAccessible(true);
            try {
                if (this.factoryBeanInstanceCache.get(autowiredBeanName) == null) {continue;}
                // 当前实例根据beanName进行注入
                field.set(instance,this.factoryBeanInstanceCache.get(autowiredBeanName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建实例
     * @param beanName bean名称
     * @param beanDefinition bean配置
     * @return 实例
     */
    private Object instantiateBean(String beanName, LgBeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();
        Object instance = null;
        try {
            if (factoryBeanObjectCache.containsKey(beanName)){
                instance = factoryBeanObjectCache.get(beanName);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                // ===============AOP开始=======================
                // 如果满足条件，则返回proxy对象
                // 1、加载AOP配置文件
                LgAdvisedSupport config = instantionAopConfig(beanDefinition);
                config.setTargetClass(clazz);
                config.setTarget(instance);

                // 2、判断规则是否生成代理类覆盖原生对象，生成代理类则覆盖原生对象
                if (config.pointCutMatch()) {
                    instance = new LgJdkDynamicAopProxy().getProxy();
                }


                // ===============AOP结束=======================

                factoryBeanObjectCache.put(beanName,instance);
                factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(),instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    private LgAdvisedSupport instantionAopConfig(LgBeanDefinition beanDefinition) {
        LgAopConfig config = new LgAopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new LgAdvisedSupport(config);
    }

    public Object getBean(Class<?> beanClass) {
        return getBean(beanClass.getName());
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public Properties getConfig() {
        return reader.getConfig();
    }
}
