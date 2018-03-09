package com.cjsoftware.ucs_library_test.ucs;

import com.cjsoftware.library.ucs.BaseUcsContract.BaseCoordinatorContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseScreenNavigationContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseStateManagerContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseUiContract;
import com.cjsoftware.library.ucs.ContractBroker;
import com.cjsoftware.library.ucs.CoordinatorBinder;

/**
 * Created by chris on 2/26/2018.
 */

public class FakeContractBroker<UiT extends BaseUiContract,
        ScreenNavigationT extends BaseScreenNavigationContract,
        CoordinatorT extends BaseCoordinatorContract,
        StateManagerT extends BaseStateManagerContract>
        implements ContractBroker<UiT, ScreenNavigationT, CoordinatorT, StateManagerT> {


    private UiT mUi;
    private ScreenNavigationT mScreenNavigation;
    private CoordinatorT mCoordinator;
    private StateManagerT mStateManager;

    private FakeCoordinatorBinder mFakeCoordinatorBinder;

    public FakeContractBroker(CoordinatorT coordinator) {
        mCoordinator = coordinator;
        mFakeCoordinatorBinder = new FakeCoordinatorBinder();
    }

    @Override
    public UiT getUi() {
        return mUi;
    }

    @Override
    public ScreenNavigationT getScreenNavigation() {
        return mScreenNavigation;
    }

    @Override
    public CoordinatorT getCoordinator() {
        return mCoordinator;
    }

    @Override
    public StateManagerT getStateManager() {
        return mStateManager;
    }

    @Override
    public CoordinatorBinder getCoordinatorBinder() {
        return mFakeCoordinatorBinder;
    }

    private class FakeCoordinatorBinder implements CoordinatorBinder {
        @Override
        public <UiImpltementationT extends BaseUiContract> void bindUi(UiImpltementationT ui) {
            mUi = (UiT) ui;
        }

        @Override
        public <ScreenNavigationImplementationT extends BaseScreenNavigationContract> void bindScreenNavigation(ScreenNavigationImplementationT screenNavigation) {
            mScreenNavigation = (ScreenNavigationT) screenNavigation;
        }

        @Override
        public <StateManagerImplementationT extends BaseStateManagerContract> void bindStateManager(StateManagerImplementationT stateManager) {
            mStateManager = (StateManagerT) stateManager;
        }
    }
}
