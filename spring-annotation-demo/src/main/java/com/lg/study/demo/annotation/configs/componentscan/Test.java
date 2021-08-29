package com.lg.study.demo.annotation.configs.componentscan;

import com.lg.study.project.entity.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

public class Test {

    @org.junit.Test
    public void test1() {
        ApplicationContext app = new AnnotationConfigApplicationContext(MyConfig.class);
        String[] beanNames = app.getBeanDefinitionNames();

        System.out.println(Arrays.toString(beanNames)
                .replaceAll("\\[|\\]","")
                .replaceAll(", ","\n"));
    }
}
