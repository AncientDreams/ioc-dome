package com.xiaoyu.bean;

import com.xiaoyu.annotation.Service;

/**
 * <p>
 *
 * </p>
 *
 * @author ZhangXianYu   Email: 1600501744@qq.com
 * @since 2021/6/28 9:07
 */
@Service
public class TestServiceImpl implements TestService {

    @Override
    public void sayHello() {
        System.out.println("hello,i am testServiceImpl");
    }
}
