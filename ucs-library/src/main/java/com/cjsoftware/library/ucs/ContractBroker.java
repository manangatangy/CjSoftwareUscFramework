package com.cjsoftware.library.ucs;

import com.cjsoftware.library.ucs.BaseUcsContract.BaseCoordinatorContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseUiContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseScreenNavigationContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseStateManagerContract;

/**
 * Created by chris on 2/24/2018.
 */

public interface ContractBroker<UiT extends BaseUiContract<StateManagerT>,
        CoordinatorT extends BaseCoordinatorContract<UiT, ScreenNavigationT, StateManagerT>,
        ScreenNavigationT extends BaseScreenNavigationContract,
        StateManagerT extends BaseStateManagerContract> {


    <UiImplementT extends BaseUiContract> void  bindUi(UiImplementT ui);

    <NavigationImplementT extends BaseScreenNavigationContract> void bindScreenNavigation(NavigationImplementT screenNavigation);


    UiT getUi();

    ScreenNavigationT getScreenNavigation();

    CoordinatorT getCoordinator();

    StateManagerT getStateManager();
}
