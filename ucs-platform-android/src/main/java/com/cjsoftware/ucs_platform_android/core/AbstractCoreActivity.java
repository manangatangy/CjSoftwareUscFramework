package com.cjsoftware.ucs_platform_android.core;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cjsoftware.ucs_platform_android.core.contract.NestedHost;
import com.cjsoftware.ucs_library.core.UserNavigationRequestListener;
import com.cjsoftware.ucs_library.core.UserNavigationRequestProvider;
import com.cjsoftware.ucs_library.core.UserNavigationRequest;

import java.lang.ref.WeakReference;

/**
 * Created by chris on 10/29/2017.
 * Provides "unified" Fragment/Activity lifecycle
 * Adds UserNavigation support (abstract back and up heirarchy handling)
 * Adds Nestedhost support (ability to recurse parent from fragment to activity)
 * Adds findFirstImplementationOf (ability to search for an implementation of and interface; built on NestedHost)
 */

public abstract class AbstractCoreActivity
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
   * Pass the back pressed event to the active user navigation handler if one exists. Super is never called. It is
   * the responsibility of the listener to ensure correct action is taken.
   */
  @Override
  public void onBackPressed() {
    notifyUserNavigationRequest(UserNavigationRequest.NAVIGATE_BACK);
  }


  // endregaion

  // region Protected helper methods

  /**
   * Traverse up the nested host heirarchy looking for the first implementation of the interface specified in
   * the parameter.
   *
   * @return The first class implementing the interface or null if not found.
   */
  @Nullable
  protected <InterfaceT> InterfaceT findFirstImplementationOf(@NonNull Class<?> interfaceT) {
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
   * Get the parent of this component. Activities can't be nested in another component so it is always null and acts as
   * an effective search terminator.
   */
  @Override
  public NestedHost getParentNestedHost() {
    return null;
  }

  /**
   * Set the User Navigation Request Listener
   */
  @Override
  public void setUserNavigationRequestListener(@Nullable UserNavigationRequestListener listener) {
    mUserNavigationRequestListener = new WeakReference<>(listener);
  }

  /**
   * Get the current User Navigation Request Listener
   */
  @Nullable
  @Override
  public UserNavigationRequestListener getUserNavigationRequestListener() {
    return mUserNavigationRequestListener.get();
  }

  // endregion

  // region Optional overrides

  /**
   * Perform any activity configuration required before content is set. This is where you would specify any special
   * window options etc
   */
  @CallSuper
  protected void onPreconfigure() {
  }

  /**
   * Perform any post-view binding actions. Do not set view default values here. The default values should be in
   * the Model (set during construction/injection) and will be applied to the views in the presenter redraw. You
   * might want to do things like constructing and setting adapter view adapters here.
   */
  @CallSuper
  protected void onBound(@Nullable Bundle savedInstanceState) {
  }

  /**
   * Perform any Ui component specific initialization required.
   *
   * @Param newInstance - true if this is a new instance
   */
  @CallSuper
  protected void onInitializeInstance(Bundle savedInstanceState) {
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
