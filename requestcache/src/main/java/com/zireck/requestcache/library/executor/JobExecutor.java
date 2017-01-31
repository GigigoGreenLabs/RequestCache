package com.zireck.requestcache.library.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JobExecutor implements ThreadExecutor {

  private static JobExecutor INSTANCE = null;

  private static final int INITIAL_POOL_SIZE = 3;
  private static final int MAX_POOL_SIZE = 5;
  private static final int KEEP_ALIVE_TIME = 10;
  private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

  private final BlockingQueue<Runnable> workQueue;
  private final ThreadFactory threadFactory;
  private final ThreadPoolExecutor threadPoolExecutor;

  public static ThreadExecutor getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new JobExecutor();
    }

    return INSTANCE;
  }

  private JobExecutor() {
    workQueue = new LinkedBlockingQueue<>();
    threadFactory = new JobThreadFactory();
    this.threadPoolExecutor =
        new ThreadPoolExecutor(INITIAL_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT, this.workQueue, this.threadFactory);
  }

  @Override public void execute(Runnable runnable) {
    if (runnable == null) {
      throw new IllegalArgumentException("Runnable to execute cannot be null");
    }

    threadPoolExecutor.execute(runnable);
  }

  private static class JobThreadFactory implements ThreadFactory {

    int counter = 0;

    @Override public Thread newThread(Runnable runnable) {
      return new Thread(runnable, "android_" + counter++);
    }
  }
}
