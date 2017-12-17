package com.cjsoftware.ucs_platform_android.preservable;

import android.os.Bundle;
import android.support.annotation.CallSuper;

import com.cjsoftware.ucs_platform_android.injected.AbstractInjectedActivity;
import com.cjsoftware.ucs_library.core.ObjectRegistry;
import com.cjsoftware.ucs_library.uistatepreservation.StatePreservationManager;

/**
 * Created by chris on 10/29/2017.
 */
public abstract class AbstractPreservableActivity<ComponentT>
    extends AbstractInjectedActivity<ComponentT> {

  // region Private fields
  private static final String STATE_PRESERVATION_HANDLER = "viewstateretentionmanager";

  private StatePreservationManager mStatePreservationManager;

  // endregion

  // region Android lifecycle

  @CallSuper
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    ObjectRegistry objectRegistry = getObjectRegistry();
    outState.putString(STATE_PRESERVATION_HANDLER, objectRegistry.put(mStatePreservationManager));
  }

  @Override
  protected final void onResume() {
    super.onResume();
    if (mStatePreservationManager != null) {
      mStatePreservationManager.restorePreservedFields(this);
    }
    onStateRestored();
    onAttachViewListeners();
  }

  @Override
  protected final void onPause() {
    super.onPause();
    onDetachViewListeners();
    onPreStatePreserve();
    if (mStatePreservationManager != null) {
      mStatePreservationManager.savePreservedFields(this);
    }
  }
  // endregion

  // region private helper methods


  private StatePreservationManager restoreViewStateManager(Bundle savedState) {
    StatePreservationManager uiStatePreservationManager = null;

    ObjectRegistry objectRegistry = getObjectRegistry();
    String statePreservationHandlerKey = savedState.getString(STATE_PRESERVATION_HANDLER);

    if (statePreservationHandlerKey != null) {
      uiStatePreservationManager = objectRegistry.get(statePreservationHandlerKey);
    }

    return uiStatePreservationManager;
  }

  // endregion

  // region optional overrides

  @Override
  protected void onInitializeInstance(Bundle savedInstanceState) {
    super.onInitializeInstance(savedInstanceState);

    if (savedInstanceState == null) {
      mStatePreservationManager = createStatePreservationManager();
    } else {
      mStatePreservationManager = restoreViewStateManager(savedInstanceState);
      if (mStatePreservationManager == null) {
        mStatePreservationManager = createStatePreservationManager();
      }
    }

  }

  /**
   * Called after preserved state is restored. You can attach listeners to objects here (for example RecyclerView
   * Adapter
   * event listeners or text change listeners)
   */
  @CallSuper
  protected void onStateRestored() {
  }

  /**
   * Called just before the preserved state is saved. You can detach listeners to objects heres (for example
   * RecyclerView Adapter
   * event listeners or text change listeners)
   */
  @CallSuper
  protected void onPreStatePreserve() {
  }

  /**
   * Use this event to attach listeners to views (TextChange Listeners etc). With state preservation it will be
   * important
   * to attach listeners at the correct point during resume to avoid false change events. onAttachViewListeners ensures
   * correct order.
   */
  @CallSuper
  protected void onAttachViewListeners() {
  }

  /**
   * Use this event to detach listeners from views (TextChange Listeners etc).
   */

  @CallSuper
  protected void onDetachViewListeners() {
  }

  // endregion

  // region mandatory overrides


  /**
   * Get the state preservation manager. The manager is generated from @Preserve annotations on fields.
   */
  protected abstract StatePreservationManager createStatePreservationManager();

  // endregion

}
