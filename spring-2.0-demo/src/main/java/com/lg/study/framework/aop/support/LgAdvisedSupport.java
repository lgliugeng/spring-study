package com.lg.study.framework.aop.support;

import com.lg.study.framework.aop.aspect.LgAdvice;
import com.lg.study.framework.aop.config.LgAopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 读取aop配置并解析成aopConfig
 * @author lg
 */
public class LgAdvisedSupport {

    private LgAopConfig config;
    private Object target;
    private Class<?> targetClass;
    private Pattern pointCutClassPattern;

    private Map<Method,Map<String, LgAdvice>> methodCache;

    public LgAdvisedSupport(LgAopConfig config) {
        this.config = config;
    }

    /**
     * 获取到类对象时进行解析
     */
    private void parse() {
        // 正则解析
        // 替换成正则可识别的符号
        String pointCut = config.getPointCut()
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");

        // 三段
        // 1、第一段:方法的修饰符和返回值
        // 2、第二段：类名
        // 3、第三段：方法名称形参列表

        // 截取public .* com.lg.study.demo.service..*Service..*
        String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("\\(")-4);
        // 保存专门匹配Class的正则
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" ") + 1));

        // 享元的共享池
        methodCache = new HashMap<Method, Map<String, LgAdvice>>();
        Pattern pointCutPatter = Pattern.compile(pointCut);

        // 建立methodCache关系
        try {
            // 获取切面类
            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String,Method> aspectMethods = new HashMap<>(16);
            // 记录所有类方法
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(),method);
            }

            // 开始封装Advice
            for (Method method : this.targetClass.getMethods()) {
                String methodStr = method.toString();
                if (methodStr.contains("throws")) {
                    methodStr = methodStr.substring(0,methodStr.lastIndexOf("throws")).trim();
                }
                Matcher matcher = pointCutPatter.matcher(methodStr);
                if (matcher.matches()) {
                    Map<String,LgAdvice> advices = new HashMap<>(16);
                    if (null == config.getAspectBefore() || "".equals(config.getAspectBefore())) {
                        advices.put("before",new LgAdvice(aspectClass.newInstance(),aspectMethods.get(config.getAspectBefore())));
                    }
                    if (null == config.getAspectAfter() || "".equals(config.getAspectAfter())) {
                        advices.put("after",new LgAdvice(aspectClass.newInstance(),aspectMethods.get(config.getAspectAfter())));
                    }
                    if (null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow())) {
                        LgAdvice advice = new LgAdvice(aspectClass.newInstance(),aspectMethods.get(config.getAspectAfterThrow()));
                        advice.setThrowName(config.getAspectAfterThrowingName());
                        advices.put("afterThrow",advice);
                    }
                    methodCache.put(method,advices);
                }
            }
        }catch (Exception e) {

        }
    }

    public Map<String, LgAdvice> getAdvices(Method method, Object o) throws Exception {
        Map<String, LgAdvice> cache = methodCache.get(method);
        if (null == cache) {
            Method m = this.targetClass.getMethod(method.getName(),method.getParameterTypes());
            cache = methodCache.get(m);
            methodCache.put(m,cache);
        }
        return cache;
    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

    public void setTargetClass(Class<?> clazz) {
        this.targetClass = clazz;
        parse();
    }

    public void setTarget(Object instance) {
        this.target = instance;
    }

    public Object getTarget() {
        return target;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }
}
