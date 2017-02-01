package com.zireck.requestcache.library.executor;

import com.zireck.requestcache.library.cache.RequestQueue;
import io.reactivex.observers.DisposableObserver;

public interface RequestExecutor {
  boolean isExecuting();
  void setIntervalTime(long intervalTimeInMillis);
  void execute(RequestQueue requestQueue, DisposableObserver observer);
}
