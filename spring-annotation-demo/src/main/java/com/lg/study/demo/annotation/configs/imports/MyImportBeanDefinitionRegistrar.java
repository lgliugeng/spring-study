package com.lg.study.demo.annotation.configs.imports;

import com.lg.study.project.entity.Company;
import com.lg.study.project.entity.User;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        boolean company = beanDefinitionRegistry.containsBeanDefinition("com.lg.study.project.entity.Company");
        boolean member = beanDefinitionRegistry.containsBeanDefinition("com.lg.study.project.entity.Member");
        if (company&& company) {
            BeanDefinition beanDefinition = new RootBeanDefinition(User.class);
            beanDefinitionRegistry.registerBeanDefinition("user",beanDefinition);
        }
    }
}
