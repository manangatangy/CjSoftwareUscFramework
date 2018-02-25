package com.cjsoftware.ucstestapp.application._di;

import com.cjsoftware.library.core.ObjectRegistry;
import com.cjsoftware.library.platform.android.core.facility.DefaultObjectRegistry;
import com.cjsoftware.library.platform.android.core.facility.MainLooper;
import com.cjsoftware.library.platform.android.core.facility.MainLooperExecutor;
import com.cjsoftware.ucstestapp.application.Application;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by chris on 11/04/2015.
 */
@Module
public class ApplicationModule {

  private final Application mApplication;

  public ApplicationModule(Application application) {
    this.mApplication = application;
  }

  @Provides
  @Singleton
  public Application application() {
    return this.mApplication;
  }

  @Provides
  @Singleton
  ObjectRegistry getObjectRegistry() {
    return new DefaultObjectRegistry();
  }

  @Provides
  @Singleton
  Executor getExecutor() {
    return Executors.newCachedThreadPool();
  }

  @Provides
  @Singleton
  @MainLooper
  Executor getUiExecutor() {
    return new MainLooperExecutor();
  }

}
