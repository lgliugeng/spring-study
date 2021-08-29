package com.lg.study.demo.annotation.configs.componentscan;

import com.lg.study.project.controller.MyController1;
import com.lg.study.project.entity.Person;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;

@Configuration
@ComponentScan(value = "com.lg.study.project",
                // 通过注解去扫描类
                //includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,value = {Controller.class})},
                // 通过类去扫描类
                //includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,value = {MyController1.class})},
                // 通过自定义类去扫描类
                includeFilters = {@ComponentScan.Filter(type = FilterType.CUSTOM,value = {MyTypeFilter.class})},
                useDefaultFilters = false)
public class MyConfig {
}
