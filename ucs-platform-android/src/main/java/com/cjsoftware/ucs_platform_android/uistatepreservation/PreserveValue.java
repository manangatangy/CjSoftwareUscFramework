package com.cjsoftware.ucs_platform_android.uistatepreservation;

import com.cjsoftware.ucs_library.uistatepreservation.strategy.ValuePreservationStrategy;

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
