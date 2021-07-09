package com.xiaoyu;

import com.xiaoyu.bean.TestA;
import com.xiaoyu.core.Application;
import com.xiaoyu.core.BeanFactory;

/**
 * <p>
 * 启动测试类
 * </p>
 *
 * @author ZhangXianYu   Email: 1600501744@qq.com
 * @since 2021/7/9 10:44
 */
public class ApplicationContext {

    public static void main(String[] args) {
        /*
            循环依赖的产生？
            假设有一个  A类   和  B类
            A类中需要一个属性B，B类中需要一个属性A
            那么在创建A的时候，发现需要B,那么就创建B，创建B的时候发现需要A，那么就创建A……………………………………………………
            这个就死循环了。

            循环依赖的解决
            问题在于创建B的时候没有A，创建A的时候没有B。
            于是我们在创建B的时候，先创建好A。
            于是变成了 创建A（ newInstance() ），创建完成，把它加入到容器中，再为A中的属性进行注入，发现需要B，
            容器中没有B，于是创建B，创建完成后加入到容器，再对B类中的属性进行注入，发现B需要A，于是从容器中
            获取到A，注入到B中，就完成了这两个类的创建。
            Spring 的解决方式也是类似，先创建对象放到一个缓存容器里面，然后再对属性进行注入。

            无法解决的问题
            上面解决的核心方式在于先创建A，那么如果A是通过构造方法注入的话，就无法解决。
            因为创建A的时候无法提供B出来，所以A始终是创建不了的，通过反射无法创建。
            Spring也无法解决构造方法的循环依赖。
            举例：
            public Class A{
                private B b;

                public A(B b){
                this.b = b;
                }
            }

            public Class B{
                private A a;

                public B(A a){
                this a = a;
                }
            }

            通过构造方法创建对象的时候无法提供构造方法需要的对象，所以创建不出来A，那么B也创建不了。
            换种方式展现：
            A a = new A(需要提供B);
            B b = new B(需要提供A);
            没有A的话B创建不了，没有B的话A创建不了，于是两个都创建不了。
         */
        BeanFactory beanFactory = Application.run(ApplicationContext.class);
        beanFactory.getBean(TestA.class).sayHello();
    }
}
