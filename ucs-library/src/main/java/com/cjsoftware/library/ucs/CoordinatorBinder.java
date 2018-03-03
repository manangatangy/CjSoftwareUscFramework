package com.cjsoftware.library.ucs;

/**
 * Created by chris on 3/3/2018.
 */
import com.cjsoftware.library.ucs.BaseUcsContract.*;

public interface CoordinatorBinder {

    <UiImpltementationT extends BaseUiContract> void bindUi(UiImpltementationT ui);

    <ScreenNavigationImplementationT extends BaseScreenNavigationContract> void bindScreenNavigation(ScreenNavigationImplementationT screenNavigation);

    <StateManagerImplementationT extends BaseStateManagerContract> void bindStateManager(StateManagerImplementationT stateManager);
}
