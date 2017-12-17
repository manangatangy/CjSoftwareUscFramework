package com.cjsoftware.library.platform.android.uistatepreservation;

import com.cjsoftware.library.uistatepreservation.strategy.StatePreservationStrategy;

import android.widget.Adapter;
import android.widget.AdapterView;

/**
 * @author chris
 * @date 26 Jul 2017
 */

public class PreserveAdapterViewAdapter<OwnerT> implements StatePreservationStrategy<OwnerT, AdapterView> {

  private Adapter mAdapter;

  @Override
  public void saveState(OwnerT owner, AdapterView view) {
    mAdapter = view.getAdapter();
  }

  @Override
  public void restoreState(OwnerT owner, AdapterView destination) {
    destination.setAdapter(mAdapter);
    mAdapter = null;
  }
}
