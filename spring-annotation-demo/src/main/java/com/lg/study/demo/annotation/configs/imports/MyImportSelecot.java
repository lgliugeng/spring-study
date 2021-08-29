package com.lg.study.demo.annotation.configs.imports;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class MyImportSelecot implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return  new String[]{"com.lg.study.project.entity.Company",
                "com.lg.study.project.entity.Member"};
    }
}
