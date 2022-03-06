package com.liurq.handler;


import com.liurq.executor.LiurqThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
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
        log.info("任务队列已满..");
//        throw new PolicyException("任务已经满了");
    }
}
