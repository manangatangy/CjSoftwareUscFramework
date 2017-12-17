package com.cjsoftware.library.platform.android.core.facility;

import com.cjsoftware.library.core.ObjectRegistry;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Chris James
 * @date 2 Mar 2017
 */
public class DefaultObjectRegistry implements ObjectRegistry {

    private final Map<UUID, Object> mObjectMap;

    public DefaultObjectRegistry(){
        mObjectMap = new HashMap<>();
    }

    @NonNull
    @Override
    public <RegistryT> String put(@NonNull RegistryT object) {
        UUID key = UUID.randomUUID();
        mObjectMap.put(key, object);
        return key.toString();
    }

    @Nullable
    @Override
    public <RegistryT> RegistryT get(@NonNull String registryKey) {
        UUID key = UUID.fromString(registryKey);
        return (RegistryT) mObjectMap.remove(key);
    }
}