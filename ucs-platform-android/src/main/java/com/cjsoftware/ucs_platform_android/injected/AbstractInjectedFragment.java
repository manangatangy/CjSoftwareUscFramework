package com.cjsoftware.ucs_platform_android.injected;

import android.support.annotation.NonNull;

import com.cjsoftware.ucs_platform_android.core.AbstractCoreFragment;
import com.cjsoftware.ucs_platform_android.core.facility.MainLooperExecutor;
import com.cjsoftware.ucs_library.core.ObjectRegistry;
import dagger.Lazy;

import java.util.concurrent.Executor;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by chris on 11/13/2017.
 */

public abstract class AbstractInjectedFragment<ComponentT>
    extends AbstractCoreFragment {

  private ComponentT mComponent;

  @Inject
  Lazy<ObjectRegistry> mObjectRegistry;

  @Inject @Named(MainLooperExecutor.INJECTOR_NAME)
  Lazy<Executor> mMainLooperExecutor;

  // region Lifecycle

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
   * Get the main looper (ui) executor. This executor unconditionally posts a runnable onto the event queue.
   * This is not the same behaviour as runOnUiThread
   */
  @NonNull
  protected Executor getMainLooperExecutor() {
    return mMainLooperExecutor.get();
  }

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
