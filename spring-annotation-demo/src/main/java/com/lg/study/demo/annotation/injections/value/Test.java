package com.lg.study.demo.annotation.injections.value;

import com.lg.study.project.entity.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Arrays;

public class Test {

    @org.junit.Test
    public void test1() {
        ApplicationContext app = new AnnotationConfigApplicationContext(MyConfig.class);
        System.out.println(app.getBean("bird"));

        String [] beanNames = app.getBeanDefinitionNames();
        System.out.println(Arrays.toString(beanNames)
                .replaceAll("\\[|\\]","")
                .replaceAll(", ","\n"));
    }
}
