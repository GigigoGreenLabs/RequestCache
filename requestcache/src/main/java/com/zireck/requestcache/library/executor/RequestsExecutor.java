package com.zireck.requestcache.library.executor;

import android.os.CountDownTimer;
import android.util.Log;
import com.zireck.requestcache.library.model.RequestModel;
import com.zireck.requestcache.library.network.ApiService;
import com.zireck.requestcache.library.util.MethodType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestsExecutor {

  private static final String TAG = RequestsExecutor.class.getSimpleName();
  private static final int REQUEST_INTERVAL_IN_MILLIS = 5000;

  private Callback<ResponseBody> retrofitCallback;
  private CountDownTimer executorTimer;
  private ApiService apiService;
  private boolean isExecuting = false;
  private List<RequestModel> requestModels;
  private Iterator<RequestModel> requestModelIterator;

  public RequestsExecutor(ApiService apiService) {
    this.apiService = apiService;
    setupRetrofitCallback();
    setupTimer(REQUEST_INTERVAL_IN_MILLIS);
  }

  public boolean isExecuting() {
    return isExecuting;
  }

  public void setIntervalTime(long intervalTimeInMillis) {
    if (intervalTimeInMillis < 0) {
      throw new IllegalArgumentException("Interval time must be a positive number");
    }

    setupTimer(intervalTimeInMillis);
  }

  public void execute(RequestModel requestModel) {
    if (requestModel == null) {
      Log.e(TAG, "Invalid request list given");
      return;
    }

    requestModels = Collections.synchronizedList(new ArrayList<RequestModel>());
    requestModels.add(requestModel);
    requestModelIterator = requestModels.iterator();
    executeNextPendingRequest();
  }

  public void execute(List<RequestModel> requestModels) {
    if (requestModels == null) {
      Log.e(TAG, "Invalid request list given");
      return;
    }

    this.requestModels = Collections.synchronizedList(requestModels);
    requestModelIterator = this.requestModels.iterator();
    executeNextPendingRequest();
  }

  private void executeNextPendingRequest() {
    if (!requestModelIterator.hasNext()) {
      isExecuting = false;
      Log.d(TAG, "No pending requests left.");
      return;
    }

    isExecuting = true;

    RequestModel requestModel = requestModelIterator.next();
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
        requestModelIterator.remove();
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
