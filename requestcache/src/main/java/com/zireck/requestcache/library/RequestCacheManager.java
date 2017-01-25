package com.zireck.requestcache.library;

import com.zireck.requestcache.library.executor.RequestsExecutor;
import com.zireck.requestcache.library.network.ApiService;
import com.zireck.requestcache.library.network.ApiServiceBuilder;

public class RequestCacheManager implements RequestCache {

  private static final String TAG = RequestCacheManager.class.getSimpleName();

  private static RequestCacheManager INSTANCE = null;

  private final ApiServiceBuilder apiServiceBuilder;
  private final ApiService apiService;
  private final RequestsExecutor requestsExecutor;

  public static RequestCacheManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new RequestCacheManager();
    }

    return INSTANCE;
  }

  private RequestCacheManager() {
    apiServiceBuilder = new ApiServiceBuilder();
    apiService = apiServiceBuilder.build();
    requestsExecutor = new RequestsExecutor(apiService);
  }

  @Override public void enqueueRequests() {

  }

  @Override public void sendPendingRequests() {

  }

  @Override public void clearRequestsCache() {

  }
}
