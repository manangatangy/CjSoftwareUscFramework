package com.cjsoftware.ucs_platform_android.injected;

/**
 * Created by chris on 11/13/2017.
 */

public class InjectionInstrumentation {

  private static InjectionInstrumentation mInstance;

  private CreateComponentInterceptor mCreateComponentInterceptor = null;

  protected InjectionInstrumentation() {
  }

  public static InjectionInstrumentation getInstance() {
    if (mInstance == null) {
      mInstance = new InjectionInstrumentation();
    }

    return mInstance;
  }

  public CreateComponentInterceptor getCreateComponentInterceptor() {
    return mCreateComponentInterceptor;
  }

  public void setCreateComponentInterceptor(CreateComponentInterceptor createComponentInterceptor) {
    mCreateComponentInterceptor = createComponentInterceptor;
  }
}
