package com.lg.study.demo.annotation.configs.lifecycle;

import com.lg.study.project.entity.Car;
import com.lg.study.project.entity.Cat;
import com.lg.study.project.entity.Person;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScans({
        @ComponentScan("com.lg.study.project.entity"),
        @ComponentScan("com.lg.study.demo.annotation.configs.lifecycle")
})
public class MyConfig {

    //3种方式
    // 1.添加initMethod 和 destroyMethod
    // 2.实现InitializingBean和DisposableBean接口
    // 3.使用@PostConstruct和@PreDestroy注解
    // 4.自定义BeanPostProcessor类
    @Bean(initMethod = "addOil",destroyMethod = "close")
    public Car car() {
        System.out.println("将对象添加到IoC容器中");
        return new Car();
    }
}
