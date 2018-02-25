package com.cjsoftware.ucstestapp.ucsactivity.impl;

import com.cjsoftware.library.platform.android.ucs.BaseUiActivity;
import com.cjsoftware.library.ucs.ContractBroker;
import com.cjsoftware.library.uistatepreservation.StatePreservationManager;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.*;
import com.cjsoftware.ucstestapp.ucsactivity._di.UcsActivityComponent;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by chris on 2/25/2018.
 */

public class UcsUiActivity extends BaseUiActivity<Ui,Coordinator,StateManager,ScreenNavigation, UcsActivityComponent> {
    @NonNull
    @Override
    protected UcsActivityComponent createComponent() {
        return null;
    }

    @Override
    protected void injectFields(@NonNull UcsActivityComponent component) {

    }

    @Override
    protected StatePreservationManager createStatePreservationManager() {
        return null;
    }

    @NonNull
    @Override
    protected ContractBroker<Ui, Coordinator, ScreenNavigation, StateManager> createContractBroker(@NonNull UcsActivityComponent component) {
        return null;
    }

    @Override
    protected void initializeStateManager(@NonNull StateManager stateManager) {

    }

    @Override
    protected int getLayoutResource() {
        return 0;
    }

    @Override
    protected void onBindViews(View layoutRoot) {

    }
}
