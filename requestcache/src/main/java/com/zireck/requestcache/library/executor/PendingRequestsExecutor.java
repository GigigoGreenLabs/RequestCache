package com.zireck.requestcache.library.executor;

import android.os.CountDownTimer;
import android.util.Log;
import com.zireck.requestcache.library.cache.RequestQueue;
import com.zireck.requestcache.library.model.RequestModel;
import com.zireck.requestcache.library.network.NetworkRequestManager;
import com.zireck.requestcache.library.network.NetworkResponseCallback;

public class PendingRequestsExecutor implements RequestExecutor {

  private static final String TAG = PendingRequestsExecutor.class.getSimpleName();
  private static final int DEFAULT_REQUEST_INTERVAL_IN_MILLIS = 5000;

  private CountDownTimer executorTimer;
  private final NetworkRequestManager networkRequestManager;
  private boolean isExecuting = false;
  private RequestQueue requestQueue;

  public PendingRequestsExecutor(NetworkRequestManager networkRequestManager) {
    this.networkRequestManager = networkRequestManager;
    setupTimer(DEFAULT_REQUEST_INTERVAL_IN_MILLIS);
  }

  @Override public boolean isExecuting() {
    return isExecuting;
  }

  @Override public void setIntervalTime(long intervalTimeInMillis) {
    if (intervalTimeInMillis < 0) {
      throw new IllegalArgumentException("Interval time must be a positive number");
    }

    setupTimer(intervalTimeInMillis);
  }

  @Override public boolean execute(RequestQueue requestQueue) {
    if (requestQueue == null) {
      Log.e(TAG, "Invalid request list given");
      return false;
    }

    this.requestQueue = requestQueue;
    this.requestQueue.load();
    executeNextPendingRequest();
    return true;
  }

  private void executeNextPendingRequest() {
    if (requestQueue.isEmpty() || !requestQueue.hasNext()) {
      isExecuting = false;
      Log.d(TAG, "No pending requests left.");
      return;
    }

    isExecuting = true;

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
    requestQueue.persist();
    executorTimer.start();
  }

  private void handleUnsuccessfulResponse() {
    executorTimer.start();
  }

  private void setupTimer(long intervalTimeInMillis) {
    if (executorTimer != null) {
      executorTimer.cancel();
    }

    executorTimer = new CountDownTimer(intervalTimeInMillis, 1000) {
      @Override public void onTick(long millisUntilFinished) {
        // no-op
      }

      @Override public void onFinish() {
        executeNextPendingRequest();
      }
    };
  }
}
