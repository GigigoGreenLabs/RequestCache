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

  private void initComponent() {
    applicationComponent = DaggerApplicationComponent.builder()
        .applicationModule(new ApplicationModule(this))
        .build();
  }

  public ApplicationComponent getComponent() {
    return applicationComponent;
  }
}
