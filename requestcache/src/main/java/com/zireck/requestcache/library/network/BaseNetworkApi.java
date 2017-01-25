package com.zireck.requestcache.library.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BaseNetworkApi {
  @GET("{endpoint}")
  Call<ResponseBody> requestGet(@Path(value = "endpoint", encoded = true) String endpoint);
}
