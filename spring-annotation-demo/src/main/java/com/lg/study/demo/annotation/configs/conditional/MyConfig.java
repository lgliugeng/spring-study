package com.lg.study.demo.annotation.configs.conditional;

import com.lg.study.project.entity.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class MyConfig {


    @Conditional(LinuxCondition.class)
    @Bean
    public Person person() {
        return new Person("lg",18);
    }

    @Conditional(WindowsCondition.class)
    @Bean
    public Person person2() {
        return new Person("lg2",18);
    }
}
