package com.cjsoftware.library.platform.android.uistatepreservation;

import com.cjsoftware.library.uistatepreservation.strategy.ValuePreservationStrategy;

/**
 * @author chris
 * @date 08 Oct 2017
 */

public class PreserveValue<OwnerT, PreservedT> implements ValuePreservationStrategy<OwnerT, PreservedT> {
  PreservedT savedValue;

  @Override
  public void saveValue(OwnerT owner, PreservedT valueSource) {
    savedValue = valueSource;
  }

  @Override
  public PreservedT retrieveValue(OwnerT owner) {
    return savedValue;
  }
}
