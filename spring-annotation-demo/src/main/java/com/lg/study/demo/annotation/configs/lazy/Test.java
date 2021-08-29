package com.lg.study.demo.annotation.configs.lazy;

import com.lg.study.project.entity.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Test {

    @org.junit.Test
    public void test1() {
        ApplicationContext app = new AnnotationConfigApplicationContext(MyConfig.class);
        System.out.println("IoC容器创建完成");
        // lazy使用时才会加入到容器
        Person person = (Person)app.getBean("person");
        Person person2 = (Person)app.getBean("person");

        System.out.println(person2==person);
        System.out.println(person);
    }
}
