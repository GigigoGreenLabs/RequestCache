package com.zireck.requestcache.library.network;

import com.zireck.requestcache.library.model.RequestModel;
import retrofit2.Retrofit;

public class ApiServiceFactory {

  private static final Class<BaseApiService> BASE_API_SERVICE_CLASS = BaseApiService.class;

  private ApiServiceFactory() {

  }

  public static BaseApiService from(RequestModel requestModel) {
    Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
    retrofitBuilder.baseUrl(requestModel.getBaseUrl());

    Retrofit retrofit = retrofitBuilder.build();
    return retrofit.create(BASE_API_SERVICE_CLASS);
  }
}
