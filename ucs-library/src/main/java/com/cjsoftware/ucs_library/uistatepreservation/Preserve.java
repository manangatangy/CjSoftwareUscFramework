package com.cjsoftware.ucs_library.uistatepreservation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import com.cjsoftware.ucs_library.uistatepreservation.strategy.PreservationStrategy;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author chris
 * @date 12 Aug 2017
 */
@Target(FIELD)
@Retention(SOURCE)
public @interface Preserve {
  String[] applyRuleGroups() default {"default"};
  Class<? extends PreservationStrategy>[] removeStrategies() default {};
  Class<? extends PreservationStrategy>[] addStrategies() default {};
}
