package com.zireck.requestcache.library.network;

import android.util.Log;
import com.zireck.requestcache.library.model.RequestModel;
import com.zireck.requestcache.library.util.MethodType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitNetworkRequestManager implements NetworkRequestManager {

  private static final String TAG = RetrofitNetworkRequestManager.class.getSimpleName();

  private final ApiService apiService;
  private Callback<ResponseBody> retrofitCallback;
  private NetworkResponseCallback networkResponseCallback;

  public RetrofitNetworkRequestManager(ApiService apiService) {
    this.apiService = apiService;
    setupRetrofitCallback();
  }

  @Override public void sendRequest(RequestModel requestModel,
      NetworkResponseCallback networkResponseCallback) {
    this.networkResponseCallback = networkResponseCallback;

    Call<ResponseBody> retrofitCall = getRetrofitCallFor(requestModel);

    if (retrofitCall == null) {
      Log.e(TAG, "Invalid Retrofit call");
      networkResponseCallback.onFailure();
      return;
    }

    retrofitCall.enqueue(retrofitCallback);
  }

  private void setupRetrofitCallback() {
    retrofitCallback = new Callback<ResponseBody>() {
      @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if (networkResponseCallback == null) {
          return;
        }

        if (response.isSuccessful()) {
          networkResponseCallback.onSuccess();
        } else {
          networkResponseCallback.onFailure();
        }
      }

      @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
        if (networkResponseCallback == null) {
          return;
        }

        networkResponseCallback.onFailure();
      }
    };
  }

  private Call<ResponseBody> getRetrofitCallFor(RequestModel requestModel) {
    if (requestModel == null) {
      Log.e(TAG, "Invalid request model");
      return null;
    }

    final String requestUrl = requestModel.getBaseUrl() + requestModel.getEndpoint();
    Call<ResponseBody> retrofitCall = null;

    if (requestModel.getMethodType() == MethodType.GET) {
      retrofitCall = handleGetRequest(requestModel, requestUrl);
    } else if (requestModel.getMethodType() == MethodType.POST) {
      retrofitCall = handlePostRequest(requestModel, requestUrl);
    }

    return retrofitCall;
  }

  private Call<ResponseBody> handleGetRequest(RequestModel requestModel, String requestUrl) {
    Call<ResponseBody> retrofitCall = null;

    if (requestModel.getQuery() != null) {
      retrofitCall = apiService.requestGet(requestUrl);
    } else {
      retrofitCall = apiService.requestGet(requestUrl, requestModel.getQuery());
    }

    return retrofitCall;
  }

  private Call<ResponseBody> handlePostRequest(RequestModel requestModel, String requestUrl) {
    Call<ResponseBody> retrofitCall = null;

    if (requestModel.getQuery() == null && requestModel.getBody() == null) {
      retrofitCall = apiService.requestPost(requestUrl);
    } else if (requestModel.getQuery() != null && requestModel.getBody() == null) {
      retrofitCall = apiService.requestPost(requestUrl, requestModel.getQuery());
    } else if (requestModel.getQuery() == null && requestModel.getBody() != null) {
      retrofitCall = apiService.requestPost(requestUrl, requestModel.getBody());
    } else if (requestModel.getQuery() != null && requestModel.getBody() != null) {
      retrofitCall =
          apiService.requestPost(requestUrl, requestModel.getQuery(), requestModel.getBody());
    }

    return retrofitCall;
  }
}
