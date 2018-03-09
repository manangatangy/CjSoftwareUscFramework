package com.cjsoftware.library.uistatepreservation.strategy;

/**
 * @author chris
 * @date 08 Oct 2017
 */

public interface ValuePreservationStrategy<OwnerT, PreservedT>
    extends PreservationStrategy {
  void saveValue(OwnerT owner, PreservedT valueSource);
  PreservedT retrieveValue(OwnerT owner);
}
