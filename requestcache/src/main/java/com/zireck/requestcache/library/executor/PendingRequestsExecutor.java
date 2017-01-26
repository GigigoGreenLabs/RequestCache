package com.zireck.requestcache.library.executor;

import android.os.CountDownTimer;
import android.util.Log;
import com.zireck.requestcache.library.cache.RequestQueue;
import com.zireck.requestcache.library.model.RequestModel;
import com.zireck.requestcache.library.network.ApiService;
import com.zireck.requestcache.library.util.MethodType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingRequestsExecutor implements RequestExecutor {

  private static final String TAG = PendingRequestsExecutor.class.getSimpleName();
  private static final int REQUEST_INTERVAL_IN_MILLIS = 5000;

  private Callback<ResponseBody> retrofitCallback;
  private CountDownTimer executorTimer;
  private ApiService apiService;
  private boolean isExecuting = false;
  private RequestQueue requestQueue;

  public PendingRequestsExecutor(ApiService apiService) {
    this.apiService = apiService;
    setupRetrofitCallback();
    setupTimer(REQUEST_INTERVAL_IN_MILLIS);
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
    Call<ResponseBody> retrofitCall = getRetrofitCallFor(requestModel);
    if (retrofitCall == null) {
      Log.e(TAG, "Invalid Retrofit call");
      executeNextPendingRequest();
      return;
    }

    retrofitCall.enqueue(retrofitCallback);
  }

  private Call<ResponseBody> getRetrofitCallFor(RequestModel requestModel) {
    final String requestUrl = requestModel.getBaseUrl() + requestModel.getEndpoint();
    Call<ResponseBody> retrofitCall = null;

    if (requestModel.getMethodType() == MethodType.GET) {
      if (requestModel.getQuery() != null) {
        retrofitCall = apiService.requestGet(requestUrl);
      } else {
        retrofitCall = apiService.requestGet(requestUrl, requestModel.getQuery());
      }
    } else {
      // TODO
    }

    return retrofitCall;
  }

  private void setupRetrofitCallback() {
    retrofitCallback = new Callback<ResponseBody>() {
      @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        requestQueue.remove();
        requestQueue.persist();
        executorTimer.start();
      }

      @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
        executorTimer.start();
      }
    };
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
