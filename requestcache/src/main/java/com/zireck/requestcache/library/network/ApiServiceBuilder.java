package com.zireck.requestcache.library.network;

import retrofit2.Retrofit;

public class ApiServiceBuilder {

  private static final Class<ApiService> BASE_API_SERVICE_CLASS = ApiService.class;

  public ApiServiceBuilder() {

  }

  public ApiService build() {
    Retrofit retrofit = new Retrofit.Builder().build();
    return retrofit.create(BASE_API_SERVICE_CLASS);
  }
}
