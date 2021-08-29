package com.lg.study.demo.annotation.configs.componentscan;

import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.util.Set;

public class MyTypeFilter implements TypeFilter {

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {

        // 获取当前类的所有注解信息
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        // 获取当前扫描到的类信息
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        // 获取当前类的所有资源
        Resource resource = metadataReader.getResource();
        Set<String> stringSet = annotationMetadata.getAnnotationTypes();
        String className = classMetadata.getClassName();
        String desc = resource.getDescription();
        System.out.println("--------Annotation"+stringSet+"-------");
        System.out.println("--------ClassName"+className+"-------");
        System.out.println("--------Desc"+desc+"-------");
        if (className.contains("son")) {return true;}
        return false;
    }
}
