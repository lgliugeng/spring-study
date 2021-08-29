package com.lg.study.demo.annotation.configs.configuration;

import com.lg.study.project.entity.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfig {

    @Bean
    public Person person() {
        return new Person("lg",18);
    }

    @Bean(value = "person1")
    public Person person1() {
        return new Person("lg",10);
    }
}
