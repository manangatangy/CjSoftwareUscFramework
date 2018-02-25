package com.cjsoftware.ucstestapp.ucsactivity.impl;

import com.cjsoftware.library.core.UserNavigationRequest;
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

    @Override
    public void onUserNavigationRequest(UserNavigationRequest navigationRequest) {
        super.onUserNavigationRequest(navigationRequest);

        if (navigationRequest == UserNavigationRequest.NAVIGATE_BACK) {
            getNavigation().requestExit();
        }
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        getUi().setTextContent("Demo");
        getUi().setButtonEnable(true);
    }

    @Override
    public void onUserPressedButton() {
        getUi().setButtonEnable(false);
        getUi().setTextContent("");
    }

    @Override
    public void onUserChangedText(String newText) {
        getUi().setButtonEnable(newText.trim().length()>0);
    }
}
