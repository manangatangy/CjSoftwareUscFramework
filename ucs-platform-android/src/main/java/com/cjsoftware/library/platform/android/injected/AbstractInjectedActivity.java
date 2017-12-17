package com.cjsoftware.library.platform.android.injected;

import com.cjsoftware.library.core.ObjectRegistry;
import com.cjsoftware.library.platform.android.core.AbstractCoreActivity;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * Created by chris on 11/13/2017.
 */

public abstract class AbstractInjectedActivity<ComponentT>
    extends AbstractCoreActivity {

  @Inject
  Lazy<ObjectRegistry> mObjectRegistry;
    private ComponentT mComponent;

  // region Lifecycle

  /**
   * Perform any activity configuration required before content is set. This is where you would specify any special
   * window options etc
   */
  @Override
  protected void onPreconfigure() {
    super.onPreconfigure();

    mComponent = createComponent();
    CreateComponentInterceptor createComponentInterceptor = InjectionInstrumentation.getInstance().getCreateComponentInterceptor();

    if (createComponentInterceptor != null) {
      mComponent = (ComponentT) createComponentInterceptor.interceptCreateComponent(this, mComponent);
    }

    injectFields(mComponent);
  }

  // endregion

  // region Protected helper methods

  /**
   * Get the Dagger component for this object
   */
  protected ComponentT getComponent() {
    return mComponent;
  }

  /**
   * Get the object registry.
   */
  @NonNull
  protected ObjectRegistry getObjectRegistry() {
    return mObjectRegistry.get();
  }

  // endregion


  // region Mandatory overrides

  /**
   * Instantiate the dagger component object to be used for providing implementation objects
   */
  @NonNull
  protected abstract ComponentT createComponent();

  /**
   * Call the injection method on the supplied dagger component to inject fields within this object
   */
  protected abstract void injectFields(@NonNull ComponentT component);

  // endregion

}
