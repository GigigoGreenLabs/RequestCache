package com.zireck.requestcache.library.network;

import io.reactivex.Observable;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface ApiService {
  @GET Observable<Response<ResponseBody>> requestGet(@HeaderMap Map<String, String> headers,
      @Url String url, @QueryMap(encoded = true) Map<String, String> query);

  @POST Observable<Response<ResponseBody>> requestPost(@HeaderMap Map<String, String> headers,
      @Url String url, @Body Object body, @QueryMap(encoded = true) Map<String, String> query);
}
