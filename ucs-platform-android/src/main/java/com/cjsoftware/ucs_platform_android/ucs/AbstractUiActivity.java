package com.cjsoftware.ucs_platform_android.ucs;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import com.cjsoftware.ucs_platform_android.preservable.AbstractPreservableActivity;
import com.cjsoftware.ucs_library.core.ObjectRegistry;
import com.cjsoftware.ucs_library.core.UserNavigationRequest;
import com.cjsoftware.ucs_library.core.UserNavigationRequestListener;
import com.cjsoftware.ucs_library.ucs.AbstractUcsContract;
import com.cjsoftware.ucs_library.ucs.accessor.CoordinatorAccessor;
import com.cjsoftware.ucs_library.ucs.accessor.StateManagerAccessor;
import com.cjsoftware.ucs_library.ucs.binder.ScreenNavigationBinder;
import com.cjsoftware.ucs_library.ucs.binder.UiBinder;

/**
 * @author chris
 * @date 30 Jul 2017
 * AbstractUiActivity adds Ucs (Ui, Coordinator, Statemanager - MVP by any other name) to PreservableActivity
 */
public abstract class AbstractUiActivity<UiT extends AbstractUcsContract.AbstractUi,
    CoordinatorT extends AbstractUcsContract.AbstractCoordinator,
    StateManagerT extends AbstractUcsContract.AbstractStateManager,
    NavigationT extends AbstractUcsContract.AbstractScreenNavigation,
    ComponentT>

    extends AbstractPreservableActivity<ComponentT>

    implements AbstractUcsContract.AbstractUi,
    AbstractUcsContract.AbstractScreenNavigation {


  // region Private fields

  private static final String STATE_CONTRACT_BROKER = "contractbroker";
  private CoordinatorAccessor<CoordinatorT> mContractBroker;

  // In UCS, the navigation requests are always sent to the coordinator. The coordinator then decides what to do,
  // even if that means passing it to a hosted fragment (via the Ui)
  private UserNavigationRequestListener mUserNavigationRequestListener = new UserNavigationRequestListener() {
    @Override
    public void onUserNavigationRequest(UserNavigationRequest navigationRequest) {
      getCoordinator().onUserNavigationRequest(navigationRequest);
    }
  };

  // endregion

  // region Android lifecycle

  @CallSuper
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    ObjectRegistry objectRegistry = getObjectRegistry();
    outState.putString(STATE_CONTRACT_BROKER, objectRegistry.put(mContractBroker));
  }

// endregion

  // region private helper methods


  private CoordinatorAccessor<CoordinatorT> restoreCoordinator(Bundle savedState) {
    CoordinatorAccessor<CoordinatorT> coordinator = null;

    ObjectRegistry objectRegistry = getObjectRegistry();
    String coordinatorKey = savedState.getString(STATE_CONTRACT_BROKER);

    if (coordinatorKey != null) {
      coordinator = objectRegistry.get(coordinatorKey);
    }

    return coordinator;
  }
  // endregion

  // region protected helper methods

  /**
   * Get the Coordinator
   */
  protected CoordinatorT getCoordinator() {
    return ((CoordinatorAccessor<CoordinatorT>) mContractBroker).getCoordinator();
  }

  // endregion

  // region optional overrides

  /**
   * Perform any Ui component specific initialization required.
   *
   * @Param newInstance - true if this is a new instance
   */
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
    setUserNavigationRequestListener(null);
    ((UiBinder<UiT>) mContractBroker).bindToImplementation(null);
    ((ScreenNavigationBinder<NavigationT>) mContractBroker).bindToImplementation(null);
  }


  @Override
  protected void onStateRestored() {
    super.onStateRestored();
    ((UiBinder<UiT>) mContractBroker).bindToImplementation((UiT) this);
    ((ScreenNavigationBinder<NavigationT>) mContractBroker).bindToImplementation((NavigationT) this);

    setUserNavigationRequestListener(mUserNavigationRequestListener);
  }

  // endregion

  // region mandatory overrides

  /**
   * Obtain an instance of the contract broker. The contract broker implementation is generated by the Ucs annotation
   * processor from the UcsContract interface.
   *
   * @return Accessor for the Coordinator exposed by the contract broker
   */
  @NonNull
  protected abstract CoordinatorAccessor<CoordinatorT> createContractBroker(@NonNull ComponentT component);

  /**
   * Set the state manager to a fresh new instance state. This happens once when the Ucs stack is first initialized.
   *
   * @param stateManager - state manager to initialize.
   */
  protected abstract void initializeStateManager(@NonNull StateManagerT stateManager);


  // endregion

}