package com.cjsoftware.ucstestapp.ucsactivity;

import com.cjsoftware.library.ucs.BaseUcsContract;
import com.cjsoftware.library.ucs.UcsContract;

/**
 * Created by chris on 2/25/2018.
 */

@UcsContract
public interface UcsActivityContract extends BaseUcsContract {
    interface ScreenNavigation extends BaseScreenNavigationContract {
        void requestExit();
    }

    interface Ui extends BaseUiContract<StateManager> {
        void setButtonEnable(boolean enable);

        void setTextContent(String text);
    }

    interface Coordinator extends BaseCoordinatorContract<Ui, ScreenNavigation, StateManager> {
        void onUserPressedButton();

        void onUserChangedText(String newText);
    }

    interface StateManager extends BaseStateManagerContract {

    }
}
