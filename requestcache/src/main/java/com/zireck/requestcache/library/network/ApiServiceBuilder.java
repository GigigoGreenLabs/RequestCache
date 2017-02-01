package com.zireck.requestcache.library.network;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceBuilder {

  private static final Class<ApiService> BASE_API_SERVICE_CLASS = ApiService.class;
  private static final String PLACEHOLDER_URL = "http://www.placeholder-url.com/";

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
    } else {
      retrofitBuilder.baseUrl(PLACEHOLDER_URL);
    }

    retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
    retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());

    if (true) {
    // TODO if (BuildConfig.DEBUG) {
      HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
      loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
      OkHttpClient.Builder client = new OkHttpClient.Builder();
      client.addInterceptor(loggingInterceptor);
      retrofitBuilder.client(client.build());
    }

    Retrofit retrofit = retrofitBuilder.build();
    return retrofit.create(BASE_API_SERVICE_CLASS);
  }
}
