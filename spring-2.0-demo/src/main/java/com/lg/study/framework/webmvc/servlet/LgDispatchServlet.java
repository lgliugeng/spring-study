package com.lg.study.framework.webmvc.servlet;

import com.lg.study.framework.annotation.*;
import com.lg.study.framework.context.LgApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 职责：委派模式，请求分发，任务调度
 * @author lg
 */
public class LgDispatchServlet extends HttpServlet {

    private LgApplicationContext applicationContext;

    private List<LgHandlerMapping> handlerMappings = new ArrayList<>();

    private Map<LgHandlerMapping,LgHandlerAdapter> handlerAdapters = new HashMap<>(16);

    // handlerMapping,key为url,value为方法
    private Map<String, Method> handlerMapping = new HashMap<>(16);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        // 委派模式，将对应的url匹配method方法进行请求并通过response返回
        try {
            doDispatch(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Exception,Detail : " + Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * 进行委派转化请求
     * @param req 参数
     * @param resp 参数
     * @throws Exception 异常
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        LgHandlerMapping handler = getHandler(req);


        if (handler == null) {
            processDispatchResult(req,resp,new LgModelAndView("404"));
            //resp.getWriter().write("404 Not Found!!!");
            return;
        }

        // 2.根据一个handlerMapping获取一个handlerAdapter
        LgHandlerAdapter ha = getHandlerAdapter(handler);

        // 3.解析某一个方法的形参和返回值之后，统一封装成modelAndView对象
        LgModelAndView modelAndView = ha.handler(req,resp,handler);

        processDispatchResult(req,resp,modelAndView);

        /*Method method = handlerMapping.getMethod();
        // 获取请求参数
        Map<String,String[]> paramMap = req.getParameterMap();
        // 获取方法形参列表
        Class<?>[] paramTypes = method.getParameterTypes();
        // 参数值
        Object[] paramValues = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];
            if (HttpServletRequest.class.equals(paramType)) {
                paramValues[i] = req;
            } else if (HttpServletResponse.class.equals(paramType)) {
                paramValues[i] = resp;
            } else if (String.class.equals(paramType)) {
                //通过运行时的状态去拿参数注解
                Annotation[][] annotations = method.getParameterAnnotations();
                for (int i1 = 0; i1 < annotations.length; i1++) {
                    for (Annotation a : annotations[i1]) {
                        if (a instanceof LgRequestParam) {
                            String paramName = ((LgRequestParam) a).value();
                            if (!"".equals(paramName.trim())) {
                                paramValues[i] = Arrays.toString(paramMap.get(paramName))
                                        .replaceAll("\\[|\\]","")
                                        .replaceAll("\\s+",",");
                            }
                        }
                    }
                }
                
            }
        }
        // 获取方法所在类的beanName
        String beanName = toLowerFirstCase(method.getDeclaringClass().getSimpleName());
        // 反射执行方法
        method.invoke(applicationContext.getBean(beanName),paramValues);*/
    }

    private LgHandlerAdapter getHandlerAdapter(LgHandlerMapping handlerMapping) {
        return null;
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, LgModelAndView lgModelAndView) {
    }

    private LgHandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) {return null;}
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String url = uri.replace(contextPath,"").replaceAll("/+","/");
        for (LgHandlerMapping mapping : this.handlerMappings) {
            if (mapping.getUrl().equals(uri)) {
                return mapping;
            }
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 初始化IOC容器
        applicationContext = new LgApplicationContext(config.getInitParameter("contextConfigLocation"));
        //=================MVC部分===================
        initStrategies(applicationContext);
        // 5.初始化HandlerMapping
        doInitHandlerMapping();
        System.out.println("============初始化HandlerMapping完成==============");

        System.out.println("Lg Spring framework is init.");

    }

    public void initStrategies(LgApplicationContext context) {
        //this.initMultipartResolver(context);
        //this.initLocaleResolver(context);
        //this.initThemeResolver(context);
        this.initHandlerMappings(context);
        this.initHandlerAdapters(context);
        //this.initHandlerExceptionResolvers(context);
        //this.initRequestToViewNameTranslator(context);
        this.initViewResolvers(context);
        //this.initFlashMapManager(context);
    }

    private void initViewResolvers(LgApplicationContext context) {
    }

    private void initHandlerAdapters(LgApplicationContext context) {
    }

    private void initHandlerMappings(LgApplicationContext context) {
    }

    /**
     * 初始化HandlerMapping
     */
    private void doInitHandlerMapping() {
        if (this.applicationContext.getBeanDefinitionCount() == 0) {return;}

        for (String beanName : this.applicationContext.getBeanDefinitionNames()) {
            Object instance = this.applicationContext.getBean(beanName);
            Class<?> clazz = instance.getClass();

            // 提取controller注解上的url
            String classUrl = "";
            if (clazz.isAnnotationPresent(LgRequestMapping.class)) {
                LgRequestMapping lgRequestMapping = clazz.getAnnotation(LgRequestMapping.class);
                classUrl = lgRequestMapping.value();
            }

            // 只获取声明注解的方法
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(LgRequestMapping.class)) {continue;}
                // 提取方放上的url
                LgRequestMapping lgRequestMapping = method.getAnnotation(LgRequestMapping.class);
                // 保证url为/xxx/xxx
                String regex = String.format("/%s/%s",classUrl,lgRequestMapping.value().replaceAll("\\*",".*")).replaceAll("/+","/");
                Pattern pattern = Pattern.compile(regex);
                // 匹配url和方放进行反射调用
                //handlerMapping.put(url,method);
                handlerMappings.add(new LgHandlerMapping(pattern,method,instance));
                System.out.println(String.format("Mapping:%s------->%s",regex,method));
            }
        }
    }

    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return new String(chars);
    }
}
