package com.zireck.requestcache.library;

import android.util.Log;
import com.zireck.requestcache.library.network.BaseNetworkApi;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RequestCacheManager implements RequestCache {

  private static final String TAG = RequestCacheManager.class.getSimpleName();

  private static RequestCacheManager INSTANCE = null;

  private Retrofit retrofit;
  private BaseNetworkApi baseNetworkApi;

  public static RequestCacheManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new RequestCacheManager();
    }

    return INSTANCE;
  }

  private RequestCacheManager() {

  }

  @Override public void enqueueRequests() {

  }

  @Override public void sendPendingRequests() {

  }

  @Override public void clearRequestsCache() {

  }
}
