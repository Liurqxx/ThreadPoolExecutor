package com.liurq.executor;


import com.liurq.handler.DefaultPolicyHandler;
import com.liurq.handler.PolicyHandler;

import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author liurq
 * @desc 自定义线程池
 * @date 2022-03-05  19:40
 */
public class LiurqThreadPoolExecutor implements LiurqExecutorService {
    /**
     * 默认队列大小
     */
    private static final int defaultQueueSize = 5;

    /**
     * 默认池的大小
     */
    private static final int defaultPoolSize = 5;

    private static final long defaultAliveTime = 60l;

    /**
     * 线程池最大的大小
     */
    private static final int maxPoolSize = 50;

    /**
     * 线程池大小
     */
    private volatile int poolsize;

    /**
     * 任务容量
     */
    private long completedTaskCount;

    /**
     * 拒绝策略
     */
    private volatile PolicyHandler handler;

    /**
     * 是否已经中断
     */
    private volatile boolean isShutDown = false;

    /**
     * active当前激活线程数
     */
    private AtomicInteger ctl = new AtomicInteger();

    /**
     * 队列
     */
    private final BlockingQueue<Runnable> workQueue;

    /**
     * Lock
     */
    private final ReentrantLock mainLock = new ReentrantLock();
    /**
     * worker集合
     */
    private final HashSet<Worker> workers = new HashSet<Worker>();

    /**
     * 是否允许超时
     */
    private volatile boolean allowThreadTimeOut;

    private volatile long keepAliveTime;

    public LiurqThreadPoolExecutor() {
        this(defaultPoolSize, defaultQueueSize, defaultAliveTime, new DefaultPolicyHandler());
    }

    public LiurqThreadPoolExecutor(int poolsize) {
        this(poolsize, defaultQueueSize, defaultAliveTime, new DefaultPolicyHandler());
    }

    public LiurqThreadPoolExecutor(int poolsize, int queueSize, long keepAliveTime, PolicyHandler handler) {
        if (poolsize <= 0 || poolsize > maxPoolSize)
            throw new IllegalArgumentException("线程池大小不能<=0");
        this.poolsize = poolsize;
        this.handler = handler;
        this.keepAliveTime = keepAliveTime;
        if (keepAliveTime > 0)
            allowThreadTimeOut = true;
        this.workQueue = new ArrayBlockingQueue<Runnable>(queueSize);
    }

    /**
     * 执行任务
     *
     * @param task
     */
    public void execute(Runnable task) {
        if (task == null)
            throw new NullPointerException("任务不能为空");
        if (isShutDown)
            throw new IllegalStateException("线程池已销毁,禁止提交任务...");

        int c = ctl.get();
        //任务数小于
        if (c < poolsize) {
            if (addWorker(task, true))
                return;
        } else if (workQueue.offer(task)) {

        } else {
            handler.rejected(task, this);//任务拒绝策略
        }
    }

    public void shutdown() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            isShutDown = true;
            for (Worker w : workers) {
                Thread t = w.thread;
                if (!t.isInterrupted() && w.tryLock()) {
                    try {
                        t.interrupt();
                    } catch (Exception e) {
                        //e.printStackTrace();
                    } finally {
                        w.unlock();
                    }
                }
            }
        } finally {
            mainLock.unlock();
        }

    }

    public int getCorePoolSize() {
        return ctl.get();
    }

    /**
     * 取出任务
     *
     * @return
     */
    public Runnable getTask() {
        try {
            return allowThreadTimeOut ? workQueue.poll(keepAliveTime, TimeUnit.SECONDS) : workQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void runWorker(Worker worker) {
        Thread wt = Thread.currentThread();
        Runnable task = worker.firstTask;
        worker.firstTask = null;
        boolean completedAbruptly = true;
        try {
            while (task != null || (task = getTask()) != null) {
                worker.lock();
                if (isShutDown && !wt.isInterrupted()) {
                    wt.interrupt();
                }
                try {
                    task.run();
                } finally {
                    task = null;
                    worker.completedTask++; //当前线程完成的任务数
                    worker.unlock();
                }
            }
            completedAbruptly = false;
        } finally {
            processWorkerExit(worker, completedAbruptly);
        }
    }

    private void processWorkerExit(Worker worker, boolean completedAbruptly) {
        if (completedAbruptly)
            ctl.decrementAndGet();

        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            completedTaskCount += worker.completedTask;
            workers.remove(worker);
        } finally {
            mainLock.unlock();
        }
        if (completedAbruptly && !workQueue.isEmpty()) {
            addWorker(null, false);
        }
    }

    /**
     * 是否启动线程执行任务 or 放入
     *
     * @param r
     * @param startNew
     */
    private boolean addWorker(Runnable r, boolean startNew) {
        if (startNew) {
            ctl.incrementAndGet();
        }
        boolean workerAdded = false;
        boolean workerStarted = false;

        Worker w = new Worker(r);
        Thread t = w.thread;
        if (t != null) {
            ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                if (!isShutDown) { // 线程池未关闭
                    if (t.isAlive()) // 检查线程是否已经处于运行状态，start方法不能重复调用执行
                        throw new IllegalThreadStateException();
                    workers.add(w);
                    workerAdded = true;
                }
            } finally {
                mainLock.unlock();
            }
            if (workerAdded) {
                t.start();
                workerStarted = true;
            }
        }
        return workerStarted;
    }

    static AtomicInteger atomic = new AtomicInteger();

    class Worker extends ReentrantLock implements Runnable {

        volatile long completedTask;
        final Thread thread;
        Runnable firstTask;

        public Worker(Runnable r) {
            this.firstTask = r;
            this.thread = new Thread(this, "thread-name-" + atomic.incrementAndGet());
        }

        @Override
        public void run() {
            runWorker(this);
        }
    }

}
