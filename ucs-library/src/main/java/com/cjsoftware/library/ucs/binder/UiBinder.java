package com.cjsoftware.library.ucs.binder;

import com.cjsoftware.library.ucs.BaseUcsContract.BaseUiContract;

/**
 * Created by chris on 10/29/2017.
 */

public interface UiBinder<BoundT extends BaseUiContract> {
  void bindToImplementation(BoundT realization);
}
