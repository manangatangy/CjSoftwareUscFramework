package com.cjsoftware.library.platform.android.ucs;

import com.cjsoftware.library.core.ObjectRegistry;
import com.cjsoftware.library.core.UserNavigationRequest;
import com.cjsoftware.library.core.UserNavigationRequestListener;
import com.cjsoftware.library.platform.android.preservable.AbstractPreservableFragment;
import com.cjsoftware.library.ucs.AbstractUcsContract;
import com.cjsoftware.library.ucs.AbstractUcsContract.AbstractScreenNavigation;
import com.cjsoftware.library.ucs.AbstractUcsContract.AbstractUi;
import com.cjsoftware.library.ucs.accessor.CoordinatorAccessor;
import com.cjsoftware.library.ucs.accessor.StateManagerAccessor;
import com.cjsoftware.library.ucs.binder.ScreenNavigationBinder;
import com.cjsoftware.library.ucs.binder.UiBinder;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

/**
 * @author chris
 * @date 30 Jul 2017
 */

public abstract class  AbstractUiFragment<UiT extends AbstractUi,
                                          CoordinatorT extends AbstractUcsContract.AbstractCoordinator,
                                          StateManagerT extends AbstractUcsContract.AbstractStateManager,
                                          NavigationT extends AbstractScreenNavigation,
                                          ComponentT>

    extends AbstractPreservableFragment<ComponentT>

    implements AbstractUi,
    UserNavigationRequestListener,
    AbstractScreenNavigation {

  // region Private fields
  private static final String STATE_COORDINATOR = "coordinator";
  private CoordinatorAccessor<CoordinatorT> mContractBroker;
  // endregion


  // region Android lifecycle

  @CallSuper
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    ObjectRegistry objectRegistry = getObjectRegistry();
    outState.putString(STATE_COORDINATOR, objectRegistry.put(mContractBroker));
  }

  // endregion


  // region private helper methods

  private CoordinatorAccessor<CoordinatorT> restoreCoordinator(Bundle savedState) {
    CoordinatorAccessor<CoordinatorT> coordinator = null;

    ObjectRegistry objectRegistry = getObjectRegistry();
    String coordinatorKey = savedState.getString(STATE_COORDINATOR);

    if (coordinatorKey != null) {
      coordinator = objectRegistry.get(coordinatorKey);
    }

    return coordinator;
  }

  // endregion


  // region protected helper methods

  protected CoordinatorT getCoordinator() {
    return mContractBroker.getCoordinator();
  }

  // endregion

  // region optional overrides

  @Override
  protected void onInitializeInstance(Bundle savedInstanceState) {
    super.onInitializeInstance(savedInstanceState);

    if (savedInstanceState == null) {

      mContractBroker = createContractBroker(getComponent());
      initializeStateManager(((StateManagerAccessor<StateManagerT>) mContractBroker).getStateManager());
    } else {

      mContractBroker = restoreCoordinator(savedInstanceState);

      if (mContractBroker == null) {
        mContractBroker = createContractBroker(getComponent());
        initializeStateManager(((StateManagerAccessor<StateManagerT>) mContractBroker).getStateManager());
      }
    }

  }

  @Override
  protected void onPreStatePreserve() {
    super.onPreStatePreserve();
    ((UiBinder<UiT>) mContractBroker).bindToImplementation(null);
    ((ScreenNavigationBinder<NavigationT>) mContractBroker).bindToImplementation(null);
  }


  @Override
  protected void onStateRestored() {
    super.onStateRestored();
    ((UiBinder<UiT>) mContractBroker).bindToImplementation((UiT) this);
    ((ScreenNavigationBinder<NavigationT>) mContractBroker).bindToImplementation((NavigationT) this);
  }

  @Override
  public void onUserNavigationRequest(UserNavigationRequest navigationRequest) {
    super.onUserNavigationRequest(navigationRequest);

    getCoordinator().onUserNavigationRequest(navigationRequest);

  }

  // endregion


  // region mandatory overrides

  @NonNull
  protected abstract CoordinatorAccessor<CoordinatorT> createContractBroker(@NonNull ComponentT component);

  protected abstract void initializeStateManager(@NonNull StateManagerT stateManager);

  // endregion


}
