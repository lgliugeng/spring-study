package com.lg.study.framework.webmvc.servlet;

import com.lg.study.framework.annotation.LgRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LgHandlerAdapter {

    public LgModelAndView handler(HttpServletRequest req, HttpServletResponse resp, LgHandlerMapping handler) throws Exception {
        // 保存形参列表的位置
        Map<String,Integer> paramIndexMapping = new HashMap<>(16);

        //通过运行时的状态去拿参数注解
        Annotation[][] annotations = handler.getMethod().getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            for (Annotation a : annotations[i]) {
                if (a instanceof LgRequestParam) {
                    String paramName = ((LgRequestParam) a).value();
                    if (!"".equals(paramName.trim())) {
                        /*paramValues[i] = Arrays.toString(paramMap.get(paramName))
                                .replaceAll("\\[|\\]","")
                                .replaceAll("\\s+",",");*/
                        paramIndexMapping.put(paramName,i);
                    }
                }
            }
        }

        // 获取方法形参列表
        Class<?>[] paramTypes = handler.getMethod().getParameterTypes();

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];
            // 记录req和resp位置
            if (HttpServletRequest.class.equals(paramType) || HttpServletResponse.class.equals(paramType)) {
                paramIndexMapping.put(paramType.getName(),i);
            }
        }

        // 拼接实参列表
        Map<String,String[]> params = req.getParameterMap();
        Object[] paramValues = new Object[paramTypes.length];

        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(params.get(param.getKey()))
                    .replaceAll("\\[|\\]","")
                    .replaceAll("\\s+",",");
            if (!paramIndexMapping.containsKey(param.getKey())) {continue;}

            int index = paramIndexMapping.get(param.getKey());

            // 自定义转换器
            paramValues[index] = castStringValue(value,paramTypes[index]);
        }
        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int index = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = req;
        }
        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int index = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = resp;
        }
        Object result = handler.getMethod().invoke(handler.getController(),paramValues);
        if (result == null || result instanceof Void) {return null;}

        boolean isModelAndView = handler.getMethod().getReturnType() == LgModelAndView.class;
        if (isModelAndView) {
            return (LgModelAndView)result;
        }
        return null;
    }

    private Object castStringValue(String value, Class<?> paramType) {
        if (String.class == paramType) {
            return value;
        } else if (Integer.class == paramType) {
            return Integer.valueOf(value);
        } else if (Double.class == paramType) {
            return Double.valueOf(value);
        }
        return value;
    }
}
