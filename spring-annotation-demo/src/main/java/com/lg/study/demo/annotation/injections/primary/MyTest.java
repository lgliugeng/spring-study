package com.lg.study.demo.annotation.injections.primary;

import com.lg.study.project.service.MyService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by lg.
 */
public class MyTest {
    @Test
    public void test(){
        ApplicationContext app = new AnnotationConfigApplicationContext(MyConfig.class);

        MyService service = app.getBean(MyService.class);
        service.print();


    }
}
