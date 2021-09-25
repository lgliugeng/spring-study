package com.lg.study.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * 通知类（进行代理和调用）
 * @author lg
 */
public class LgAdvice {

    private Object aspect;
    private Method adviceMethod;
    private String throwName;

    public LgAdvice(Object aspect, Method adviceMethod) {
        this.aspect = aspect;
        this.adviceMethod = adviceMethod;
    }

    public Object getAspect() {
        return aspect;
    }

    public void setAspect(Object aspect) {
        this.aspect = aspect;
    }

    public Method getAdviceMethod() {
        return adviceMethod;
    }

    public void setAdviceMethod(Method adviceMethod) {
        this.adviceMethod = adviceMethod;
    }

    public String getThrowName() {
        return throwName;
    }

    public void setThrowName(String throwName) {
        this.throwName = throwName;
    }
}
