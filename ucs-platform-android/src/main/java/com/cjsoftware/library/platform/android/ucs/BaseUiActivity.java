package com.cjsoftware.library.platform.android.ucs;

import com.cjsoftware.library.core.ObjectRegistry;
import com.cjsoftware.library.core.UserNavigationRequest;
import com.cjsoftware.library.core.UserNavigationRequestListener;
import com.cjsoftware.library.platform.android.preservable.BasePreservableActivity;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseCoordinatorContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseScreenNavigationContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseStateManagerContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseUiContract;
import com.cjsoftware.library.ucs.ContractBroker;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

/**
 * @author chris
 * 30 Jul 2017
 * AbstractUiActivity adds Ucs (Ui, Coordinator, Statemanager - MVP by any other
 * name) to PreservableActivity
 */
public abstract class BaseUiActivity<UiT extends BaseUiContract<StateManagerT>,
        CoordinatorT extends BaseCoordinatorContract<UiT, NavigationT, StateManagerT>,
        StateManagerT extends BaseStateManagerContract,
        NavigationT extends BaseScreenNavigationContract,
        ComponentT>

        extends BasePreservableActivity<ComponentT>

        implements BaseUiContract<StateManagerT>,
        BaseScreenNavigationContract {


    // region Private fields

    private static final String STATE_CONTRACT_BROKER = "contractbroker";
    private ContractBroker<UiT, CoordinatorT, NavigationT, StateManagerT> mContractBroker;

    // In UCS, the navigation requests are always sent to the coordinator. The coordinator then decides what to do,
    // even if that means passing it to a hosted fragment (via the Ui)
    private UserNavigationRequestListener mUserNavigationRequestListener = new UserNavigationRequestListener() {
        @Override
        public void onUserNavigationRequest(UserNavigationRequest navigationRequest) {
            getCoordinator().onUserNavigationRequest(navigationRequest);
        }
    };

    // endregion

    // region Android lifecycle

    @CallSuper
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ObjectRegistry objectRegistry = getObjectRegistry();
        outState.putString(STATE_CONTRACT_BROKER, objectRegistry.put(mContractBroker));
    }

    // endregion

    // region private helper methods


    private ContractBroker<UiT, CoordinatorT, NavigationT, StateManagerT> restoreCoordinator(Bundle savedState) {
        ContractBroker<UiT, CoordinatorT, NavigationT, StateManagerT> contractBroker = null;

        ObjectRegistry objectRegistry = getObjectRegistry();
        String coordinatorKey = savedState.getString(STATE_CONTRACT_BROKER);

        if (coordinatorKey != null) {
            contractBroker = objectRegistry.get(coordinatorKey);
        }

        return contractBroker;
    }
    // endregion

    // region protected helper methods

    /**
     * Get the Coordinator
     */
    protected CoordinatorT getCoordinator() {
        return mContractBroker.getCoordinator();
    }

    // endregion

    // region optional overrides


    @Override
    protected void onPreconfigure(Bundle savedInstanceState) {
        super.onPreconfigure(savedInstanceState);
        if (savedInstanceState == null) {

            mContractBroker = createContractBroker(getComponent());
            initializeStateManager(mContractBroker.getStateManager());

        } else {

            mContractBroker = restoreCoordinator(savedInstanceState);

            if (mContractBroker == null) {
                mContractBroker = createContractBroker(getComponent());
                initializeStateManager(mContractBroker.getStateManager());
            } else {
                updateStateManager(mContractBroker.getStateManager());
            }
        }
    }

    @Override
    protected void onInitializeInstance(Bundle savedInstanceState) {
        super.onInitializeInstance(savedInstanceState);

        if (savedInstanceState == null) {
            getCoordinator().onInitialize();
        } else {
            getCoordinator().onUpdate();
        }
    }

    @Override
    protected void onBeforeStatePreserve() {
        super.onBeforeStatePreserve();
        setUserNavigationRequestListener(null);
        mContractBroker.bindScreenNavigation(null);
        mContractBroker.bindUi(null);
    }


    @Override
    protected void onAfterStateRestored() {
        super.onAfterStateRestored();

        mContractBroker.bindScreenNavigation(this);
        mContractBroker.bindUi(this);

        setUserNavigationRequestListener(mUserNavigationRequestListener);
    }

    /**
     * Perform any updates required in the state manager when a Ucs Stack has resumed from
     * interruption.
     */
    @CallSuper
    protected void updateStateManager(@NonNull StateManagerT stateManager) {
    }


    // endregion

    // region mandatory overrides

    /**
     * Obtain an instance of the contract broker. The contract broker implementation is generated by
     * the Ucs annotation
     * processor from the UcsContract interface.
     */
    @NonNull
    protected abstract ContractBroker<UiT, CoordinatorT, NavigationT, StateManagerT> createContractBroker(@NonNull ComponentT component);

    /**
     * Set the state manager to a fresh new instance state. This happens once when the Ucs stack is
     * first initialized.
     */
    protected abstract void initializeStateManager(@NonNull StateManagerT stateManager);

    // endregion

}
