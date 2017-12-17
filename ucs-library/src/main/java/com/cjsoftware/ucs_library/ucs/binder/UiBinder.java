package com.cjsoftware.ucs_library.ucs.binder;

import com.cjsoftware.ucs_library.ucs.AbstractUcsContract;

/**
 * Created by chris on 10/29/2017.
 */

public interface UiBinder<BoundT extends AbstractUcsContract.AbstractUi> {
  void bindToImplementation(BoundT realization);
}