package com.liurq.handler;


import com.liurq.executor.LiurqThreadPoolExecutor;


/**
 *
 */
public class DefaultPolicyHandler implements PolicyHandler {

    public DefaultPolicyHandler() {
    }

    /**
     * 拒绝策略
     *
     * @param task
     * @param executor
     */
    @Override
    public void rejected(Runnable task, LiurqThreadPoolExecutor executor) {
        //TODO 拒绝策略，自行实现
//        throw new PolicyException("任务已经满了");
    }
}
