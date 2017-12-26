package com.cjsoftware.library.platform.android.core;


import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by chris on 10/29/2017.
 * Base Unified Lifecycle Dialog Fragment
 * Provides "unified" Fragment/Activity lifecycle
 */

public abstract class BaseULDialogFragment
        extends DialogFragment {


    // region Private fields

    // endregion


    // region Android lifecycle

    @Override
    public final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onPreconfigure();
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


    // endregion


    // region protected helper methods


    // endregion

    // region Optional overrides

    /**
     * Perform any configuration required before content is set.
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
