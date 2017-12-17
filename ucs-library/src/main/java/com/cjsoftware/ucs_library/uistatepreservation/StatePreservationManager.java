package com.cjsoftware.ucs_library.uistatepreservation;

/**
 * @author chris
 * @date 10 Oct 2017
 */

public interface StatePreservationManager<FieldOwnerT> {
  void savePreservedFields(FieldOwnerT fieldOwner);
  void restorePreservedFields(FieldOwnerT fieldOwner);
}
