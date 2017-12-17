package com.cjsoftware.library.ucs;

/**
 * @author chris
 * @date 29 Aug 2017
 */

public interface CachedMethodCall<InterfaceT> {
  void execute(InterfaceT implementation);

  int methodId();
}
