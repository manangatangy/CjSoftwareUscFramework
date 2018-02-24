package com.cjsoftware.library.platform.android.ucs;


import com.cjsoftware.library.core.UserNavigationRequest;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseCoordinatorContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseScreenNavigationContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseStateManagerContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseUiContract;

import android.support.annotation.CallSuper;

import java.lang.ref.WeakReference;

/**
 * @author chris
 * @date 30 Jul 2017
 */

public abstract class BaseCoordinator<UiT extends BaseUiContract<StateManagerT>,
        StateManagerT extends BaseStateManagerContract,
        NavigationT extends BaseScreenNavigationContract>

        implements BaseCoordinatorContract<UiT, NavigationT, StateManagerT> {

    private final StateManagerT mStateManager;

    private WeakReference<UiT> mUi = new WeakReference<>(null);
    private WeakReference<NavigationT> mNavigation = new WeakReference<>(null);

    public BaseCoordinator(StateManagerT stateManager) {
        mStateManager = stateManager;
    }


    @Override
    public void bindUi(UiT ui) {
        mUi = new WeakReference<>(ui);
    }

    @Override
    public void bindScreenNavigation(NavigationT screenNavigation) {
        mNavigation = new WeakReference<>(screenNavigation);
    }


    protected StateManagerT getStateManager() {
        return mStateManager;
    }

    protected UiT getUi() {
        return mUi.get();
    }

    protected NavigationT getNavigation() {
        return mNavigation.get();
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
