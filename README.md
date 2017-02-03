# RequestCache [![Build Status](https://travis-ci.org/Zireck/RequestCache.svg?branch=master)](https://travis-ci.org/Zireck/RequestCache) [![codecov](https://codecov.io/gh/Zireck/RequestCache/branch/master/graph/badge.svg)](https://codecov.io/gh/Zireck/RequestCache)
*-Work in progress-*

See: [Reactive approach](https://github.com/Zireck/RequestCache/tree/reactive)

This is a library that allows you to cache and retry failed HTTP requests.
You can interact through this interface:
```java
public interface RequestCache {
  void setRequestIntervalTime(long intervalTimeInMillis);
  void enqueueRequest(RequestModel requestModel);
  void enqueueRequests(List<RequestModel> requestModels);
  boolean sendPendingRequests();
  void cancel();
  void clearRequestsCache();
}
```

Make sure to provide your requests this way:
```java
new RequestModel.Builder<String>()
  .methodType(MethodType.POST)
  .headers(new HashMap<String, String>())
  .baseUrl("https://api.github.com/")
  .endpoint("users/GigigoGreenLabs/repos")
  .query(new HashMap<String, String>())
  .body("This is the body")
  .build();
```

## ToDo
* Define a Retry Policy: Manual or Automatic.
* If automatic, it'd be a good idea to register a Broadcast Receiver subscribed to the following intent-filter *android.net.conn.CONNECTIVITY_CHANGE*
* Maybe use *AlarmManager* to schedule retries.
* ~~Alternative implementation of [RequestQueue](https://github.com/Zireck/RequestCache/blob/master/requestcache/src/main/java/com/zireck/requestcache/library/cache/RequestQueue.java) using SQLite (or even a plain old file) instead of [SharedPreferences](https://github.com/Zireck/RequestCache/blob/master/requestcache/src/main/java/com/zireck/requestcache/library/cache/SharedPreferencesQueue.java)~~. [Done](https://github.com/Zireck/RequestCache/blob/develop/requestcache/src/main/java/com/zireck/requestcache/library/cache/sqlite/SqliteQueue.java)
* ~~Alternative implementation using RxJava to avoid callback hell~~. [Done](https://github.com/Zireck/RequestCache/tree/reactive).
* ~~Add a cancel method~~. Done
