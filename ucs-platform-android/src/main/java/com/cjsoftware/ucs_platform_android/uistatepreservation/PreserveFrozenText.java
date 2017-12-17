package com.cjsoftware.ucs_platform_android.uistatepreservation;

import android.os.Parcelable;
import android.widget.TextView;

import com.cjsoftware.ucs_library.uistatepreservation.strategy.StatePreservationStrategy;

/**
 * @author chris
 * @date 26 Jul 2017
 */

public class PreserveFrozenText<OwnerT> implements StatePreservationStrategy<OwnerT, TextView> {

  private Parcelable mTextViewState;

  public void saveState(OwnerT owner, TextView view) {

    boolean freezeText = view.getFreezesText();
    view.setFreezesText(true);
    mTextViewState = view.onSaveInstanceState();
    view.setFreezesText(freezeText);
  }

  public void restoreState(OwnerT owner, TextView destination) {

    boolean freezeText = destination.getFreezesText();
    destination.setFreezesText(true);
    destination.onRestoreInstanceState(mTextViewState);
    destination.setFreezesText(freezeText);
    mTextViewState = null;
  }
}
