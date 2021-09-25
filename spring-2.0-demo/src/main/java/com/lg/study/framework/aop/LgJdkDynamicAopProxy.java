package com.lg.study.framework.aop;

import com.lg.study.framework.aop.aspect.LgAdvice;
import com.lg.study.framework.aop.support.LgAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 代理类
 * @author lg
 */
public class LgJdkDynamicAopProxy implements InvocationHandler {

    private LgAdvisedSupport config;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Map<String, LgAdvice> advices = config.getAdvices(method,null);

        try {
            //advices.get("before").invoke();
            method.invoke(null,args);
            //advices.get("after").invoke();
        }catch (Exception e) {
            //advices.get("afterThrow").invoke();
        }
        return null;
    }

    public Object getProxy() {
        return null;
    }
}
