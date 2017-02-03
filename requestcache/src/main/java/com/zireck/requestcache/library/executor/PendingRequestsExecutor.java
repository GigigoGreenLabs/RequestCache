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
  private boolean abortExecution = false;
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

  @Override public void cancel() {
    abortExecution = true;
  }

  @Override public void run() {
    this.requestQueue.loadToMemory();
    abortExecution = false;
    executeNextPendingRequest();
  }

  private void executeNextPendingRequest() {
    if (abortExecution) {
      abortExecution = false;
      isExecuting = false;
      logger.d("Aborting pending request executor.");
      return;
    }

    if (requestQueue.isEmpty() || !requestQueue.hasNext()) {
      requestQueue.persistToDisk();
      isExecuting = false;
      logger.d("No pending requests left.");
      return;
    }

    isExecuting = true;

    RequestModel requestModel = requestQueue.next();
    networkRequestManager.sendRequest(requestModel, new NetworkResponseCallback() {
      @Override public void onSuccess() {
        processResponse(true);
      }

      @Override public void onFailure() {
        processResponse(false);
      }
    });
  }

  private void processResponse(boolean isSuccessful) {
    if (isSuccessful) {
      requestQueue.remove();
    }

    sleep(intervalTimeInMillis);
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
