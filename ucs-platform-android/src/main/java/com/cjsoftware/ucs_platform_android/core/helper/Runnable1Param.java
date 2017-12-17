package com.cjsoftware.ucs_platform_android.core.helper;

/**
 * Created by chris on 19/04/2015.
 */
public abstract class Runnable1Param<T1> implements Runnable {
  private final T1 mParam1;

  public Runnable1Param(T1 param) {
    this.mParam1 = param;
  }

  public T1 getParam1() {
    return mParam1;
  }

}
