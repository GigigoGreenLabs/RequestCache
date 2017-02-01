package com.zireck.requestcache.library.network;

import android.util.Log;
import com.zireck.requestcache.library.executor.ThreadExecutor;
import com.zireck.requestcache.library.model.RequestModel;
import com.zireck.requestcache.library.util.MethodType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class RetrofitNetworkRequestManager implements NetworkRequestManager, Runnable {

  private static final String TAG = RetrofitNetworkRequestManager.class.getSimpleName();

  private final ThreadExecutor threadExecutor;
  private final ApiService apiService;
  private RequestModel requestModel;
  private NetworkResponseCallback networkResponseCallback;

  public RetrofitNetworkRequestManager(ThreadExecutor threadExecutor, ApiService apiService) {
    this.threadExecutor = threadExecutor;
    this.apiService = apiService;
  }

  @Override public void sendRequest(RequestModel requestModel,
      NetworkResponseCallback networkResponseCallback) {
    if (networkResponseCallback == null) {
      Log.e(TAG, "Unable to deliver Retrofit request response. You must provide a valid callback.");
      return;
    }

    if (requestModel == null) {
      Log.e(TAG, "Invalid request model");
      networkResponseCallback.onFailure();
      return;
    }

    this.requestModel = requestModel;
    this.networkResponseCallback = networkResponseCallback;

    threadExecutor.execute(this);
  }

  @Override public void run() {
    Call<ResponseBody> retrofitCall = composeRequestFor(requestModel);
    if (retrofitCall == null) {
      Log.e(TAG, "Invalid Retrofit call");
      networkResponseCallback.onFailure();
      return;
    }

    try {
      Response<ResponseBody> response = retrofitCall.execute();
      handleResponse(response);
    } catch (IOException e) {
      e.printStackTrace();
      networkResponseCallback.onFailure();
    }
  }

  private Call<ResponseBody> composeRequestFor(RequestModel requestModel) {
    Call<ResponseBody> request = null;
    Map headers = requestModel.getHeaders() == null ? new HashMap<>() : requestModel.getHeaders();
    Map query = requestModel.getQuery() == null ? new HashMap<>() : requestModel.getQuery();

    if (requestModel.getMethodType() == MethodType.GET) {
      request = apiService.requestGet(headers, requestModel.getUrl(), query);
    } else if (requestModel.getMethodType() == MethodType.POST) {
      request =
          apiService.requestPost(headers, requestModel.getUrl(), requestModel.getBody(), query);
    }

    return request;
  }

  private void handleResponse(Response<ResponseBody> response) {
    if (response != null && response.isSuccessful()) {
      networkResponseCallback.onSuccess();
    } else {
      networkResponseCallback.onFailure();
    }
  }
}
