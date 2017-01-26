package com.zireck.requestcache.library.cache;

import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zireck.requestcache.library.model.RequestModel;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SharedPreferencesQueue implements RequestQueue {

  private static final String TAG = SharedPreferencesQueue.class.getSimpleName();
  private static final String KEY_PENDING_REQUEST_QUEUE = "PENDING_REQUEST_QUEUE";

  private final SharedPreferences sharedPreferences;
  private final Gson gson;
  private List<RequestModel> pendingRequestQueue =
      Collections.synchronizedList(new ArrayList<RequestModel>());
  private Iterator<RequestModel> pendingRequestQueueIterator;

  public SharedPreferencesQueue(SharedPreferences sharedPreferences, Gson gson) {
    this.sharedPreferences = sharedPreferences;
    this.gson = gson;
  }

  @Override public boolean isEmpty() {
    return pendingRequestQueue.isEmpty();
  }

  @Override public void add(RequestModel requestModel) {
    pendingRequestQueue.add(requestModel);
  }

  @Override public void add(List<RequestModel> requestModels) {
    pendingRequestQueue.addAll(requestModels);
  }

  @Override public void load() {
    String pendingRequestQueueString = sharedPreferences.getString(KEY_PENDING_REQUEST_QUEUE, "");
    if (pendingRequestQueueString.length() <= 0) {
      pendingRequestQueue = new ArrayList<>();
    } else {
      Type pendingRequestQueueType = new TypeToken<List<RequestModel>>() {}.getType();
      pendingRequestQueue = gson.fromJson(pendingRequestQueueString, pendingRequestQueueType);
    }

    pendingRequestQueueIterator = pendingRequestQueue.iterator();
  }

  @Override public boolean persist() {
    String pendingRequestQueueString = gson.toJson(pendingRequestQueue);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(KEY_PENDING_REQUEST_QUEUE, pendingRequestQueueString);

    return editor.commit();
  }

  @Override public boolean hasNext() {
    if (pendingRequestQueue == null || pendingRequestQueue.isEmpty()) {
      return false;
    }

    if (pendingRequestQueueIterator == null) {
      pendingRequestQueueIterator = pendingRequestQueue.iterator();
    }

    return pendingRequestQueueIterator.hasNext();
  }

  @Override public RequestModel next() {
    return pendingRequestQueueIterator == null ? null : pendingRequestQueueIterator.next();
  }

  @Override public void remove() {
    if (pendingRequestQueueIterator == null) {
      Log.e(TAG, "Cannot delete the current element when the iterator is null.");
      return;
    }

    pendingRequestQueueIterator.remove();
  }

  @Override public boolean clear() {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(KEY_PENDING_REQUEST_QUEUE, "");

    return editor.commit();
  }
}
