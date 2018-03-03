package com.cjsoftware.ucstestapp.ucsactivity._di;

import com.cjsoftware.library.ucs.ContractBroker;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.Coordinator;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.ScreenNavigation;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.StateManager;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.Ui;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract_ContractBroker;
import com.cjsoftware.ucstestapp.ucsactivity.impl.UcsActivityCoordinator;
import com.cjsoftware.ucstestapp.ucsactivity.impl.UcsActivityStateManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by chris on 2/25/2018.
 */
@Module
public class UcsActivityModule {

    @Provides
    public ContractBroker<Ui, ScreenNavigation, Coordinator, StateManager> provideContractBroker(UcsActivityContract_ContractBroker contract_contractBroker) {
        return contract_contractBroker;
    }

    @Provides
    public Coordinator provideCoordinator() {
        return new UcsActivityCoordinator();
    }

    @Provides
    public StateManager provideStateManager(UcsActivityStateManager stateManager) {
        return stateManager;
    }
}
