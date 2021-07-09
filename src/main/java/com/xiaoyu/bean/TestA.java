package com.xiaoyu.bean;

import com.xiaoyu.annotation.Autowired;
import com.xiaoyu.annotation.Qualifier;
import com.xiaoyu.annotation.Service;

/**
 * <p>
 *
 * </p>
 *
 * @author ZhangXianYu   Email: 1600501744@qq.com
 * @since 2021/6/25 17:36
 */
@Service
public class TestA {

    @Autowired
    private TestB testB;

    @Qualifier("testServiceImpl")
    @Autowired
    private TestService testService;

    public void sayHello() {
        System.out.println("hello,i am A");
        testB.sayHello();
        testService.sayHello();
    }
}
