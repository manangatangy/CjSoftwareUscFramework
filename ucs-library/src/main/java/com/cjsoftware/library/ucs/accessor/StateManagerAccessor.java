package com.cjsoftware.library.ucs.accessor;

import com.cjsoftware.library.ucs.AbstractUcsContract;

/**
 * Created by chris on 10/29/2017.
 */

public interface StateManagerAccessor<StateManagerT extends AbstractUcsContract.AbstractStateManager> {
  StateManagerT getStateManager();
}