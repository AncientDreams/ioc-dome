package com.xiaoyu.bean;

import com.xiaoyu.annotation.Service;

/**
 * <p>
 *
 * </p>
 *
 * @author ZhangXianYu   Email: 1600501744@qq.com
 * @since 2021/6/28 10:27
 */
@Service
public class TestServiceImpl2 implements TestService {

    @Override
    public void sayHello() {
        System.out.println("hello,i am testServiceImpl2");
    }
}
