package com.lg.study.demo.annotation.injections.value;

import com.lg.study.project.entity.Bird;
import com.lg.study.project.entity.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;

@Configuration
public class MyConfig {
    @Bean
    public Bird bird() {
        System.out.println("将对象添加到IoC容器中");
        return new Bird();
    }
}
