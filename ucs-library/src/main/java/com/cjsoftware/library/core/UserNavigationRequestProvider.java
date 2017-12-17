package com.cjsoftware.library.core;

/**
 * @author chris
 * @date 12 Aug 2017
 */

public interface UserNavigationRequestProvider {
  UserNavigationRequestListener getUserNavigationRequestListener();

    void setUserNavigationRequestListener(UserNavigationRequestListener listener);
}
