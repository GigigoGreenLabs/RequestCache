package com.zireck.requestcache.library.executor;

import android.util.Log;
import com.zireck.requestcache.library.cache.RequestQueue;
import com.zireck.requestcache.library.model.RequestModel;
import com.zireck.requestcache.library.network.NetworkRequestManager;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PendingRequestsExecutor implements RequestExecutor {

  private static final String TAG = PendingRequestsExecutor.class.getSimpleName();
  private static final int DEFAULT_REQUEST_INTERVAL_IN_MILLIS = 5000;
  private final ThreadExecutor threadExecutor;
  private final NetworkRequestManager networkRequestManager;
  private long intervalTimeInMillis;
  private boolean isExecuting = false;

  public PendingRequestsExecutor(ThreadExecutor threadExecutor,
      NetworkRequestManager networkRequestManager) {
    this.threadExecutor = threadExecutor;
    this.networkRequestManager = networkRequestManager;
    this.intervalTimeInMillis = DEFAULT_REQUEST_INTERVAL_IN_MILLIS;
  }

  @Override public boolean isExecuting() {
    return isExecuting;
  }

  @Override public void setIntervalTime(long intervalTimeInMillis) {
    if (intervalTimeInMillis < 0) {
      throw new IllegalArgumentException("Interval time must be a positive number");
    }

    this.intervalTimeInMillis = intervalTimeInMillis;
  }

  @SuppressWarnings("unchecked") @Override
  public void execute(RequestQueue requestQueue, DisposableObserver observer) {
    if (requestQueue == null) {
      Log.e(TAG, "Invalid request list given");
      return;
    }

    List<RequestModel> queue = Collections.synchronizedList(requestQueue.getQueue());
    Observable.zip(
        Observable.just(queue).flatMapIterable(request -> request),
        Observable.interval(intervalTimeInMillis, TimeUnit.MILLISECONDS),
        (item, timer) -> item)
        .doOnSubscribe(disposable -> requestQueue.loadToMemory())
        .doOnNext(request -> {
          networkRequestManager.getRequestStreamFor(request)
              .subscribe(response -> {
                if (response.isSuccessful()) {
                  queue.remove(request);
                }
              });
        })
        .doOnComplete(requestQueue::persistToDisk)
        .doOnComplete(() -> {
          Log.d(TAG, "No pending requests left.");
          isExecuting = false;
        })
        .subscribeOn(Schedulers.from(threadExecutor))
        .subscribeWith(observer);
  }
}
