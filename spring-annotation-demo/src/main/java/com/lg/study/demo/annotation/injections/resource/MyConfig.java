package com.lg.study.demo.annotation.injections.resource;

import com.lg.study.project.dao.MyDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Tom.
 */
@Configuration
@ComponentScan({
        "com.lg.study.project.controller",
        "com.lg.study.project.service",
        "com.lg.study.project.dao"
            })
public class MyConfig {

    @Bean("dao")
    public MyDao dao(){
        MyDao dao = new MyDao();
        dao.setFlag("4");
        return dao;
    }
}
