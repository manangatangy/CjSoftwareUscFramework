package com.cjsoftware.library.ucs;

import com.cjsoftware.library.ucs.BaseUcsContract.BaseCoordinatorContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseUiContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseScreenNavigationContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseStateManagerContract;

/**
 * Created by chris on 2/24/2018.
 */

public interface ContractBroker<UiT extends BaseUiContract,
        ScreenNavigationT extends BaseScreenNavigationContract,
        CoordinatorT extends BaseCoordinatorContract,
        StateManagerT extends BaseStateManagerContract> {

    UiT getUi();

    ScreenNavigationT getScreenNavigation();

    CoordinatorT getCoordinator();

    StateManagerT getStateManager();

    CoordinatorBinder getCoordinatorBinder();
}
