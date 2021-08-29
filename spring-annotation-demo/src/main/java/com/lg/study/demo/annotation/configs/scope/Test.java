package com.lg.study.demo.annotation.configs.scope;

import com.lg.study.project.entity.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Test {

    @org.junit.Test
    public void test1() {
        ApplicationContext app = new AnnotationConfigApplicationContext(MyConfig.class);
        Person person = (Person)app.getBean("person");
        Person person2 = (Person)app.getBean("person");
        // 证明是在IOC容器中，获取到的是同一个Bean,使用@scope时根据scope返回
        System.out.println(person2==person);
        System.out.println(person);
    }
}
