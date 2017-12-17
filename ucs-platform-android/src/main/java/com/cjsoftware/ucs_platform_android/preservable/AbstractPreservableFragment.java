package com.cjsoftware.ucs_platform_android.preservable;

import android.os.Bundle;
import android.support.annotation.CallSuper;

import com.cjsoftware.ucs_platform_android.core.helper.Runnable1Param;
import com.cjsoftware.ucs_platform_android.injected.AbstractInjectedFragment;
import com.cjsoftware.ucs_library.core.ObjectRegistry;
import com.cjsoftware.ucs_library.uistatepreservation.StatePreservationManager;

/**
 * Created by chris on 10/29/2017.
 */

public abstract class AbstractPreservableFragment<ComponentT>
    extends AbstractInjectedFragment<ComponentT> {

  // region Private Fields

  private static final String STATE_PRESERVATION_HANDLER = "viewstateretentionmanager";



  private StatePreservationManager mStatePreservationManager;

  // endregion

  // region Android Lifecycle

  @CallSuper
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    ObjectRegistry objectRegistry = getObjectRegistry();

    outState.putString(STATE_PRESERVATION_HANDLER, objectRegistry.put(mStatePreservationManager));

  }


  @Override
  public final void onResume() {
    super.onResume();

    getMainLooperExecutor().execute(new Runnable1Param<AbstractPreservableFragment>(this) {
      @Override
      public void run() {
        if (mStatePreservationManager != null) {
          mStatePreservationManager.restorePreservedFields(getParam1());
        }
        onStateRestored();
        onAttachViewListeners();
      }
    });
  }


  @Override
  public final void onPause() {
    super.onPause();

    onDetachViewListeners();
    onPreStatePreserve();
    if (mStatePreservationManager != null) {
      mStatePreservationManager.savePreservedFields(this);
    }
  }

  // endregion

  // region Private helper methods
  private StatePreservationManager restoreViewStateManager(Bundle savedState) {
    StatePreservationManager statePreservationManager = null;

    ObjectRegistry objectRegistry = getObjectRegistry();
    String statePreservationHandlerKey = savedState.getString(STATE_PRESERVATION_HANDLER);

    if (statePreservationHandlerKey != null) {
      statePreservationManager = objectRegistry.get(statePreservationHandlerKey);
    }

    return statePreservationManager;
  }

  // endregion



  // region Optional Overrides



  @Override
  protected void onInitializeInstance(Bundle savedInstanceState) {
    super.onInitializeInstance(savedInstanceState);

    if (savedInstanceState == null) {

      mStatePreservationManager = createStatePreservationHandler();

    } else {

      mStatePreservationManager = restoreViewStateManager(savedInstanceState);
      if (mStatePreservationManager == null) {
        mStatePreservationManager = createStatePreservationHandler();
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
   * Use this event to attach listeners to views (TextChange Listeners etc). With state preservation it will be important
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

  // region Mandatory Overrides

  /**
   * Get the state preservation manager. The manager is generated from @Preserve annotations on fields.
   */
  protected abstract StatePreservationManager createStatePreservationHandler();

  // endregion

}
