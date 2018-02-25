package com.cjsoftware.ucstestapp.ucsactivity;

import com.cjsoftware.library.ucs.BaseUcsContract;
import com.cjsoftware.library.ucs.UcsContract;

/**
 * Created by chris on 2/25/2018.
 */

@UcsContract
public interface UcsActivityContract extends BaseUcsContract {
    interface ScreenNavigation extends BaseScreenNavigationContract {

    }

    interface Ui extends BaseUiContract<StateManager> {

    }

    interface Coordinator extends BaseCoordinatorContract<Ui, ScreenNavigation, StateManager> {

    }

    interface StateManager extends BaseStateManagerContract {

    }
}
