package com.lg.study.demo.annotation.injections.primary;

import com.lg.study.project.dao.MyDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Created by lg.
 */
@Configuration
@ComponentScan({
        "com.lg.study.project.controller",
        "com.lg.study.project.service",
        "com.lg.study.project.dao"
            })
public class MyConfig {


    @Primary
    @Bean("myDao")
    public MyDao dao(){
        MyDao dao = new MyDao();
        dao.setFlag("9");
        return dao;
    }


    @Bean("myDao")
    public MyDao myDao(){
        MyDao dao = new MyDao();
        dao.setFlag("3");
        return dao;
    }
}
