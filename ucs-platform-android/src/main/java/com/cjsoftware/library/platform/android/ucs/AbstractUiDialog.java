package com.cjsoftware.library.platform.android.ucs;

import com.cjsoftware.library.core.ObjectRegistry;
import com.cjsoftware.library.platform.android.core.contract.NestedHost;
import com.cjsoftware.library.ucs.AbstractUcsContract;
import com.cjsoftware.library.ucs.accessor.CoordinatorAccessor;
import com.cjsoftware.library.ucs.accessor.StateManagerAccessor;
import com.cjsoftware.library.ucs.binder.ScreenNavigationBinder;
import com.cjsoftware.library.ucs.binder.UiBinder;
import com.cjsoftware.library.uistatepreservation.StatePreservationManager;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * @author chris
 * @date 30 Jul 2017
 */

public abstract class AbstractUiDialog<UiT extends AbstractUcsContract.AbstractUi,
    CoordinatorT extends AbstractUcsContract.AbstractCoordinator,
    NavigationT extends AbstractUcsContract.AbstractScreenNavigation,
    StateManagerT extends AbstractUcsContract.AbstractStateManager,
    ComponentT>
    extends DialogFragment
    implements
    AbstractUcsContract.AbstractUi,
    AbstractUcsContract.AbstractScreenNavigation,
    NestedHost {

  // region Private fields
  private static final String STATE_CONTRACT_BROKER = "contractbroker";
  private static final String STATE_PRESERVATION_HANDLER = "preservationhandler";

  @Inject
  Lazy<ObjectRegistry> mObjectRegistry;

  private StatePreservationManager mStatePreservationManager;

  private CoordinatorAccessor<CoordinatorT> mContractBroker;

  // endregion


  // region Android lifecycle
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ComponentT component = createComponent();
    injectFields(component);

    if (savedInstanceState == null) {

      mStatePreservationManager = createStatePreservationHandler();

      mContractBroker = createContractBroker(component);

      initializeStateManager(((StateManagerAccessor<StateManagerT>) mContractBroker).getStateManager());

    } else {

      mStatePreservationManager = statePreservationHandler(savedInstanceState);
      if (mStatePreservationManager == null) {
        mStatePreservationManager = createStatePreservationHandler();
      }

      mContractBroker = restoreContractBroker(savedInstanceState);

      if (mContractBroker == null) {
        mContractBroker = createContractBroker(component);
        initializeStateManager(((StateManagerAccessor<StateManagerT>) mContractBroker).getStateManager());
      }
    }

  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    onInitializeInstance(savedInstanceState == null);
  }

  @CallSuper
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    ObjectRegistry objectRegistry = getObjectRegistry();

    outState.putString(STATE_PRESERVATION_HANDLER, objectRegistry.put(mStatePreservationManager));
    outState.putString(STATE_CONTRACT_BROKER, objectRegistry.put(mContractBroker));

  }

  /**
   * Made final here to discourage life cycle abuse. If you feel you need to override this it likely means you need to
   * override a later lifecycle event or investigate your particular use case more thoroughly. Use onBound if you need
   * to attach listeners.. do not set initial values for the views
   */
  @Nullable
  @Override
  public final View onCreateView(LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
    int layoutResource = getLayoutResource();
    if (layoutResource > 0) {
      View view = inflater.inflate(layoutResource, container, false);
      onBindViews(view);
      onBound();
      return view;
    }
    return null;
  }

  @CallSuper
  @Override
  public void onResume() {
    super.onResume();
    if (mStatePreservationManager != null) {
      mStatePreservationManager.restorePreservedFields(this);
    }
    ((UiBinder<UiT>) mContractBroker).bindToImplementation((UiT) this);
    ((ScreenNavigationBinder<NavigationT>) mContractBroker).bindToImplementation((NavigationT) this);
  }

  @Override
  public void onPause() {
    super.onPause();
    ((UiBinder<UiT>) mContractBroker).bindToImplementation(null);
    ((ScreenNavigationBinder<NavigationT>) mContractBroker).bindToImplementation(null);
    if (mStatePreservationManager != null) {
      mStatePreservationManager.savePreservedFields(this);
    }
  }

  // endregion

  // region private helper methods

  private StatePreservationManager statePreservationHandler(Bundle savedState) {
    StatePreservationManager statePreservationManager = null;

    ObjectRegistry objectRegistry = getObjectRegistry();
    String statePreservationHandlerKey = savedState.getString(STATE_PRESERVATION_HANDLER);

    if (statePreservationHandlerKey != null) {
      statePreservationManager = objectRegistry.get(statePreservationHandlerKey);
    }

    return statePreservationManager;
  }

  private CoordinatorAccessor restoreContractBroker(Bundle savedState) {
    CoordinatorAccessor contractBroker = null;

    ObjectRegistry objectRegistry = getObjectRegistry();
    String contractBrokerKey = savedState.getString(STATE_CONTRACT_BROKER);

    if (contractBrokerKey != null) {
      contractBroker = objectRegistry.get(contractBrokerKey);
    }

    return contractBroker;
  }

  // endregion


  @Override
  public NestedHost getParentNestedHost() {
    NestedHost parentNestedHost = null;
    if (getParentFragment() instanceof NestedHost) {
      parentNestedHost = (NestedHost) getParentFragment();
    } else {
      if (getActivity() instanceof NestedHost) {
        parentNestedHost = (NestedHost) getActivity();
      }
    }
    return parentNestedHost;
  }

  // region protected helper methods
  @NonNull
  protected ObjectRegistry getObjectRegistry() {
    return mObjectRegistry.get();
  }

  protected CoordinatorT getCoordinator() {
    return ((CoordinatorAccessor<CoordinatorT>) mContractBroker).getCoordinator();
  }

  protected <InterfaceT> InterfaceT findFirstImplementationOf(Class<?> interfaceT) {
    NestedHost nestedHost = this;
    InterfaceT foundImplementation = null;
    while (nestedHost != null && foundImplementation == null) {
      if (interfaceT.isAssignableFrom(nestedHost.getClass())) {
        foundImplementation = (InterfaceT) nestedHost;
      } else {
        nestedHost = nestedHost.getParentNestedHost();
      }
    }

    return foundImplementation;
  }
  // endregion

  // region optional overrides

  /**
   * Perform any post-view binding actions. Do not set view default values here. The default values should be in
   * the Model (set during construction/injection) and will be applied to the views in the presenter redraw. You
   * might want to do things like constructing and setting adapter view adapters here.
   */
  @CallSuper
  protected void onBound() {
  }

  @CallSuper
  protected void onInitializeInstance(boolean newInstance) {
  }

  // endregion


  // region mandatory overrides
  @NonNull
  protected abstract ComponentT createComponent();

  protected abstract void injectFields(@NonNull ComponentT component);

  @NonNull
  protected abstract CoordinatorAccessor createContractBroker(@NonNull ComponentT component);


  protected abstract void initializeStateManager(@NonNull StateManagerT stateManager);

  /**
   * Returns the layout resource to be inflated as the View for the Fragment.
   */
  @LayoutRes
  protected abstract int getLayoutResource();

  /**
   * Bind views in layout to fields
   */
  protected abstract void onBindViews(View layoutRoot);


  protected abstract StatePreservationManager createStatePreservationHandler();

  // endregion


}
