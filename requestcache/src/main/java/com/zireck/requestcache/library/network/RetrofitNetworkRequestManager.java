package com.zireck.requestcache.library.network;

import com.zireck.requestcache.library.model.RequestModel;
import com.zireck.requestcache.library.util.MethodType;
import io.reactivex.Observable;
import java.util.HashMap;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class RetrofitNetworkRequestManager implements NetworkRequestManager {

  private static final String TAG = RetrofitNetworkRequestManager.class.getSimpleName();

  private final ApiService apiService;

  public RetrofitNetworkRequestManager(ApiService apiService) {
    this.apiService = apiService;
  }

  @Override public Observable<Response<ResponseBody>> getRequestStreamFor(RequestModel requestModel) {
    Observable<Response<ResponseBody>> request = null;
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
}
