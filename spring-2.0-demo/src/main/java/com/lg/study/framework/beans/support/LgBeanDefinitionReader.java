package com.lg.study.framework.beans.support;

import com.lg.study.framework.annotation.LgController;
import com.lg.study.framework.annotation.LgService;
import com.lg.study.framework.beans.config.LgBeanDefinition;
import com.sun.org.apache.regexp.internal.RE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class LgBeanDefinitionReader {

    private Properties contextConfig = new Properties();

    private List<String> registryBeanClasses = new ArrayList<>();

    public LgBeanDefinitionReader(String...contextConfigLocation) {
        doLoadConfig(contextConfigLocation[0]);

        // 扫描配置文件中配置的类路径
        doScanner(contextConfig.getProperty("scanPackage"));
    }

    public List<LgBeanDefinition> loadBeanDefinitions() {
        List<LgBeanDefinition> result = new ArrayList<>();
        for (String className : registryBeanClasses) {
            try {
                Class<?> beanClass = Class.forName(className);
                // 如果是接口不能实例化由子类完成
                if (beanClass.isInterface()) {continue;}
                // 保存对应的className以及beanName
                // 1.在自定义名称中，必须全局唯一
                String beanName = getBeanNameByCustomer(beanClass);
                if ("".equals(beanName.trim())) {
                    // 2.默认名称时小写
                    beanName = toLowerFirstCase(beanClass.getSimpleName());
                }
                result.add(doCreateBeanDefinition(beanName,beanClass.getName()));
                // 3.接口注入
                for (Class<?> i : beanClass.getInterfaces()) {
                    result.add(doCreateBeanDefinition(i.getName(),beanClass.getName()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private String getBeanNameByCustomer(Class<?> beanClass) {
        String beanName = "";
        if (beanClass.isAnnotationPresent(LgController.class)) {
            beanName = beanClass.getAnnotation(LgController.class).value();
        } else if (beanClass.isAnnotationPresent(LgService.class)){
            beanName =  beanClass.getAnnotation(LgService.class).value();
        }
        return beanName;
    }

    private LgBeanDefinition doCreateBeanDefinition(String beanName, String beanClassName) {
        LgBeanDefinition lgBeanDefinition = new LgBeanDefinition();
        lgBeanDefinition.setFactoryBeanName(beanName);
        lgBeanDefinition.setBeanClassName(beanClassName);
        return lgBeanDefinition;
    }

    private void doLoadConfig(String contextConfigLocation) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation.replaceAll("classpath:",""));
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 类扫描
     * @param scanPackage 包路径
     */
    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource(String.format("/%s",scanPackage.replaceAll("\\.","/")));
        File classPath = new File(url.getFile());

        // 文件夹获取所有类
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                // 递归
                doScanner(String.format("%s.%s",scanPackage,file.getName()));
            } else {
                // class文件才进行保存
                if (!file.getName().endsWith(".class")) {continue;}
                String className = String.format("%s.%s",scanPackage,file.getName().replace(".class",""));
                // className可通过Class.forName进行实例化
                registryBeanClasses.add(className);
            }

        }
    }

    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return new String(chars);
    }
}
