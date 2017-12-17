package com.cjsoftware.ucs_library.uistatepreservation.strategy;

/**
 * @author chris
 * @date 12 Sep 2017
 */

public interface StatePreservationStrategy<OwnerT, PreservedT>
    extends PreservationStrategy {
  void saveState(OwnerT owner, PreservedT source);
  void restoreState(OwnerT owner, PreservedT destination);
}
