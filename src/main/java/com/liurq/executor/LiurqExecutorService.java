package com.liurq.executor;

/**
 * @author liurq
 * @desc 线程池接口
 * @date 2022-03-05  18:40
 */
public interface LiurqExecutorService {

    /**
     * 执行任务
     *
     * @param task
     */
    void execute(Runnable task);

    /**
     * 关闭
     */
    void shutdown();

    /**
     * 核心线程池数量
     *
     * @return
     */
    int getCorePoolSize();

    /**
     * 数量
     *
     * @return
     */
    Runnable getTask();

}
