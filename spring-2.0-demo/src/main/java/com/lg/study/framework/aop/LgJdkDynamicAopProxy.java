package com.lg.study.framework.aop;

import com.lg.study.framework.aop.aspect.LgAdvice;
import com.lg.study.framework.aop.support.LgAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 代理类
 * @author lg
 */
public class LgJdkDynamicAopProxy implements InvocationHandler {

    private LgAdvisedSupport config;

    public LgJdkDynamicAopProxy(LgAdvisedSupport config) {
        this.config = config;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Map<String, LgAdvice> advices = config.getAdvices(method,null);
        Object returnObj;
        try {
            invokeAdvice(advices.get("before"));

            returnObj = method.invoke(this.config.getTarget(),args);

            invokeAdvice(advices.get("after"));
        }catch (Exception e) {
            invokeAdvice(advices.get("afterThrow"));
            throw e;
        }
        return returnObj;
    }

    private void invokeAdvice(LgAdvice advice) {
        try {
            advice.getAdviceMethod().invoke(advice.getAspect());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Object getProxy() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),this.config.getTargetClass().getInterfaces(),this);
    }
}
