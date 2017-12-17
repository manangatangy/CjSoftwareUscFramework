package com.cjsoftware.ucs_library.uistatepreservation.rule;

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
public @interface PreservationRuleGroup {
  String groupName() default "default";
  String[] inheritFrom() default {};
  PreservationRule[] rules() default {};
}
