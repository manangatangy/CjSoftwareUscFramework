package com.cjsoftware.ucstestapp.ucsactivity;

import com.cjsoftware.library.platform.android.core.helper.Runnable1Param;
import com.cjsoftware.library.platform.android.dagger.CreateComponentInterceptor;
import com.cjsoftware.library.platform.android.dagger.InjectionInstrumentation;
import com.cjsoftware.library.ucs.ContractBroker;
import com.cjsoftware.library.ucs.test.FakeContractBroker;
import com.cjsoftware.ucstestapp.R;
import com.cjsoftware.ucstestapp.application.Application;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.Coordinator;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.ScreenNavigation;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.StateManager;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.Ui;
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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by chris on 2/25/2018.
 */

public class TestUcsActivityUi implements CreateComponentInterceptor {

    @Rule
    public ActivityTestRule<UcsUiActivity> mActivityRule
            = new ActivityTestRule<>(UcsUiActivity.class, false, false);

    private FakeContractBroker<Ui, Coordinator, ScreenNavigation, StateManager> mFakeContractBroker;

    private Coordinator mMockCoordinator;
    private StateManager mMockStateManager;
    private Instrumentation mInstrumentation;

    @Override
    public Object interceptCreateComponent(Object creator, Object component) {
        return DaggerUcsActivityComponent.builder()
                .applicationComponent(Application.getComponent())
                .ucsActivityModule(new MockModule())
                .build();
    }

    class MockModule extends UcsActivityModule {

        @Override
        public ContractBroker<Ui, Coordinator, ScreenNavigation, StateManager> provideContractBroker(UcsActivityContract_ContractBroker contract_contractBroker) {
            return mFakeContractBroker;
        }
    }

    @Before
    public void setup() {

        mInstrumentation = InstrumentationRegistry.getInstrumentation();
        InjectionInstrumentation.getInstance().setCreateComponentInterceptor(this);

        mMockCoordinator = mock(Coordinator.class);
        mMockStateManager = mock(StateManager.class);

        mFakeContractBroker = new FakeContractBroker<>(mMockCoordinator, mMockStateManager);

        Intent startIntent = new Intent(mInstrumentation.getTargetContext(), UcsUiActivity.class);
        mActivityRule.launchActivity(startIntent);
    }

    @Test
    public void testButtonEnable() {


        onView(withId(R.id.ucsActivity_Button)).check(matches(not(isEnabled())));

        mInstrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                UcsActivityContract.Ui ui = mActivityRule.getActivity();
                ui.setButtonEnable(true);
            }
        });

        onView(withId(R.id.ucsActivity_Button)).check(matches(isEnabled()));
    }

    @Test
    public void testButtonClick() {

        mInstrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                UcsActivityContract.Ui ui = mActivityRule.getActivity();
                ui.setButtonEnable(true);
            }
        });

        onView(withId(R.id.ucsActivity_Button)).perform(click());
        verify(mMockCoordinator).onUserPressedButton();
    }

    @Test
    public void testOnUserChangeText() {
        onView(withId(R.id.ucsActivity_editText)).perform(replaceText("Hello World"));
        verify(mMockCoordinator).onUserChangedText("Hello World");
    }
}
