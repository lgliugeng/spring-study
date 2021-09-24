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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 职责：委派模式，请求分发，任务调度
 * @author lg
 */
public class LgDispatchServlet extends HttpServlet {

    private LgApplicationContext applicationContext;

    private List<LgHandlerMapping> handlerMappings = new ArrayList<>();

    private Map<LgHandlerMapping,LgHandlerAdapter> handlerAdapters = new HashMap<>(16);

    private List<LgViewResolver> viewResolvers = new ArrayList<>();

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

        // 4.把modelAndView对象变成viewResolver
        processDispatchResult(req,resp,modelAndView);
    }

    private LgHandlerAdapter getHandlerAdapter(LgHandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()) {return null;}
        return this.handlerAdapters.get(handler);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, LgModelAndView modelAndView) throws Exception {
        if (modelAndView == null) {return;}
        if (this.viewResolvers.isEmpty()) {return;}

        for (LgViewResolver viewResolver : this.viewResolvers) {
            LgView view = viewResolver.resolverViewName(modelAndView.getViewName());
            // 渲染
            view.render(modelAndView.getModel(),req,resp);
            return;
        }
    }

    private LgHandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) {return null;}
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+","/");
        for (LgHandlerMapping mapping : this.handlerMappings) {
            Matcher matcher = mapping.getUrl().matcher(url);
            if (!matcher.matches()) {continue;}
            return mapping;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 初始化IOC容器
        applicationContext = new LgApplicationContext(config.getInitParameter("contextConfigLocation"));
        //=================MVC部分===================
        //完成了IoC、DI和MVC部分对接

        // 初始化九大组件
        initStrategies(applicationContext);

        System.out.println("Lg Spring framework is init.");

    }

    public void initStrategies(LgApplicationContext context) {
        //多文件上传的组件
//        initMultipartResolver(context);
//        //初始化本地语言环境
//        initLocaleResolver(context);
//        //初始化模板处理器
//        initThemeResolver(context);
        //handlerMapping
        initHandlerMappings(context);
        //初始化参数适配器
        initHandlerAdapters(context);
//        //初始化异常拦截器
//        initHandlerExceptionResolvers(context);
//        //初始化视图预处理器
//        initRequestToViewNameTranslator(context);
        //初始化视图转换器
        initViewResolvers(context);
//        //FlashMap管理器
//        initFlashMapManager(context);
    }

    private void initViewResolvers(LgApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for (File file : templateRootDir.listFiles()) {
            this.viewResolvers.add(new LgViewResolver(templateRoot));
        }
    }

    private void initHandlerAdapters(LgApplicationContext context) {
        for (LgHandlerMapping handler : this.handlerMappings) {
            handlerAdapters.put(handler,new LgHandlerAdapter());
        }
    }

    private void initHandlerMappings(LgApplicationContext context) {
        if (context.getBeanDefinitionCount() == 0) {return;}

        for (String beanName : context.getBeanDefinitionNames()) {
            Object instance = context.getBean(beanName);
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
}
