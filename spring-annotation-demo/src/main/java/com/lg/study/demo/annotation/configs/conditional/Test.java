package com.lg.study.demo.annotation.configs.conditional;

import com.lg.study.project.entity.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;

public class Test {

    @org.junit.Test
    public void test1() {
        ApplicationContext app = new AnnotationConfigApplicationContext(MyConfig.class);
        Environment environment = app.getEnvironment();
        String os = environment.getProperty("os.name");
        // 不同条件加载不同类
        if (os.contains("Linux")) {
            Person person = (Person)app.getBean("person");
            System.out.println("Linux:"+person);
        } else {
            Person person = (Person)app.getBean("person2");
            System.out.println("Windows:"+person);
        }
    }
}
