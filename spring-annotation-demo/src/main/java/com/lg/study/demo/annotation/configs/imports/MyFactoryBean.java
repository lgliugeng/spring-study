package com.lg.study.demo.annotation.configs.imports;

import com.lg.study.project.entity.Monkey;
import org.springframework.beans.factory.FactoryBean;

public class MyFactoryBean implements FactoryBean<Monkey> {

    @Override
    public Monkey getObject() throws Exception {
        return new Monkey();
    }

    @Override
    public Class<?> getObjectType() {
        return Monkey.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
