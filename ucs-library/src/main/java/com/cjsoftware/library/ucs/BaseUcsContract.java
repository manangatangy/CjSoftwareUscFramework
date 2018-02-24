package com.cjsoftware.library.ucs;

import com.cjsoftware.library.core.UserNavigationRequestListener;

/**
 * @author chris
 * @date 30 Jul 2017
 */

public interface BaseUcsContract {

  interface BaseScreenNavigationContract {
  }

  interface BaseUiContract<StateManagerT extends BaseStateManagerContract> {
  }

  interface BaseCoordinatorContract<UiT extends BaseUiContract, ScreenNavigationT extends BaseScreenNavigationContract, StateManagerT extends BaseStateManagerContract>
          extends UserNavigationRequestListener {

      // region "Physical" Architecture methods. Plumbing to realise the logical Architecture

      /**
       * Bind ui to implementation.
       */
      void bindUi(UiT ui);

      /**
       * Bind screenNavigation to implementation.
       */
      void bindScreenNavigation(ScreenNavigationT screenNavigation);

      // endregion

      /**
       * Called once at construction of Ucs Stack (new instance).
       */
      void onInitialize();

      /**
       * Called when Ucs Stack was interrupted then resumed (onResume in Android speak)
       */
      void onUpdate();

  }

  interface BaseStateManagerContract {
  }
}
