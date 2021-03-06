package com.lg.study.framework.webmvc.servlet;

import java.io.File;

public class LgViewResolver {

    private static final String DEFAULT_TEMPLATE_SUFFIX = ".html";
    private File templateRootDir;

    public LgViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir = new File(templateRootPath);
    }

    public LgView resolverViewName(String viewName) {
        if (viewName == null || "".equals(viewName.trim())) {return null;}
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX)?viewName:viewName+DEFAULT_TEMPLATE_SUFFIX;
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+","/"));
        return new LgView(templateFile);
    }
}
