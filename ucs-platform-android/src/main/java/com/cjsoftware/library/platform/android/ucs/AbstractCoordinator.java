package com.cjsoftware.library.platform.android.ucs;


import com.cjsoftware.library.core.UserNavigationRequest;
import com.cjsoftware.library.ucs.AbstractUcsContract;
import com.cjsoftware.library.ucs.accessor.StateManagerAccessor;
import com.cjsoftware.library.ucs.binder.ScreenNavigationBinder;
import com.cjsoftware.library.ucs.binder.UiBinder;

import java.lang.ref.WeakReference;

/**
 * @author chris
 * @date 30 Jul 2017
 */

public abstract class AbstractCoordinator<UiT extends AbstractUcsContract.AbstractUi,
    StateManagerT extends AbstractUcsContract.AbstractStateManager,
    NavigationT extends AbstractUcsContract.AbstractScreenNavigation>

    implements AbstractUcsContract.AbstractCoordinator,
    UiBinder<UiT>,
    ScreenNavigationBinder<NavigationT>,
    StateManagerAccessor<StateManagerT> {

  private final StateManagerT mStateManager;

  private WeakReference<UiT> mUi = new WeakReference<>(null);
  private WeakReference<NavigationT> mNavigation = new WeakReference<>(null);

  public AbstractCoordinator(StateManagerT stateManager) {
    mStateManager = stateManager;
  }


  @Override
  public void bindToImplementation(NavigationT realization) {
    mNavigation = new WeakReference<NavigationT>(realization);
  }

  @Override
  public void bindToImplementation(UiT realization) {
    mUi = new WeakReference<>(realization);
  }

  @Override
  public StateManagerT getStateManager() {
    return mStateManager;
  }

  protected UiT getUi() {
    return mUi.get();
  }

  protected NavigationT getNavigation() {
    return mNavigation.get();
  }

  @Override
  public void onUserNavigationRequest(UserNavigationRequest navigationRequest) {
  }

}
