package com.liurq.test;

import com.liurq.executor.LiurqThreadPoolExecutor;
import org.junit.Test;

/**
 * @Desc 线程池测试
 * @Author Liurq
 * @Date 2022/3/7 17:56
 */
public class LiurqThreadTest {


    @Test
    public void threadTestStep01() throws InterruptedException {


        LiurqThreadPoolExecutor threadPoolExecutor = new LiurqThreadPoolExecutor(10);

        for (int i = 0; i < 100; i++) {
            threadPoolExecutor.execute(new RunTask());
        }
        Thread.sleep(Integer.MAX_VALUE);

    }

}