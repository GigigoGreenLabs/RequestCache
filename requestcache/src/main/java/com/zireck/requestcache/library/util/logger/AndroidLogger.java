package com.zireck.requestcache.library.util.logger;

import android.util.Log;

public class AndroidLogger implements Logger {

  private static final String TAG = AndroidLogger.class.getSimpleName();

  @Override public void d(String message) {
    Log.d(TAG, message);
  }

  @Override public void e(String message) {
    Log.e(TAG, message);
  }
}
