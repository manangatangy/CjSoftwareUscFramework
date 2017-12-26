package com.cjsoftware.library.ucs.accessor;

import com.cjsoftware.library.ucs.BaseUcsContract.BaseCoordinatorContract;

/**
 * Created by chris on 10/29/2017.
 */

public interface CoordinatorAccessor<CoordinatorT extends BaseCoordinatorContract> {
  CoordinatorT getCoordinator();
}
