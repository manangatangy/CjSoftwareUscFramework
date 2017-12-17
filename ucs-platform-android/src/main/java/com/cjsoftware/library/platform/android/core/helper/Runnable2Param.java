package com.cjsoftware.library.platform.android.core.helper;

/**
 * Created by chris on 19/04/2015.
 */
public abstract class Runnable2Param<T1, T2> implements Runnable {
  private final T1 mParam1;
  private final T2 mParam2;

  public Runnable2Param(T1 param1, T2 param2) {
    this.mParam1 = param1;
    this.mParam2 = param2;
  }

  public T1 getParam1() {
    return mParam1;
  }

  public T2 getParam2() {
    return mParam2;
  }
}
