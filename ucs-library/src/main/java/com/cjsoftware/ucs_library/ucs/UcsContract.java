package com.cjsoftware.ucs_library.ucs;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author chris
 * @date 07 Sep 2017
 */
@Target(TYPE)
@Retention(SOURCE)
public @interface UcsContract {
}
