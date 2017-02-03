package com.zireck.requestcache.library.executor;

import com.zireck.requestcache.library.cache.RequestQueue;
import com.zireck.requestcache.library.model.RequestModel;
import com.zireck.requestcache.library.network.NetworkRequestManager;
import com.zireck.requestcache.library.network.NetworkResponseCallback;
import com.zireck.requestcache.library.util.logger.Logger;
import com.zireck.requestcache.library.util.sleeper.Sleeper;

public class PendingRequestsExecutor implements RequestExecutor, Runnable {

  private static final int DEFAULT_REQUEST_INTERVAL_IN_MILLIS = 5000;

  private final ThreadExecutor threadExecutor;
  private final NetworkRequestManager networkRequestManager;
  private final Logger logger;
  private final Sleeper sleeper;
  private long intervalTimeInMillis;
  private boolean isExecuting = false;
  private RequestQueue requestQueue;

  public PendingRequestsExecutor(ThreadExecutor threadExecutor,
      NetworkRequestManager networkRequestManager, Logger logger, Sleeper sleeper) {
    this.threadExecutor = threadExecutor;
    this.networkRequestManager = networkRequestManager;
    this.logger = logger;
    this.sleeper = sleeper;
    this.intervalTimeInMillis = DEFAULT_REQUEST_INTERVAL_IN_MILLIS;
  }

  @Override public boolean isExecuting() {
    return isExecuting;
  }

  @Override public void setIntervalTime(long intervalTimeInMillis) {
    if (intervalTimeInMillis < 0) {
      throw new IllegalArgumentException("Interval time must be a positive number");
    }

    this.intervalTimeInMillis = intervalTimeInMillis;
  }

  @Override public boolean execute(RequestQueue requestQueue) {
    if (requestQueue == null) {
      logger.e("Invalid request list given");
      return false;
    }

    this.requestQueue = requestQueue;
    threadExecutor.execute(this);

    return true;
  }

  @Override public void run() {
    this.requestQueue.loadToMemory();
    executeNextPendingRequest();
  }

  private void executeNextPendingRequest() {
    if (requestQueue.isEmpty() || !requestQueue.hasNext()) {
      requestQueue.persistToDisk();
      isExecuting = false;
      logger.d("No pending requests left.");
      return;
    }

    isExecuting = true;

    sleep(intervalTimeInMillis);

    RequestModel requestModel = requestQueue.next();
    networkRequestManager.sendRequest(requestModel, new NetworkResponseCallback() {
      @Override public void onSuccess() {
        handleSuccessfulResponse();
      }

      @Override public void onFailure() {
        handleUnsuccessfulResponse();
      }
    });
  }

  private void handleSuccessfulResponse() {
    requestQueue.remove();
    executeNextPendingRequest();
  }

  private void handleUnsuccessfulResponse() {
    executeNextPendingRequest();
  }

  private void sleep(long intervalTimeInMillis) {
    try {
      sleeper.sleep(intervalTimeInMillis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
