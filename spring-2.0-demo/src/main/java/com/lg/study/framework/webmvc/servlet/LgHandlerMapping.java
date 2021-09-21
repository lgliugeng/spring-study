package com.lg.study.framework.webmvc.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class LgHandlerMapping {

    // 路径
    private Pattern url;
    // 方法
    private Method method;
    // 方法对应实例
    private Object controller;

    public LgHandlerMapping(Pattern url, Method method, Object controller) {
        this.url = url;
        this.method = method;
        this.controller = controller;
    }

    public Pattern getUrl() {
        return url;
    }

    public void setUrl(Pattern url) {
        this.url = url;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }
}
