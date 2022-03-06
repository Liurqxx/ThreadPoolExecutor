package com.liurq.handler;

import com.liurq.executor.LiurqThreadPoolExecutor;

/**
 * @author liurq
 * @desc 拒绝策略接口
 * @date 2022-03-05  18:40
 */
public interface PolicyHandler {

    /**
     * 拒绝策略
     *
     * @param task
     * @param executor
     */
    void rejected(Runnable task, LiurqThreadPoolExecutor executor)  ;

}
