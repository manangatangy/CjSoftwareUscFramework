package com.cjsoftware.library.platform.android.preservable;

import android.os.Bundle;
import android.support.annotation.CallSuper;

import com.cjsoftware.library.core.ObjectRegistry;
import com.cjsoftware.library.platform.android.dagger.BaseDaggerDialogFragment;
import com.cjsoftware.library.uistatepreservation.StatePreservationManager;

/**
 * Extends the {@link BaseDaggerDialogFragment} by adding support for view and field state preservation
 * using the state preservation framework
 */
public abstract class BasePreservableDialogFragment<ComponentT>
        extends BaseDaggerDialogFragment<ComponentT> {

    // region Private Fields

    private static final String STATE_PRESERVATION_MANAGER_KEY = "statepreservationmanager";


    private StatePreservationManager mStatePreservationManager;

    // endregion

    // region Android Lifecycle

    @CallSuper
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ObjectRegistry objectRegistry = getObjectRegistry();

        outState.putString(STATE_PRESERVATION_MANAGER_KEY, objectRegistry.put(mStatePreservationManager));

    }

    @Override
    protected void onBeforeAttachViewListeners() {
        super.onBeforeAttachViewListeners();
        if (mStatePreservationManager != null) {
            mStatePreservationManager.restorePreservedFields(this);
        }
        onAfterStateRestored();
    }

    @Override
    protected void onAfterDetachViewListeners() {
        super.onAfterDetachViewListeners();
        onBeforeStatePreserve();
        if (mStatePreservationManager != null) {
            mStatePreservationManager.savePreservedFields(this);
        }

    }

    // endregion

    // region Private helper methods
    private StatePreservationManager restoreViewStateManager(Bundle savedState) {
        StatePreservationManager statePreservationManager = null;

        ObjectRegistry objectRegistry = getObjectRegistry();
        String statePreservationHandlerKey = savedState.getString(STATE_PRESERVATION_MANAGER_KEY);

        if (statePreservationHandlerKey != null) {
            statePreservationManager = objectRegistry.get(statePreservationHandlerKey);
        }

        return statePreservationManager;
    }

    // endregion


    // region Optional Overrides


    @Override
    protected void onPreconfigure(Bundle savedInstanceState) {
        super.onPreconfigure(savedInstanceState);

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
     * Called after preserved state is restored. You can attach listeners to objects here (for
     * example RecyclerView
     * Adapter
     * event listeners or text change listeners)
     */
    @CallSuper
    protected void onAfterStateRestored() {
    }

    /**
     * Called just before the preserved state is saved. You can detach listeners to objects heres
     * (for example
     * RecyclerView Adapter
     * event listeners or text change listeners)
     */
    @CallSuper
    protected void onBeforeStatePreserve() {
    }

    // endregion

    // region Mandatory Overrides

    /**
     * Get the state preservation manager. The manager is generated from @Preserve annotations on
     * fields.
     */
    protected abstract StatePreservationManager createStatePreservationManager();

    // endregion

}
