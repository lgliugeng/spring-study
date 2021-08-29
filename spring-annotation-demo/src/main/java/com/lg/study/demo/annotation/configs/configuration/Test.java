package com.lg.study.demo.annotation.configs.configuration;

import com.lg.study.project.entity.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Test {

    @org.junit.Test
    public void test1() {
        ApplicationContext app = new AnnotationConfigApplicationContext(MyConfig.class);
        Person person = (Person)app.getBean("person");
        Person person2 = (Person)app.getBean("person");
        // 证明是在IOC容器中，获取到的是同一个Bean
        System.out.println(person2==person);
        System.out.println(person);
    }

    @org.junit.Test
    public void test2() {
        // 默认使用类名作为beanName
        // 其次使用方放名称
        // 最后优先使用@bean注解声明
        ApplicationContext app = new AnnotationConfigApplicationContext(MyConfig.class);
        Person person = (Person)app.getBean("person1");
        Person person2 = (Person)app.getBean("person1");
        // 证明是在IOC容器中，获取到的是同一个Bean
        System.out.println(person2==person);
        System.out.println(person);
    }
}
