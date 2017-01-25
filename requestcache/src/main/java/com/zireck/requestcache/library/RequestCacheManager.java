package com.zireck.requestcache.library;

import com.zireck.requestcache.library.executor.RequestsExecutor;

public class RequestCacheManager implements RequestCache {

  private static final String TAG = RequestCacheManager.class.getSimpleName();

  private static RequestCacheManager INSTANCE = null;

  private final RequestsExecutor requestsExecutor;

  public static RequestCacheManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new RequestCacheManager();
    }

    return INSTANCE;
  }

  private RequestCacheManager() {
    requestsExecutor = new RequestsExecutor();
  }

  @Override public void enqueueRequests() {

  }

  @Override public void sendPendingRequests() {

  }

  @Override public void clearRequestsCache() {

  }
}
