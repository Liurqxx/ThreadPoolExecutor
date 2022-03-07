package com.liurq.test;

/**
 * @Desc
 * @Author Liurq
 * @Date 2022/3/7 18:33
 */
public class RunTask implements Runnable {
    @Override
    public void run() {
        System.out.println("任务运行");
    }
}