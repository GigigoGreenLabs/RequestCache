package com.zireck.requestcache.library.executor;

import android.util.Log;
import com.zireck.requestcache.library.cache.RequestQueue;
import com.zireck.requestcache.library.model.RequestModel;
import com.zireck.requestcache.library.network.NetworkRequestManager;
import com.zireck.requestcache.library.network.NetworkResponseCallback;

public class PendingRequestsExecutor implements RequestExecutor, Runnable {

  private static final String TAG = PendingRequestsExecutor.class.getSimpleName();
  private static final int DEFAULT_REQUEST_INTERVAL_IN_MILLIS = 5000;

  private ThreadExecutor threadExecutor;
  private long intervalTimeInMillis;
  private final NetworkRequestManager networkRequestManager;
  private boolean isExecuting = false;
  private RequestQueue requestQueue;

  public PendingRequestsExecutor(ThreadExecutor threadExecutor,
      NetworkRequestManager networkRequestManager) {
    this.threadExecutor = threadExecutor;
    this.networkRequestManager = networkRequestManager;
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
      Log.e(TAG, "Invalid request list given");
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
      isExecuting = false;
      Log.d(TAG, "No pending requests left.");
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
    requestQueue.persistToDisk();
    executeNextPendingRequest();
  }

  private void handleUnsuccessfulResponse() {
    executeNextPendingRequest();
  }

  private void sleep(long intervalTimeInMillis) {
    try {
      Thread.sleep(intervalTimeInMillis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
