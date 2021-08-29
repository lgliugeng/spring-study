package com.lg.study.demo.annotation.configs.scope;

import com.lg.study.project.entity.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class MyConfig {

    //prototype 多例
    //singleton 单例
    //request 主要应用于web模块，同一次请求只创建一个实例
    //session 主要应用于web模块，同一个session只创建一个实例
    @Scope("prototype")
    @Bean
    public Person person() {
        return new Person("lg",18);
    }
}
