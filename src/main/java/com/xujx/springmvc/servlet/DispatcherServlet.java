package com.xujx.springmvc.servlet;

import com.xujx.springmvc.annotation.*;
import com.xujx.springmvc.test.controller.UserController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xujinxin on 2017/8/7.
 */
public class DispatcherServlet extends HttpServlet {

    /* path -> method : /add -> add() */
    private Map<String, Method> methodsMap = new HashMap<>();

    /*method -> com.xujx.springmvc....  : add()-> "Test.class" */
    private Map<Method, String> methodLocationMap = new HashMap<>();

    /*com.xujx.springmvc.... -> Test.class*/
    private Map<String, Class> clazzesMap = new HashMap<>();

    /*class -> class.instance :Test.class :new Test()*/
    private Map<Class, Object> instanceMap = new HashMap<>();

    private List<String> packageNames = new ArrayList<>();

    /**
     * 加载类配置
     */
    @Override
    public void init(ServletConfig config) throws ServletException {

        String basePackage = config.getInitParameter("base-package");
        scanBasePackage(basePackage);
        try {
            instance();
            handRequestMapping();
            ioc();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();

        Method method = methodsMap.get(path);

        String location = methodLocationMap.get(method);

        Class clazz = clazzesMap.get(location);

        try {
            method.invoke(instanceMap.get(clazz));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描类的包路径名
     *
     * @param basePackage base-package
     */
    private void scanBasePackage(String basePackage) {
        URL url = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));

        if (url != null) {
            File basePackageFile = new File(url.getPath());

            File[] childFiles = basePackageFile.listFiles();

            if (childFiles != null && childFiles.length != 0) {
                for (File file : childFiles) {
                    if (file.isDirectory()) {
                        scanBasePackage(basePackage.concat(".").concat(file.getName()));
                    } else if (file.isFile()) {
                        packageNames.add(basePackage.concat(".").concat(file.getName().split("\\.")[0]));
                    }
                }
            }
        }
    }

    private void instance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (packageNames != null && packageNames.size() != 0) {
            for (String className : packageNames) {
                Class clazz = Class.forName(className);

                if (clazz.isAnnotationPresent(Controller.class)
                        || clazz.isAnnotationPresent(Service.class)
                        || clazz.isAnnotationPresent(Repository.class)) {

                    Class value = null;

                    if (clazz.isAnnotationPresent(Controller.class)) {
                        Controller controller = (Controller) clazz.getAnnotation(Controller.class);
                        value = controller.clazz();
                    } else if (clazz.isAnnotationPresent(Service.class)) {
                        Service service = (Service) clazz.getAnnotation(Service.class);
                        value = service.clazz();
                    } else if (clazz.isAnnotationPresent(Repository.class)) {
                        Repository repository = (Repository) clazz.getAnnotation(Repository.class);
                        value = repository.clazz();
                    }
                    if (value != null) {
                        instanceMap.put(value, clazz.newInstance());
                        clazzesMap.put(className, value);
                    }
                }

            }
        }
    }

    /**
     * 拦截处理url映射
     */
    private void handRequestMapping() throws ClassNotFoundException {
        if (packageNames != null && packageNames.size() != 0) {
            for (String className : packageNames) {
                Class clazz = Class.forName(className);

                if (clazz.isAnnotationPresent(Controller.class)) {
                    Method[] methods = clazz.getDeclaredMethods();

                    if (methods != null && methods.length != 0) {
                        for (Method method : methods) {
                            if (method.isAnnotationPresent(RequestMapping.class)) {
                                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                                methodLocationMap.put(method, className);
                                methodsMap.put(requestMapping.path(), method);
                            }
                        }
                    }
                }

            }
        }
    }

    private void ioc() {
        instanceMap.forEach((clazz, obj) -> {
            Field[] fields = obj.getClass().getDeclaredFields();

            if (fields != null && fields.length != 0) {
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Resource.class)) {
                        Resource resource = field.getAnnotation(Resource.class);

                        field.setAccessible(true);
                        try {
                            field.set(obj, instanceMap.get(resource.clazz()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
