package com.cjsoftware.library.platform.android.core;

import com.cjsoftware.library.core.UserNavigationRequest;
import com.cjsoftware.library.core.UserNavigationRequestListener;
import com.cjsoftware.library.platform.android.core.contract.NestedHost;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by chris on 10/29/2017.
 * Base Unified Lifecycle Fragment
 * Provides "unified" Fragment/Activity lifecycle
 * Adds UserNavigation support (abstract back and up heirarchy handling)
 * Adds Nestedhost support (ability to recurse parent from fragment to activity)
 * Adds findFirstImplementationOf (ability to search for an implementation of and interface; built
 * on NestedHost)
 */

public abstract class BaseULFragment
        extends Fragment
        implements UserNavigationRequestListener,
        NestedHost {

    // region Private fields

    // endregion


    // region Android lifecycle

    @Override
    public final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onPreconfigure(savedInstanceState);
    }

    /**
     * Made final here to discourage life cycle abuse. If you feel you need to override this it
     * likely means you need to
     * override a later lifecycle event or investigate your particular use case more thoroughly. Use
     * onBound if you need
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
            onBound(savedInstanceState);
            return view;
        }
        return null;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onInitializeInstance(savedInstanceState);
    }

    @Override
    public final void onResume() {
        super.onResume();
        onBeforeAttachViewListeners();
        onAttachViewListeners();
    }

    @Override
    public final void onPause() {
        super.onPause();
        onDetachViewListeners();
        onAfterDetachViewListeners();
    }
    // endregion

    // region Public methods

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


    // endregion


    // region protected helper methods

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

    // endregion

    // region Optional overrides

    /**
     * Usually passed to fragment by activity (eg onBackPressed)
     *
     * @param navigationRequest - back or up heirarchy
     */
    @Override
    public void onUserNavigationRequest(UserNavigationRequest navigationRequest) {
    }

    /**
     * Perform any activity configuration required before content is set.
     */
    @CallSuper
    protected void onPreconfigure(Bundle savedInstanceState) {
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


    // region mandatory overrides

    /**
     * Returns the layout resource to be inflated as the View for the Fragment.
     */
    @LayoutRes
    protected abstract int getLayoutResource();

    /**
     * Bind views in layout to fields
     */
    protected abstract void onBindViews(View layoutRoot);

    // endregion


}
