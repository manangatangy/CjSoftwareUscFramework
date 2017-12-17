package com.cjsoftware.library.ucs;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author chris
 * @date 07 Sep 2017
 */
@Target(TYPE)
@Retention(SOURCE)
public @interface UcsContract {
}
