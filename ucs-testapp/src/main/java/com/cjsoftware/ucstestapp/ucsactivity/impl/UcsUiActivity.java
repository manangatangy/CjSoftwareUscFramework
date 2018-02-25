package com.cjsoftware.ucstestapp.ucsactivity.impl;

import com.cjsoftware.library.platform.android.ucs.BaseUiActivity;
import com.cjsoftware.library.ucs.ContractBroker;
import com.cjsoftware.library.uistatepreservation.StatePreservationManager;
import com.cjsoftware.ucstestapp.application.Application;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.Coordinator;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.ScreenNavigation;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.StateManager;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.Ui;
import com.cjsoftware.ucstestapp.ucsactivity._di.DaggerUcsActivityComponent;
import com.cjsoftware.ucstestapp.ucsactivity._di.UcsActivityComponent;
import com.cjsoftware.ucstestapp.ucsactivity._di.UcsActivityModule;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by chris on 2/25/2018.
 */

public class UcsUiActivity extends BaseUiActivity<Ui, Coordinator, StateManager, ScreenNavigation, UcsActivityComponent>
        implements Ui, ScreenNavigation {

    @NonNull
    @Override
    protected UcsActivityComponent createComponent() {
        return DaggerUcsActivityComponent.builder()
                .applicationComponent(Application.getComponent())
                .ucsActivityModule(new UcsActivityModule())
                .build();
    }

    @Override
    protected void injectFields(@NonNull UcsActivityComponent component) {
        component.inject(this);
    }

    @Override
    protected StatePreservationManager createStatePreservationManager() {
        return null;
    }

    @NonNull
    @Override
    protected ContractBroker<Ui, Coordinator, ScreenNavigation, StateManager> createContractBroker(@NonNull UcsActivityComponent component) {
        return component.provideContractBroker();
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
