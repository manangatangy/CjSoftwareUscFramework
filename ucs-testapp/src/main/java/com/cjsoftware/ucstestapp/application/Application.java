package com.cjsoftware.ucstestapp.application;

import com.cjsoftware.library.platform.android.uistatepreservation.PreserveAdapterViewAdapter;
import com.cjsoftware.library.platform.android.uistatepreservation.PreserveEnableVisible;
import com.cjsoftware.library.platform.android.uistatepreservation.PreserveFrozenText;
import com.cjsoftware.library.platform.android.uistatepreservation.PreserveRecyclerViewAdapter;
import com.cjsoftware.library.platform.android.uistatepreservation.PreserveValue;
import com.cjsoftware.library.uistatepreservation.rule.PreservationRule;
import com.cjsoftware.library.uistatepreservation.rule.PreservationRuleGroup;
import com.cjsoftware.ucstestapp.application._di.ApplicationComponent;
import com.cjsoftware.ucstestapp.application._di.ApplicationModule;
import com.cjsoftware.ucstestapp.application._di.DaggerApplicationComponent;

import android.support.multidex.MultiDexApplication;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

/**
 * Created by chris on 2/25/2018.
 */
@PreservationRuleGroup(
        rules = {
                @PreservationRule(
                        isInstanceOf = {Byte.class, Long.class, Integer.class, Boolean.class, Short.class, String.class, Character.class, Float.class, Double.class},
                        applyStrategies = {PreserveValue.class}),

                @PreservationRule(
                        isDescendantOf = {View.class},
                        applyStrategies = {PreserveEnableVisible.class}),

                @PreservationRule(
                        isDescendantOf = {TextView.class},
                        applyStrategies = {PreserveFrozenText.class}),

                @PreservationRule(
                        isDescendantOf = {AdapterView.class},
                        applyStrategies = {PreserveAdapterViewAdapter.class}),

                @PreservationRule(
                        isDescendantOf = {RecyclerView.class},
                        applyStrategies = {PreserveRecyclerViewAdapter.class})
        }
)
public class Application extends MultiDexApplication {

    private static ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationComponent = createApplicationComponent();
    }

    protected ApplicationComponent createApplicationComponent() {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public static ApplicationComponent getComponent() {
        return mApplicationComponent;
    }

}
