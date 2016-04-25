
package com.goldsand.collaboration.connection;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class ConnectionThreads {
    private static final int MAX_THREAD_SIZE = 5;
    private ThreadPoolExecutor mExecutor;
    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);
    private final static AtomicInteger mCount = new AtomicInteger(1);
    private static ConnectionThreads sInstance = null;
    private Object mThreadNameLocker = new Object();

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        public Thread newThread(Runnable r) {
            return new Thread(r, "Connection#" + mCount.getAndIncrement());
        }
    };

    public static synchronized ConnectionThreads getInstance() {
        if (sInstance == null) {
            sInstance = new ConnectionThreads();
        }
        return sInstance;
    }

    private ConnectionThreads() {
        mExecutor = new ThreadPoolExecutor(MAX_THREAD_SIZE, MAX_THREAD_SIZE, MAX_THREAD_SIZE,
                TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
    }

    public String startRun(ServerConnection runnable) {
        synchronized (mThreadNameLocker) {
            mExecutor.execute(runnable);
            String currentThreadName = "Connection#" + (mCount.get() - 1);
            runnable.setThreadName(currentThreadName);
            return currentThreadName;
        }
    }
}
