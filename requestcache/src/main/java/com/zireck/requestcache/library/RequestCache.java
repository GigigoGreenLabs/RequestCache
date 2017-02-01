package com.zireck.requestcache.library;

import com.zireck.requestcache.library.model.RequestModel;
import java.util.List;

public interface RequestCache {
  void setRequestIntervalTime(long intervalTimeInMillis);
  void enqueueRequest(RequestModel requestModel);
  void enqueueRequests(List<RequestModel> requestModels);
  void sendPendingRequests();
  void clearRequestsCache();
}
