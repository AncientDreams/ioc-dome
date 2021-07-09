package com.xiaoyu.core;


/**
 * <p>
 * bean工厂接口
 * </p>
 *
 * @author ZhangXianYu   Email: 1600501744@qq.com
 * @since 2021/6/22 17:10
 */
public interface BeanFactory {

    /**
     * 通过bean的名称获取bean
     *
     * @param beanName beanName
     * @return bean
     */
    Object getBean(String beanName);

    /**
     * 通过bean的class获取bean
     *
     * @param bean bean
     * @param <T>  t
     * @return T
     */
    <T> T getBean(Class<T> bean);

    /**
     * 通过bean的名称获取bean，并且判断是否是指定类型的bean，如果是则返回，不是则返回null
     *
     * @param name         bean名称
     * @param requiredType Class类型
     * @return T
     */
    <T> T getBean(String name, Class<T> requiredType);

    /**
     * 返回这个bean是否存在
     *
     * @param name bean名称
     * @return boolan
     */
    boolean containsBean(String name);


    /**
     * 返回这个bean是否存在
     *
     * @param beanClass bean Class
     * @return boolan
     */
    boolean containsBean(Class<?> beanClass);
}
