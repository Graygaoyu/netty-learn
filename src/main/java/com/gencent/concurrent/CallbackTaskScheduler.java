package com.gencent.concurrent;

import com.google.common.util.concurrent.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CallbackTaskScheduler {
    static ListeningExecutorService gPool = null;
    static ExecutorService jPool;
    private static final int KEEP_ALIVE_SECONDS = 30;
    private static final int QUEUE_SIZE = 10000;
    private static final int MIXED_MAX = 128;
    private static final int max = MIXED_MAX;
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            max,
            max,
            KEEP_ALIVE_SECONDS,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(QUEUE_SIZE),
            new CustomThreadFactory("mixed"));

    private static class CustomThreadFactory implements ThreadFactory {
        //线程池数量
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;

        //线程数量
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String threadTag;

        CustomThreadFactory(String threadTag) {
            group = Thread.currentThread().getThreadGroup();
            this.threadTag = "apppool-" + poolNumber.getAndIncrement() + "-" + threadTag + "-";
        }

        @Override
        public Thread newThread(Runnable target) {
            Thread t = new Thread(group, target,
                    threadTag + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    static {
        jPool = EXECUTOR;
        gPool = MoreExecutors.listeningDecorator(jPool);
    }

    private CallbackTaskScheduler() {
    }

    public static <R> void add(CallbackTask<R> executeTask) {


        ListenableFuture<R> future = gPool.submit(executeTask::execute);

        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(R result) {
                executeTask.onBack(result);
            }

            @Override
            public void onFailure(Throwable t) {
                executeTask.onException(t);
            }
        }, jPool);


    }
}
