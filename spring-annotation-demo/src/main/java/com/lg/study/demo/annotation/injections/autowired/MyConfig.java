package com.lg.study.demo.annotation.injections.autowired;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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

}
