package com.zireck.requestcache.library.executor;

public interface ThreadExecutor {
  void execute(final Runnable runnable);
}
