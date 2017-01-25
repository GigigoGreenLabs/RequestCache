package com.zireck.requestcache.library;

public interface RequestCache {
  void enqueueRequests();
  void sendPendingRequests();
  void clearRequestsCache();
}
