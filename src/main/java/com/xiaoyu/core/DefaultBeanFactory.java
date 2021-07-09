package com.xiaoyu.core;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * bean工厂默认实现
 * </p>
 *
 * @author ZhangXianYu   Email: 1600501744@qq.com
 * @since 2021/6/22 17:16
 */
@SuppressWarnings("all")
public abstract class DefaultBeanFactory implements BeanFactory {

    final Map<String, Object> beanMap = new ConcurrentHashMap<>();

    @Override
    public Object getBean(String beanName) {
        return beanMap.get(beanName);
    }

    @Override
    public <T> T getBean(Class<T> bean) {
        for (String key : beanMap.keySet()) {
            Class<?> c = beanMap.get(key).getClass();
            if (bean == c) {
                return (T) beanMap.get(key);
            }
        }
        return null;
    }

    /**
     * 通过接口class获取实现类
     *
     * @param bean class
     * @param <T>  T
     * @return T
     */
    public <T> List<T> getServiceBean(Class<T> bean) {
        List<T> beans = new ArrayList<>();
        for (String key : beanMap.keySet()) {
            Class<?> c = beanMap.get(key).getClass();
            Class<?>[] classes = c.getInterfaces();
            for (Class<?> aClass : classes) {
                if (aClass == bean) {
                    beans.add((T) beanMap.get(key));
                }
            }
        }
        return beans;
    }


    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        T bean = (T) getBean(name).getClass();
        if (bean == requiredType) {
            return bean;
        }
        return null;
    }

    @Override
    public boolean containsBean(String name) {
        return beanMap.containsKey(name);
    }

    @Override
    public boolean containsBean(Class<?> beanClass) {
        return beanMap.containsValue(beanMap.get(beanClass));
    }
}
