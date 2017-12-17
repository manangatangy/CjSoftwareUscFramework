package com.cjsoftware.ucs_library.core;

/**
 * @author chris
 * @date 12 Aug 2017
 */

public interface ObjectRegistry {
  <RegistryT> String put(RegistryT registryT);
  <RegistryT> RegistryT get(String key);
}
