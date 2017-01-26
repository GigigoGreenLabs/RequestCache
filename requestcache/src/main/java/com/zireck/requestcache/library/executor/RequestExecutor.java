package com.zireck.requestcache.library.executor;

import com.zireck.requestcache.library.cache.RequestQueue;

public interface RequestExecutor {
  boolean isExecuting();
  void setIntervalTime(long intervalTimeInMillis);
  boolean execute(RequestQueue requestQueue);
}
