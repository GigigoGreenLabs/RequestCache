package com.zireck.requestcache.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.zireck.requestcache.library.cache.RequestQueue;
import com.zireck.requestcache.library.cache.sqlite.SqliteQueue;
import com.zireck.requestcache.library.executor.JobExecutor;
import com.zireck.requestcache.library.executor.PendingRequestsExecutor;
import com.zireck.requestcache.library.executor.RequestExecutor;
import com.zireck.requestcache.library.executor.ThreadExecutor;
import com.zireck.requestcache.library.model.RequestModel;
import com.zireck.requestcache.library.network.ApiService;
import com.zireck.requestcache.library.network.ApiServiceBuilder;
import com.zireck.requestcache.library.network.NetworkRequestManager;
import com.zireck.requestcache.library.network.RetrofitNetworkRequestManager;
import com.zireck.requestcache.library.util.logger.AndroidLogger;
import com.zireck.requestcache.library.util.serializer.GsonSerializer;
import com.zireck.requestcache.library.util.serializer.JsonSerializer;
import com.zireck.requestcache.library.util.logger.Logger;
import com.zireck.requestcache.library.util.sleeper.Sleeper;
import com.zireck.requestcache.library.util.sleeper.ThreadSleeper;
import java.util.List;

public class RequestCacheManager implements RequestCache {

  private static RequestCacheManager INSTANCE = null;

  private final ThreadExecutor threadExecutor;
  private final SharedPreferences sharedPreferences;
  private final Logger logger;
  private final Sleeper sleeper;
  private final JsonSerializer jsonSerializer;
  private final RequestQueue requestQueue;
  private final ApiServiceBuilder apiServiceBuilder;
  private final ApiService apiService;
  private final NetworkRequestManager networkRequestManager;
  private final RequestExecutor requestExecutor;

  private RequestCacheManager(Context context) {
    threadExecutor = JobExecutor.getInstance();
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    logger = new AndroidLogger();
    sleeper = new ThreadSleeper();
    jsonSerializer = new GsonSerializer();
    requestQueue = new SqliteQueue(context, jsonSerializer);
    apiServiceBuilder = new ApiServiceBuilder();
    apiService = apiServiceBuilder.build();
    networkRequestManager = new RetrofitNetworkRequestManager(threadExecutor, apiService, logger);
    requestExecutor =
        new PendingRequestsExecutor(threadExecutor, networkRequestManager, logger, sleeper);
  }

  public static RequestCacheManager getInstance(Context context) {
    if (INSTANCE == null) {
      INSTANCE = new RequestCacheManager(context);
    }

    return INSTANCE;
  }

  @Override public void setRequestIntervalTime(long intervalTimeInMillis) {
    requestExecutor.setIntervalTime(intervalTimeInMillis);
  }

  @Override public void enqueueRequest(RequestModel requestModel) {
    requestQueue.add(requestModel);
    requestQueue.persistToDisk();
  }

  @Override public void enqueueRequests(List<RequestModel> requestModels) {
    requestQueue.add(requestModels);
    requestQueue.persistToDisk();
  }

  @Override public boolean sendPendingRequests() {
    if (requestExecutor.isExecuting()) {
      logger.e("RequestExecutor is already in progress. Try later.");
      return false;
    }

    return requestExecutor.execute(requestQueue);
  }

  @Override public void clearRequestsCache() {
    requestQueue.clear();
  }
}
