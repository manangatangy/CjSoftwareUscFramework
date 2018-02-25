package com.cjsoftware.ucstestapp.ucsactivity.impl;

import com.cjsoftware.library.platform.android.ucs.BaseCoordinator;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.ScreenNavigation;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.StateManager;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.Ui;

import javax.inject.Inject;

/**
 * Created by chris on 2/25/2018.
 */

public class UcsActivityCoordinator extends BaseCoordinator<Ui, StateManager, ScreenNavigation>
        implements UcsActivityContract.Coordinator {

    @Inject
    public UcsActivityCoordinator(StateManager stateManager) {
        super(stateManager);
    }
}
