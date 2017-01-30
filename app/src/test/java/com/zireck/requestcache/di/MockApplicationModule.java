package com.zireck.requestcache.di;

import com.zireck.requestcache.RequestCacheApplication;
import com.zireck.requestcache.library.RequestCache;

public class MockApplicationModule extends ApplicationModule {

  private RequestCache mockRequestCache;

  public MockApplicationModule(RequestCacheApplication requestCacheApplication,
      RequestCache mockRequestCache) {
    super(requestCacheApplication);
    this.mockRequestCache = mockRequestCache;
  }

  @Override RequestCache provideRequestCache() {
    return mockRequestCache;
  }
}
