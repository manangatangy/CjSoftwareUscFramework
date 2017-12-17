package com.cjsoftware.library.platform.android.core.helper;

/**
 * Created by chris on 4/8/2016.
 */
public abstract class Runnable3Param<PARAM1,PARAM2,PARAM3> extends Runnable2Param<PARAM1,PARAM2> {
  private final PARAM3 mParam3;

  public Runnable3Param(PARAM1 param1, PARAM2 param2, PARAM3 param3) {
    super(param1, param2);
    mParam3 = param3;
  }

  public PARAM3 getParam3() {
    return mParam3;
  }
}
