package com.cjsoftware.ucs_library.uistatepreservation.rule;

import com.cjsoftware.ucs_library.uistatepreservation.strategy.PreservationStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chris
 * @date 08 Oct 2017
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface PreservationRule {
  String ruleGroup() default "default";

  Class<?>[] isDescendantOf() default {};

  Class<?>[] isInstanceOf() default {};

  Class<? extends PreservationStrategy>[] applyStrategies() default {};
}
