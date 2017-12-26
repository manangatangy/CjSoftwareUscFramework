package com.cjsoftware.library.ucs.binder;

import com.cjsoftware.library.ucs.BaseUcsContract.BaseScreenNavigationContract;

/**
 * Created by chris on 10/29/2017.
 */

public interface ScreenNavigationBinder<BoundT extends BaseScreenNavigationContract> {
  void bindToImplementation(BoundT realization);
}
