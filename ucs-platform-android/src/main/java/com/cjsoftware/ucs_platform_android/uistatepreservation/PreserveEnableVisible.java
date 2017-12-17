package com.cjsoftware.ucs_platform_android.uistatepreservation;

import android.view.View;

import com.cjsoftware.ucs_library.uistatepreservation.strategy.StatePreservationStrategy;

/**
 * @author chris
 * @date 26 Jul 2017
 */

public class PreserveEnableVisible<OwnerT> implements StatePreservationStrategy<OwnerT, View> {

  private boolean mEnabled;
  private int mVisibility;

  @Override
  public void saveState(OwnerT owner, View view) {
    mEnabled = view.isEnabled();
    mVisibility = view.getVisibility();
  }

  @Override
  public void restoreState(OwnerT owner, View destination) {
    destination.setEnabled(mEnabled);
    destination.setVisibility(mVisibility);
  }

}
