package com.cjsoftware.ucs_platform_android.uistatepreservation;

import android.support.v7.widget.RecyclerView;

import com.cjsoftware.ucs_library.uistatepreservation.strategy.StatePreservationStrategy;

/**
 * @author chris
 * @date 30 Sep 2017
 */

public class PreserveRecyclerViewAdapter<OwnerT> implements StatePreservationStrategy<OwnerT, RecyclerView> {

  private RecyclerView.Adapter<?> mAdapter;

  @Override
  public void saveState(OwnerT owner, RecyclerView source) {
    mAdapter = source.getAdapter();
  }

  @Override
  public void restoreState(OwnerT owner, RecyclerView destination) {
    destination.setAdapter(mAdapter);
  }
}
