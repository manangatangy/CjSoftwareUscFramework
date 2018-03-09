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
