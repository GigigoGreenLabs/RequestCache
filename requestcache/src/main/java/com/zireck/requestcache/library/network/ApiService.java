package com.zireck.requestcache.library.network;

import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface ApiService {
  @GET Call<ResponseBody> requestGet(@Url String url);

  @GET Call<ResponseBody> requestGet(@Url String url,
      @QueryMap(encoded = true) Map<String, String> query);

  @POST Call<ResponseBody> requestPost(@Url String url);

  @POST Call<ResponseBody> requestPost(@Url String url,
      @QueryMap(encoded = true) Map<String, String> query);

  @POST Call<ResponseBody> requestPost(@Url String url, @Body Object body);

  @POST Call<ResponseBody> requestPost(@Url String url,
      @QueryMap(encoded = true) Map<String, String> query, @Body Object body);
}
