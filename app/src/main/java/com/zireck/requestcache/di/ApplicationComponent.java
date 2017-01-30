package com.zireck.requestcache.di;

import com.zireck.requestcache.activity.MainActivity;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = {
    ApplicationModule.class
}) public interface ApplicationComponent {
  void inject(MainActivity mainActivity);
}
