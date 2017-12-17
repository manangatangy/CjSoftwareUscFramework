package com.cjsoftware.ucs_platform_android.uistatepreservation;

import android.widget.Adapter;
import android.widget.AdapterView;

import com.cjsoftware.ucs_library.uistatepreservation.strategy.StatePreservationStrategy;

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
