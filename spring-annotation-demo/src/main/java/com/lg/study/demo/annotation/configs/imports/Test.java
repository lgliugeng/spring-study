package com.lg.study.demo.annotation.configs.imports;

import com.lg.study.project.entity.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

public class Test {

    @org.junit.Test
    public void test1() {
        ApplicationContext app = new AnnotationConfigApplicationContext(MyConfig.class);
        System.out.println("IoC容器创建完成");

        //通过FactoryBean注入的值
        System.out.println("============Class:"+app.getBean("monkey").getClass());

        Object monkey1 = app.getBean("monkey");
        Object monkey2 = app.getBean("monkey");
        System.out.println("是否单例：" + monkey1 == monkey2);

        //拿到构建monkey的FactoryBean
        Object monkeyFactoryBean = app.getBean("&monkey");
        System.out.println(monkeyFactoryBean);


        String[] beanNames = app.getBeanDefinitionNames();

        System.out.println(Arrays.toString(beanNames)
                .replaceAll("\\[|\\]","")
                .replaceAll(", ","\n"));
    }
}
