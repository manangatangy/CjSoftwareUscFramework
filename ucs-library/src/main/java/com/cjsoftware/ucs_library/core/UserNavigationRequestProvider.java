package com.cjsoftware.ucs_library.core;

/**
 * @author chris
 * @date 12 Aug 2017
 */

public interface UserNavigationRequestProvider {
  void setUserNavigationRequestListener(UserNavigationRequestListener listener);
  UserNavigationRequestListener getUserNavigationRequestListener();
}
