package com.cjsoftware.library.platform.android.injected;

/**
 * Created by chris on 11/13/2017.
 */

public interface CreateComponentInterceptor {
    Object interceptCreateComponent(Object creator, Object component);
}
