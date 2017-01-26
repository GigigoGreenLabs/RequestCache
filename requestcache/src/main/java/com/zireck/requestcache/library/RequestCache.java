package com.zireck.requestcache.library;

import com.zireck.requestcache.library.model.RequestModel;

public interface RequestCache {
  void setRequestIntervalTime(long intervalTimeInMillis);
  void enqueueRequest(RequestModel requestModel);
  void enqueueRequests();
  boolean sendPendingRequests();
  void clearRequestsCache();
}
