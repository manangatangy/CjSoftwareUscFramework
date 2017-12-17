package com.cjsoftware.library.platform.android.core.facility;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;


/**
 * @author chris
 * @date 27 Jul 2017
 * Executor that posts the runnable onto the main looper queue (Android Ui Thread).
 * When injecting using named injectors, please use the provided name INJECTOR_NAME.
 */

public class MainLooperExecutor implements Executor {
  public static final String INJECTOR_NAME = "mainLooperExecutor";

  private Handler mHandler = new Handler(Looper.getMainLooper());

  @Override
  public void execute(@NonNull Runnable command) {
    mHandler.post(command);
  }
}
