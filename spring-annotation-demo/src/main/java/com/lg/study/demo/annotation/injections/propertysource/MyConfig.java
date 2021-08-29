package com.lg.study.demo.annotation.injections.propertysource;

import com.lg.study.project.entity.Bird;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by lg.
 */
@Configuration
@PropertySource("classpath:values.properties")
public class MyConfig {

    @Bean
    public Bird bird(){
        return new Bird();
    }
}
