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

/**
 * 职责：委派模式，请求分发，任务调度
 * @author lg
 */
public class LgDispatchServlet extends HttpServlet {

    private LgApplicationContext lgApplicationContext;

    private Properties contextConfig = new Properties();

    // 享元模式，保存所有扫描的类
    private List<String> classNames = new ArrayList<>(16);

    // IOC容器,key为首字符小写的类名，value为实例
    private Map<String,Object> IOC = new HashMap<>(16);

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
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String url = uri.replace(contextPath,"").replaceAll("/+","/");

        if (!this.handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 Not Found!!!");
            return;
        }

        Method method = this.handlerMapping.get(url);
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
        method.invoke(IOC.get(beanName),paramValues);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 初始化IOC容器
        lgApplicationContext = new LgApplicationContext();
        // 1.加载配置文件
        doLoadConfig(config.getInitParameter("contentConfigLocation"));
        System.out.println("============加载配置完成==============");
        // 2.扫描相关类
        doScannerClass(contextConfig.getProperty("scanPackage"));
        System.out.println("============扫描类完成==============");

        //=================IOC部分===================
        // 3.创建IOC容器，将相关类实例化并存入IOC容器
        doInstance();
        System.out.println("============IOC容器实例化完成==============");
        //=================AOP=====================
        System.out.println("============AOP执行完成==============");
        //=================DI部分===================
        // 4.进行DI注入
        doAutowired();
        System.out.println("============DI注入完成==============");

        //=================MVC部分===================
        // 5.初始化HandlerMapping
        doInitHandlerMapping();
        System.out.println("============初始化HandlerMapping完成==============");

        System.out.println("Lg Spring framework is init.");

    }

    /**
     * 初始化HandlerMapping
     */
    private void doInitHandlerMapping() {
        if (IOC.isEmpty()) {return;}

        for (Map.Entry<String, Object> iocEntry : IOC.entrySet()) {
            Class<?> clazz = iocEntry.getValue().getClass();

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
                String url = String.format("/%s/%s",classUrl,lgRequestMapping.value()).replaceAll("/+","/");
                // 匹配url和方放进行反射调用
                handlerMapping.put(url,method);
                System.out.println(String.format("Mapping:%s------->%s",url,method));
            }
        }
    }

    /**
     * 依赖注入
     */
    private void doAutowired() {
        if (IOC.isEmpty()) {return;}

        for (Map.Entry<String, Object> iocEntry : IOC.entrySet()) {
            // 把所有的包括private/protected/default/public 修饰字段都取出来
            for (Field field : iocEntry.getValue().getClass().getDeclaredFields()) {
                // 没有注解不进行注入
                if (!field.isAnnotationPresent(LgAutowired.class)) {continue;}

                LgAutowired lgAutowired = field.getAnnotation(LgAutowired.class);
                // 获取注解自定义beanName
                String beanName = lgAutowired.value().trim();
                if ("".equals(beanName.trim())) {
                    // 通过类型去获取beanName
                    beanName = field.getType().getName();
                }
                // 暴力访问
                field.setAccessible(true);

                try {
                    // 当前实例根据beanName进行注入
                    field.set(iocEntry.getValue(),IOC.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * IOC实例化
     */
    private void doInstance() {
        if (classNames.isEmpty()) {return;}

        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(LgController.class)) {
                    // 获取ioc的beanName和bean实例
                    String beanName = toLowerFirstCase(clazz.getSimpleName());
                    Object instance = clazz.newInstance();
                    IOC.put(beanName,instance);
                } else if (clazz.isAnnotationPresent(LgService.class)) {
                    // 1.在自定义名称中，必须全局唯一
                    String beanName = clazz.getAnnotation(LgService.class).value();

                    // 2.默认名称时小写
                    if ("".equals(beanName.trim())) {
                        toLowerFirstCase(clazz.getSimpleName());
                    }
                    // 3.实例化
                    Object instance = clazz.newInstance();
                    IOC.put(beanName, instance);
                    // 4.如果是接口，判断有多少个实现类，有多个则需要异常
                    for (Class<?> i : clazz.getInterfaces()) {
                        if (IOC.containsKey(i.getName())) {
                            throw new Exception(String.format("This %s is exits",i.getName()));
                        }
                        IOC.put(i.getName(),instance);
                    }
                } else {
                    continue;
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return new String(chars);
    }

    /**
     * 类扫描
     * @param scanPackage 包路径
     */
    private void doScannerClass(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource(String.format("/%s",scanPackage.replaceAll("\\.","/")));
        File classPath = new File(url.getFile());

        // 文件夹获取所有类
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                // 递归
                doScannerClass(String.format("%s.%s",scanPackage,file.getName()));
            } else {
                // class文件才进行保存
                if (!file.getName().endsWith(".class")) {continue;}
                String className = String.format("%s.%s",scanPackage,file.getName().replace(".class",""));
                // className可通过Class.forName进行实例化
                classNames.add(className);
            }

        }
    }

    /**
     * 读取配置文件
     * @param contextConfigLocation
     */
    private void doLoadConfig(String contextConfigLocation) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
