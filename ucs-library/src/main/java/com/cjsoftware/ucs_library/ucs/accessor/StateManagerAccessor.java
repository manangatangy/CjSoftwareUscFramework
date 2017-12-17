package com.cjsoftware.ucs_library.ucs.accessor;

import com.cjsoftware.ucs_library.ucs.AbstractUcsContract;

/**
 * Created by chris on 10/29/2017.
 */

public interface StateManagerAccessor<StateManagerT extends AbstractUcsContract.AbstractStateManager> {
  StateManagerT getStateManager();
}
