package com.zireck.requestcache;

import com.zireck.requestcache.di.ApplicationModule;

public class RobolectricTestApp extends RequestCacheApplication {

  private ApplicationModule applicationModule;

  @Override ApplicationModule getApplicationModule() {
    if (applicationModule == null) {
      return super.getApplicationModule();
    }

    return applicationModule;
  }

  public void setApplicationModule(ApplicationModule applicationModule) {
    this.applicationModule = applicationModule;
    initComponent();
  }
}
