package com.lg.study.demo.xml;

import com.lg.study.project.entity.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application.xml");
        Person person = (Person)applicationContext.getBean("person");
        System.out.println(person);
    }
}
