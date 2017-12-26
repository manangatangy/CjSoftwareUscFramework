package com.cjsoftware.library.ucs.accessor;

import com.cjsoftware.library.ucs.BaseUcsContract.BaseStateManagerContract;

/**
 * Created by chris on 10/29/2017.
 */

public interface StateManagerAccessor<StateManagerT extends BaseStateManagerContract> {
  StateManagerT getStateManager();
}
