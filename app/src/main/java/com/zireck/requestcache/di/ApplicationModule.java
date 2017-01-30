package com.zireck.requestcache.di;

import com.zireck.requestcache.RequestCacheApplication;
import com.zireck.requestcache.library.RequestCache;
import com.zireck.requestcache.library.RequestCacheManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module public class ApplicationModule {

  private final RequestCacheApplication requestCacheApplication;

  public ApplicationModule(RequestCacheApplication requestCacheApplication) {
    this.requestCacheApplication = requestCacheApplication;
  }

  @Provides @Singleton RequestCache provideRequestCache() {
    return RequestCacheManager.getInstance(requestCacheApplication.getApplicationContext());
  }
}
