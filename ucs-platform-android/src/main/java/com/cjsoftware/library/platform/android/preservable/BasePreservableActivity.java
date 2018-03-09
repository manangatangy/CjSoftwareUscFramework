package com.cjsoftware.library.platform.android.preservable;

import com.cjsoftware.library.core.ObjectRegistry;
import com.cjsoftware.library.platform.android.dagger.BaseDaggerActivity;
import com.cjsoftware.library.uistatepreservation.StatePreservationManager;

import android.os.Bundle;
import android.support.annotation.CallSuper;

/**
 * Extends the {@link BaseDaggerActivity} by adding support for view and field state preservation
 * using the state preservation framework
 */
public abstract class BasePreservableActivity<ComponentT>
        extends BaseDaggerActivity<ComponentT> {

    // region Private fields
    private static final String STATE_PRESERVATION_MANAGER_KEY = "statepreservationmanager";

    private StatePreservationManager mStatePreservationManager;

    // endregion

    // region Android lifecycle

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

    // region private helper methods




    private StatePreservationManager restoreViewStateManager(Bundle savedState) {
        StatePreservationManager uiStatePreservationManager = null;

        ObjectRegistry objectRegistry = getObjectRegistry();
        String statePreservationHandlerKey = savedState.getString(STATE_PRESERVATION_MANAGER_KEY);

        if (statePreservationHandlerKey != null) {
            uiStatePreservationManager = objectRegistry.get(statePreservationHandlerKey);
        }

        return uiStatePreservationManager;
    }

    // endregion

    // region optional overrides

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

    // region mandatory overrides


    /**
     * Get the state preservation manager. The manager is generated from @Preserve annotations on
     * fields.
     */
    protected abstract StatePreservationManager createStatePreservationManager();

    // endregion

}
