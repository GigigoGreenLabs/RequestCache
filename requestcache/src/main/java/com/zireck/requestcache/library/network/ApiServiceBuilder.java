package com.zireck.requestcache.library.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceBuilder {

  private static final Class<ApiService> BASE_API_SERVICE_CLASS = ApiService.class;

  private final String baseUrl;

  public ApiServiceBuilder() {
    baseUrl = null;
  }

  public ApiServiceBuilder(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public ApiService build() {
    Retrofit.Builder retrofitBuilder = new Retrofit.Builder();

    if (baseUrl != null && baseUrl.length() > 0) {
      retrofitBuilder.baseUrl(baseUrl);
    }

    retrofitBuilder.addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = retrofitBuilder.build();
    return retrofit.create(BASE_API_SERVICE_CLASS);
  }
}