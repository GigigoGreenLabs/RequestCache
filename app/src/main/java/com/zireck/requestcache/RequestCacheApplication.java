package com.zireck.requestcache;

import android.app.Application;
import com.zireck.requestcache.di.ApplicationComponent;
import com.zireck.requestcache.di.ApplicationModule;
import com.zireck.requestcache.di.DaggerApplicationComponent;

public class RequestCacheApplication extends Application {

  private ApplicationComponent applicationComponent;

  @Override public void onCreate() {
    super.onCreate();
    initComponent();
  }

  public ApplicationComponent getComponent() {
    return applicationComponent;
  }

  ApplicationModule getApplicationModule() {
    return new ApplicationModule(this);
  }

  void initComponent() {
    applicationComponent = DaggerApplicationComponent.builder()
        .applicationModule(getApplicationModule())
        .build();
  }
}
