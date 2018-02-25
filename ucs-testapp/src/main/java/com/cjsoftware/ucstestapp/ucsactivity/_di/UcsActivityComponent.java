package com.cjsoftware.ucstestapp.ucsactivity._di;

import com.cjsoftware.library.ucs.ContractBroker;
import com.cjsoftware.ucstestapp.application._di.ApplicationComponent;
import com.cjsoftware.ucstestapp.shared.PerActivity;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.Coordinator;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.ScreenNavigation;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.StateManager;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.Ui;
import com.cjsoftware.ucstestapp.ucsactivity.impl.UcsUiActivity;

import dagger.Component;

/**
 * Created by chris on 2/25/2018.
 */
@PerActivity
@Component(
        dependencies = {ApplicationComponent.class},
        modules = {UcsActivityModule.class}
)
public interface UcsActivityComponent {

    void inject(UcsUiActivity ucsUiActivity);

    ContractBroker<Ui, Coordinator, ScreenNavigation, StateManager> provideContractBroker();
}
