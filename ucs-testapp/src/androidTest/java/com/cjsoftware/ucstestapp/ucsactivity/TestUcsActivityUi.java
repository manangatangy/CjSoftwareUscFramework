package com.cjsoftware.ucstestapp.ucsactivity;

import com.cjsoftware.library.platform.android.core.helper.Runnable1Param;
import com.cjsoftware.library.platform.android.dagger.CreateComponentInterceptor;
import com.cjsoftware.library.platform.android.dagger.InjectionInstrumentation;
import com.cjsoftware.library.ucs.ContractBroker;
import com.cjsoftware.ucstestapp.R;
import com.cjsoftware.ucstestapp.application.Application;
import com.cjsoftware.ucstestapp.ucsactivity._di.DaggerUcsActivityComponent;
import com.cjsoftware.ucstestapp.ucsactivity._di.UcsActivityModule;
import com.cjsoftware.ucstestapp.ucsactivity.impl.UcsUiActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by chris on 2/25/2018.
 */

public class TestUcsActivityUi implements CreateComponentInterceptor {

    @Rule
    public ActivityTestRule<UcsUiActivity> mActivityRule
            = new ActivityTestRule<>(UcsUiActivity.class, false, false);

    private ContractBroker<UcsActivityContract.Ui, UcsActivityContract.Coordinator, UcsActivityContract.ScreenNavigation, UcsActivityContract.StateManager> mMockContractBroker;
    private UcsActivityContract.Coordinator mMockCoordinator;
    private UcsActivityContract.ScreenNavigation mMockScreenNavigation;
    private UcsActivityContract.StateManager mMockStatemanager;



    @Override
    public Object interceptCreateComponent(Object creator, Object component) {
        return DaggerUcsActivityComponent.builder()
                .applicationComponent(Application.getComponent())
                .ucsActivityModule(new MockModule())
                .build();
    }

    class MockModule extends UcsActivityModule {

        @Override
        public ContractBroker<UcsActivityContract.Ui, UcsActivityContract.Coordinator, UcsActivityContract.ScreenNavigation, UcsActivityContract.StateManager> provideContractBroker(UcsActivityContract_ContractBroker contract_contractBroker) {
            return mMockContractBroker;
        }
    }

    @Before
    public void setup() {

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        InjectionInstrumentation.getInstance().setCreateComponentInterceptor(this);

        mMockCoordinator = mock(UcsActivityContract.Coordinator.class);
        mMockScreenNavigation = mock(UcsActivityContract.ScreenNavigation.class);
        mMockStatemanager = mock(UcsActivityContract.StateManager.class);

        mMockContractBroker = (ContractBroker<UcsActivityContract.Ui, UcsActivityContract.Coordinator, UcsActivityContract.ScreenNavigation, UcsActivityContract.StateManager>) mock(ContractBroker.class);
        when(mMockContractBroker.getCoordinator()).thenReturn(mMockCoordinator);
        when(mMockContractBroker.getStateManager()).thenReturn(mMockStatemanager);
        when(mMockContractBroker.getScreenNavigation()).thenReturn(mMockScreenNavigation);

        Intent startIntent = new Intent(instrumentation.getTargetContext(), UcsUiActivity.class);
        mActivityRule.launchActivity(startIntent);
    }

    @Test
    public void testButtonEnable() {
        UcsActivityContract.Ui ui = mActivityRule.getActivity();

        onView(withId(R.id.ucsActivity_Button)).check(matches(not(isEnabled())));

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        instrumentation.runOnMainSync(new Runnable1Param<UcsActivityContract.Ui>(ui) {
            @Override
            public void run() {
                getParam1().setButtonEnable(true);
            }
        });

        onView(withId(R.id.ucsActivity_Button)).check(matches(isEnabled()));
    }

}
