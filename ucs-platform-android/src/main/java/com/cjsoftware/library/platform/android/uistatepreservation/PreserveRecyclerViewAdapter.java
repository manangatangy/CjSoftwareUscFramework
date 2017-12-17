package com.cjsoftware.library.platform.android.uistatepreservation;

import com.cjsoftware.library.uistatepreservation.strategy.StatePreservationStrategy;

import android.support.v7.widget.RecyclerView;

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
