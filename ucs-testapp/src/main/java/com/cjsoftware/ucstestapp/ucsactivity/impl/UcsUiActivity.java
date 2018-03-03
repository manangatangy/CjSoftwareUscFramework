package com.cjsoftware.ucstestapp.ucsactivity.impl;

import com.cjsoftware.library.platform.android.ucs.BaseUiActivity;
import com.cjsoftware.library.ucs.ContractBroker;
import com.cjsoftware.library.uistatepreservation.Preserve;
import com.cjsoftware.library.uistatepreservation.StatePreservationManager;
import com.cjsoftware.ucstestapp.R;
import com.cjsoftware.ucstestapp.application.Application;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.Coordinator;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.ScreenNavigation;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.StateManager;
import com.cjsoftware.ucstestapp.ucsactivity.UcsActivityContract.Ui;
import com.cjsoftware.ucstestapp.ucsactivity._di.DaggerUcsActivityComponent;
import com.cjsoftware.ucstestapp.ucsactivity._di.UcsActivityComponent;
import com.cjsoftware.ucstestapp.ucsactivity._di.UcsActivityModule;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by chris on 2/25/2018.
 */

public class UcsUiActivity extends BaseUiActivity<Ui, ScreenNavigation, Coordinator, StateManager, UcsActivityComponent>
        implements Ui, ScreenNavigation {

    @Preserve
    EditText mEditText;

    @Preserve
    Button mButton;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            getCoordinator().onUserChangedText(s.toString());
        }
    };

    @NonNull
    @Override
    protected UcsActivityComponent createComponent() {
        return DaggerUcsActivityComponent.builder()
                .applicationComponent(Application.getComponent())
                .ucsActivityModule(new UcsActivityModule())
                .build();
    }

    @Override
    protected void injectFields(@NonNull UcsActivityComponent component) {
        component.inject(this);
    }

    @Override
    protected StatePreservationManager createStatePreservationManager() {
        return new UcsUiActivity_StatePreservationManager();
    }

    @NonNull
    @Override
    protected ContractBroker<Ui, ScreenNavigation, Coordinator, StateManager> createContractBroker(@NonNull UcsActivityComponent component) {
        return component.provideContractBroker();
    }

    @Override
    protected void initializeStateManager(@NonNull StateManager stateManager) {

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_ucs_activity;
    }

    @Override
    protected void onBindViews(View layoutRoot) {
        mButton = findViewById(R.id.ucsActivity_Button);
        mEditText = findViewById(R.id.ucsActivity_editText);
    }

    @Override
    protected void onAttachViewListeners() {
        super.onAttachViewListeners();
        mEditText.addTextChangedListener(mTextWatcher);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCoordinator().onUserPressedButton();
            }
        });

    }

    @Override
    protected void onDetachViewListeners() {
        super.onDetachViewListeners();
        mEditText.removeTextChangedListener(mTextWatcher);
    }

    @Override
    public void requestExit() {
        finish();
    }

    @Override
    public void setButtonEnable(boolean enable) {
        mButton.setEnabled(enable);
    }

    @Override
    public void setTextContent(String text) {
        mEditText.removeTextChangedListener(mTextWatcher);
        mEditText.setText(text);
        mEditText.addTextChangedListener(mTextWatcher);
    }
}
