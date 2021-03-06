package com.cjsoftware.library.platform.android.dagger;

import com.cjsoftware.library.core.ObjectRegistry;
import com.cjsoftware.library.platform.android.core.BaseULActivity;
import com.cjsoftware.library.platform.android.core.BaseULFragment;
import com.cjsoftware.library.platform.android.core.facility.MainLooper;
import com.cjsoftware.library.platform.android.core.facility.MainLooperExecutor;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Lazy;

/**
 * Extends the AbstractCoreFragment by adding Dagger Dependency Injection
 */
public abstract class BaseDaggerFragment<ComponentT>
        extends BaseULFragment {

    @Inject
    Lazy<ObjectRegistry> mObjectRegistry;

    @Inject
    @MainLooper
    Lazy<Executor> mMainLooperExecutor;

    private ComponentT mComponent;

    // region Lifecycle

    /**
     * Obtains the Dagger component from {@link #createComponent()}
     * Calls {@link #injectFields(Object), passing it the Dagger Component}
     * See {@link BaseULActivity#onPreconfigure(Bundle)}
     */
    @Override
    protected void onPreconfigure(Bundle savedInstanceState) {
        super.onPreconfigure(savedInstanceState);

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
     * Get the main looper (ui) executor. This executor unconditionally posts a runnable onto the
     * event queue.
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
     * Call the injection method on the supplied dagger component to inject fields within this
     * object
     */
    protected abstract void injectFields(@NonNull ComponentT component);

    // endregion
}
