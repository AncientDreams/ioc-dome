package com.xiaoyu.core;


import com.xiaoyu.annotation.Autowired;
import com.xiaoyu.annotation.Component;
import com.xiaoyu.annotation.Qualifier;
import com.xiaoyu.annotation.Service;
import com.xiaoyu.util.PackageUtil;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * IOC，扫描并初始化所有bean
 * </p>
 *
 * @author ZhangXianYu   Email: 1600501744@qq.com
 * @since 2021/6/22 14:19
 */
public class BeanScanner extends DefaultBeanFactory {

    private static BeanScanner instance;

    public static BeanScanner getInstance(Class<?> run, String packageName) {
        if (instance == null) {
            synchronized (BeanScanner.class) {
                if (instance == null) {
                    instance = new BeanScanner(run, packageName);
                    return instance;
                }
            }
        }
        return instance;
    }

    private BeanScanner(Class<?> run, String packageName) {
        if (run != null) {
            buildBean(run.getPackage().getName());
        }
        if (packageName != null) {
            buildBean(packageName);
        }
    }

    private void buildBean(String packageName) {
        try {
            List<Class<?>> list = PackageUtil.getClassName(packageName);
            //初始化所有的bean
            for (Class<?> scannerClass : list) {
                //创建对象并加入到容器
                createBean(scannerClass);
                //给对象中的属性进行注入
                if (getBean(scannerClass) != null) {
                    setBean(scannerClass, list);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据bean的class创建bean
     * <p>
     * 首先判断当前class是否包含注解，如果是需要管理的类就开始创建bean
     * 然后开始判断是否有构造方法，如果没有构造方法就直接创建bean，无需准备需要创建bean的参数。
     * 注意，bean的构造方法形参必须是bean，不能是其他的类型，否则不发创建bean，Spring亦是如此。
     * 如果是有参的构造方法，那么就开始准备好参数后再创建bean，循环遍历所有的参数类型，然后再
     * 容器中找是否已经创建过这个bean，如果没有就递归这个方法创建需要的bean。将创建需要的bean
     * 准备好后就可以开始创建这个bean了，然后把创建好的bean加入到容器（Map）
     * </p>
     *
     * @param scannerClass 需要创建的bean class
     * @throws Exception Exception
     */
    @SuppressWarnings("all")
    private void createBean(Class<?> scannerClass) throws Exception {
        //需要被管理的类
        if (scannerClass.isAnnotationPresent(Service.class) || scannerClass.isAnnotationPresent(Component.class)) {
            if (super.getBean(scannerClass) != null) {
                return;
            }
            Object bean = null;
            //获取class的构造方法，只有一个构造方法，要么是默认构造，要么是带参数构造
            Constructor<?> constructors = scannerClass.getDeclaredConstructors()[0];
            constructors.setAccessible(true);
            //获取构造方法的 参数类型
            Class<?>[] parameterTypes = constructors.getParameterTypes();
            if (parameterTypes.length == 0) {
                //无参数构造
                bean = scannerClass.newInstance();
            } else {
                Object[] params = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    //有参构造
                    if (super.getBean(parameterTypes[i]) == null) {
                        createBean(parameterTypes[i]);
                    }
                    Object constructorParamBean = super.getBean(parameterTypes[i]);
                    params[i] = constructorParamBean;
                }
                bean = constructors.newInstance(params);
            }

            // 注入的bean名称
            String beanName = getBeanName(scannerClass);
            super.beanMap.put(beanName, bean);
        }
    }


    /**
     * 注入，将bean中的字段进行注入赋值
     * <p>
     * 先获取class中的所有字段，然后判断字段是否包含Autowired注解，包含注解表示需要
     * 注入。然后判断是否是接口，如果是判断是否包含Qualifier注解，如果有根据此注解中
     * 的value值取获取bean，如果获取不到就直接把该bean下的所有实现类创建加入到容器，
     * 再次去get一次，获取不到就抛出异常，其他的类似。
     *
     * @param beanClass    需要注入的class
     * @param allClassList 扫描到的所有class
     */
    private void setBean(Class<?> beanClass, List<Class<?>> allClassList) throws Exception {
        //给bean中属性注入
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Object wiredBean = null;
                if (field.getType().isInterface()) {
                    if (field.isAnnotationPresent(Qualifier.class)) {
                        //多个实现类选取一个注入
                        Qualifier qualifier = field.getAnnotation(Qualifier.class);
                        wiredBean = super.getBean(qualifier.value());
                        if (wiredBean == null) {
                            List<Class<?>> interfaceImplClass = getInterfaceImplClass(field.getType(), allClassList);
                            for (Class<?> implClass : interfaceImplClass) {
                                createBean(implClass);
                            }
                        }
                        wiredBean = super.getBean(qualifier.value());
                        if (wiredBean == null) {
                            throw new Exception("Class : " + beanClass + "中，field :" + field.getType() + " Qualifier ->" + qualifier.value() + ",bean不存在");
                        }
                    } else {
                        List<Class<?>> interfaceImplClass = getInterfaceImplClass(field.getType(), allClassList);
                        for (Class<?> implClass : interfaceImplClass) {
                            createBean(implClass);
                        }
                        List<?> list = super.getServiceBean(field.getType());
                        if (list != null) {
                            if (list.size() > 1) {
                                throw new Exception("接口有多个实现bean : " + beanClass);
                            }
                            wiredBean = list.get(0);
                        }
                    }
                } else {
                    wiredBean = super.getBean(field.getType());
                    if (wiredBean == null) {
                        createBean(field.getType());
                        wiredBean = super.getBean(field.getType());
                    }
                }
                field.setAccessible(true);
                field.set(super.getBean(beanClass), wiredBean);
            }
        }
    }

    /**
     * 通过class获取bean的名称，如果注解指定的bean名称就使用，如果没有就默认类名称首字母小写
     *
     * @param scannerClass class
     * @return bean name
     */
    private String getBeanName(Class<?> scannerClass) throws Exception {
        // 注入的bean名称
        String beanName = null;
        if (scannerClass.isAnnotationPresent(Service.class)) {
            Service myService = scannerClass.getAnnotation(Service.class);
            beanName = myService.value();
        }
        if (scannerClass.isAnnotationPresent(Component.class)) {
            Component component = scannerClass.getAnnotation(Component.class);
            beanName = component.value();
        }
        if (strIsNull(beanName)) {
            beanName = toggleCase(scannerClass.getSimpleName());
        }
        if (super.beanMap.containsKey(beanName)) {
            throw new Exception("Bean 名称重复：" + beanName);
        }
        return beanName;
    }

    /**
     * 获取接口的所有实现类
     *
     * @param interfaceClass 接口class
     * @param list           所有被扫描到的class
     * @return 实现类集合
     */
    private List<Class<?>> getInterfaceImplClass(Class<?> interfaceClass, List<Class<?>> list) {
        List<Class<?>> classList = new ArrayList<>();
        for (Class<?> scannerClass : list) {
            for (Class<?> anInterface : scannerClass.getInterfaces()) {
                if (anInterface == interfaceClass) {
                    classList.add(scannerClass);
                }
            }
        }
        return classList;
    }

    private boolean strIsNull(String str) {
        return str == null || "".equals(str);
    }

    /**
     * 将字符串首字母小写
     *
     * @param name 需要处理的字符串
     * @return 处理后的字符串
     */
    private String toggleCase(String name) {
        char[] cs = name.toCharArray();
        cs[0] += 32;
        return String.valueOf(cs);
    }

//    /**
//     * 方法jdk1.8才提供，获取类上的注解，不包含继承
//     *
//     * @param beanClass       beanClass
//     * @param annotationClass annotationClass
//     * @return T
//     */
//    private static <A extends Annotation> A getDeclaredAnnotation(Class<?> beanClass, Class<A> annotationClass) {
//        Annotation[] annotations = beanClass.getDeclaredAnnotations();
//        for (Annotation annotation : annotations) {
//            if (annotation.annotationType() == annotationClass) {
//                return (A) annotation;
//            }
//        }
//        return null;
//    }

}
