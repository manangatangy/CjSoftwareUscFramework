package com.cjsoftware.library.platform.android.core;

import com.cjsoftware.library.core.UserNavigationRequest;
import com.cjsoftware.library.core.UserNavigationRequestListener;
import com.cjsoftware.library.core.UserNavigationRequestProvider;
import com.cjsoftware.library.platform.android.core.contract.NestedHost;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by chris on 10/29/2017.
 * Base Unified Lifecycle Activity
 * Provides "unified" Fragment/Activity lifecycle
 * Adds UserNavigation support (abstract back and up heirarchy handling)
 * Adds Nestedhost support (ability to recurse parent from fragment to activity)
 * Adds findFirstImplementationOf (ability to search for an implementation of and interface; built
 * on NestedHost)
 */

public abstract class BaseULActivity
        extends AppCompatActivity
        implements UserNavigationRequestProvider,
        NestedHost {

    // region Private fields

    private WeakReference<UserNavigationRequestListener> mUserNavigationRequestListener = new WeakReference<>(null);

    // endregion

    // region Android Lifecycle
    @CallSuper
    @Override
    public final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onPreconfigure();

        int layoutResource = getLayoutResource();

        if (layoutResource > 0) {
            setContentView(layoutResource);
            onBindViews(findViewById(android.R.id.content)); // Retrieves the view created by setContentView
            onBound(savedInstanceState);
        }

        onInitializeInstance(savedInstanceState);
    }

    /**
     * Pass the back pressed event to the active user navigation handler if one exists. Super is
     * never called. It is
     * the responsibility of the listener to ensure correct action is taken.
     */
    @Override
    public void onBackPressed() {
        notifyUserNavigationRequest(UserNavigationRequest.NAVIGATE_BACK);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            notifyUserNavigationRequest(UserNavigationRequest.NAVIGATE_UP_HEIRARCHY);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected final void onResume() {
        super.onResume();
        onBeforeAttachViewListeners();
        onAttachViewListeners();
    }

    @Override
    protected final void onPause() {
        super.onPause();
        onDetachViewListeners();
        onAfterDetachViewListeners();
    }

    // endregaion

    // region Protected helper methods

    /**
     * Traverse up the nested host heirarchy looking for the first implementation of the interface
     * specified in
     * the parameter.
     *
     * @return The first class implementing the interface or null if not found.
     */
    @Nullable
    protected final <InterfaceT> InterfaceT findFirstImplementationOf(@NonNull Class<?> interfaceT) {
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

    /**
     * Helper method to pass a user navigation request to the listener (if not null).
     *
     * @param request - Back or Up Heirarchy
     */
    protected void notifyUserNavigationRequest(UserNavigationRequest request) {
        UserNavigationRequestListener listener = getUserNavigationRequestListener();
        if (listener != null) {
            listener.onUserNavigationRequest(request);
        }
    }


    // endregion

    // region Public methods

    /**
     * Get the parent of this component. Activities can't be nested in another component so it is
     * always null and acts as
     * an effective search terminator.
     */
    @Override
    public NestedHost getParentNestedHost() {
        return null;
    }

    /**
     * Get the current User Navigation Request Listener
     */
    @Nullable
    @Override
    public UserNavigationRequestListener getUserNavigationRequestListener() {
        return mUserNavigationRequestListener.get();
    }

    /**
     * Set the User Navigation Request Listener
     */
    @Override
    public void setUserNavigationRequestListener(@Nullable UserNavigationRequestListener listener) {
        mUserNavigationRequestListener = new WeakReference<>(listener);
    }

    // endregion

    // region Optional overrides

    /**
     * Perform any activity configuration required before content is set. This is where you would
     * specify any special
     * window options etc
     */
    @CallSuper
    protected void onPreconfigure() {
    }

    /**
     * Perform any post-view binding actions.
     * Do not set view default values here.
     * The defaultvalues should be in the Model (set during construction/injection)
     * and will be applied to the ui by the coordinator
     */
    @CallSuper
    protected void onBound(@Nullable Bundle savedInstanceState) {
    }

    /**
     * Perform any Ui component specific initialization required.
     */
    @CallSuper
    protected void onInitializeInstance(Bundle savedInstanceState) {
    }

    /**
     * Called before any view listeners are attached - used by view state perservation framework
     */
    @CallSuper
    protected void onBeforeAttachViewListeners() {
    }

    /**
     * Use this event to attach listeners from views (OnClick, TextChange Listeners etc).
     * Important to maintain correct order for the view preservation framework
     */
    @CallSuper
    protected void onAttachViewListeners() {
    }

    /**
     * Use this event to detach listeners from views (OnClick, TextChange Listeners etc).
     * * Important to maintain correct order for the view preservation framework
     */
    @CallSuper
    protected void onDetachViewListeners() {
    }

    /**
     * Called after all view listeners should be detached - used by view state perservation framework
     */
    @CallSuper
    protected void onAfterDetachViewListeners() {
    }

    // endregion

    // region Mandatory overrides

    /**
     * Get the layout resource id to be inflated as the View for the Fragment.
     */
    @LayoutRes
    protected abstract int getLayoutResource();

    /**
     * Bind views (attach listeners etc) in layoutRoot to fields in this object.
     */
    protected abstract void onBindViews(View layoutRoot);

    // endregion
}
