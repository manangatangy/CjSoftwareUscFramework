package com.cjsoftware.library.ucs;

import com.cjsoftware.library.core.UserNavigationRequestListener;

/**
 * @author chris
 * @date 30 Jul 2017
 */

public interface BaseUcsContract {

  interface BaseScreenNavigationContract {
  }

  interface BaseUiContract {
  }

  interface BaseCoordinatorContract extends UserNavigationRequestListener {
    void onInitialize();
  }

  interface BaseStateManagerContract {
  }

}
