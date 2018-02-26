package com.cjsoftware.ucstestapp.ucsactivity;

import org.junit.Before;
import org.junit.Test;

import com.cjsoftware.library.core.UserNavigationRequest;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.Ui;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.Coordinator;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.ScreenNavigation;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.StateManager;
import com.cjsoftware.ucstestapp.ucsactivity.impl.UcsActivityCoordinator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Created by chris on 2/26/2018.
 */

public class TestUcsTestAppCoordinator {

    private Coordinator mCoordinator;

    private Ui mMockUi;
    private ScreenNavigation mMockScreenNavigation;
    private StateManager mMockStatemanager;


    @Before
    public void setup() {

        mMockUi = mock(Ui.class);
        mMockScreenNavigation = mock(ScreenNavigation.class);
        mMockStatemanager = mock(StateManager.class);

        mCoordinator = new UcsActivityCoordinator(mMockStatemanager);
        mCoordinator.bindUi(mMockUi);
        mCoordinator.bindScreenNavigation(mMockScreenNavigation);

    }

    @Test
    public void testOnInitialize() {
        // Verify coordinator sets correct initial state
        mCoordinator.onInitialize();
        verify(mMockUi).setButtonEnable(true);
        verify(mMockUi).setTextContent("Demo");
    }

    @Test
    public void testExitWhenUserPressesBack() {
        mCoordinator.onUserNavigationRequest(UserNavigationRequest.NAVIGATE_BACK);
        verify(mMockScreenNavigation).requestExit();
    }

    @Test
    public void testHomeAsUpIgnored() {
        mCoordinator.onUserNavigationRequest(UserNavigationRequest.NAVIGATE_UP_HEIRARCHY);
        verifyZeroInteractions(mMockUi);
    }

    @Test
    public void testOnUserPressedButton() {
        mCoordinator.onUserPressedButton();
        verify(mMockUi).setTextContent("");
        verify(mMockUi).setButtonEnable(false);
    }

    @Test
    public void testButtonDisabledWhenUserEntersEmptyString() {
        mCoordinator.onUserChangedText("");
        verify(mMockUi).setButtonEnable(false);
    }

    @Test
    public void testSpacesTrimmedFromText() {
        mCoordinator.onUserChangedText(" ");
        verify(mMockUi).setButtonEnable(false);
    }

    @Test
    public void testButtonEnabledWhenUserEntersNonEmptyString() {
        mCoordinator.onUserChangedText("A");
        verify(mMockUi).setButtonEnable(true);
    }

}
