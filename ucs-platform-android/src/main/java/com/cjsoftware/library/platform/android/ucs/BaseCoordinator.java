package com.cjsoftware.library.platform.android.ucs;


import com.cjsoftware.library.core.UserNavigationRequest;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseCoordinatorContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseScreenNavigationContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseStateManagerContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseUiContract;
import com.cjsoftware.library.ucs.CoordinatorBinder;

import android.support.annotation.CallSuper;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

/**
 * @author chris
 * @date 30 Jul 2017
 */

public abstract class BaseCoordinator<UiT extends BaseUiContract,
        ScreenNavigationT extends BaseScreenNavigationContract,
        StateManagerT extends BaseStateManagerContract>

        implements BaseCoordinatorContract,
        CoordinatorBinder {

    private StateManagerT mStateManager;

    private WeakReference<UiT> mUi = new WeakReference<>(null);
    private WeakReference<ScreenNavigationT> mScreenNavigation = new WeakReference<>(null);

    @Inject
    public BaseCoordinator() {
    }

    protected StateManagerT getStateManager() {
        return mStateManager;
    }

    protected UiT getUi() {
        return mUi.get();
    }

    protected ScreenNavigationT getScreenNavigation() {
        return mScreenNavigation.get();
    }

    @Override
    public <UiImpltementationT extends BaseUiContract> void bindUi(UiImpltementationT ui) {
        mUi = new WeakReference<>((UiT) ui);
    }

    @Override
    public <ScreenNavigationImplementationT extends BaseScreenNavigationContract> void bindScreenNavigation(ScreenNavigationImplementationT screenNavigation) {
        mScreenNavigation = new WeakReference<>((ScreenNavigationT) screenNavigation);
    }

    @Override
    public <StateManagerImplementationT extends BaseStateManagerContract> void bindStateManager(StateManagerImplementationT stateManager) {
        mStateManager = (StateManagerT) stateManager;
    }

    @CallSuper
    @Override
    public void onUserNavigationRequest(UserNavigationRequest navigationRequest) {
    }

    @CallSuper
    @Override
    public void onInitialize() {
    }

    @CallSuper
    @Override
    public void onUpdate() {
    }

}
