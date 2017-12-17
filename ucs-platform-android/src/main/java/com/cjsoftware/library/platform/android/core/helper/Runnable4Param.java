package com.cjsoftware.library.platform.android.core.helper;

/**
 * Created by chris on 11/6/2016.
 */

public abstract class Runnable4Param<PARAM1, PARAM2, PARAM3, PARAM4> extends Runnable3Param<PARAM1, PARAM2, PARAM3> {
  private final PARAM4 mParam4;

  public Runnable4Param(PARAM1 param1, PARAM2 param2, PARAM3 param3, PARAM4 param4) {
    super(param1, param2, param3);
    mParam4 = param4;
  }

  public PARAM4 getParam4() {
    return mParam4;
  }
}
