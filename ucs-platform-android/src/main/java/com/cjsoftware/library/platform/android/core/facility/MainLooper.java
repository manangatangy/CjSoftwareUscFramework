package com.cjsoftware.library.platform.android.core.facility;

import javax.inject.Qualifier;

/**
 * @author chrisjames
 * @date 01 Jan 2018.
 * Convenience qualifier to mark the MainLooperExecutor
 * eg: in your application module:
 * @Provides
 * @Singleton
 * @MainLooper
 * Executor provideMainLooperExecutor(MainLooperExecutor mainLooperExecutor) {
 *     return mainLooperExecutor
 * }
 * And in your injection: (in this case an injected constructor)
 * @inject
 * public SomeClassConstructor(@MainLooper Executor mainLooperExecutor) {
 *     ...
 * }
 *
 */
@Qualifier
public @interface MainLooper {
}
