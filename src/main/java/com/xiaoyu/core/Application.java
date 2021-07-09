package com.xiaoyu.core;


/**
 * <p>
 * IOC 启动类
 * </p>
 *
 * @author ZhangXianYu   Email: 1600501744@qq.com
 * @since 2021/6/25 17:35
 */
public class Application {

    /**
     * 启动类的class，会扫描类所在的包和子包下的bean
     *
     * @param runClass 启动类class
     * @return BeanScanner
     */
    public static BeanFactory run(Class<?> runClass) {
        return BeanScanner.getInstance(runClass, null);
    }

    /**
     * 会扫描包下所有的bean
     *
     * @param packageName 包名称
     * @return BeanScanner
     */
    public static BeanFactory run(String packageName) {
        return BeanScanner.getInstance(null, packageName);
    }


    /**
     * 获取bean工厂，全局直接手动获取bean，获取到的前提是已经初始化了工厂，调用上面方法进行初始化
     *
     * @return BeanFactory
     */
    public static BeanFactory getDefaultBeanFactory() {
        return BeanScanner.getInstance(null, null);
    }
}
