package com.cjsoftware.processor.ucs.model;


import com.squareup.javapoet.ClassName;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chris
 * @date 16 Sep 2017
 */

public class ProcessorModel {
  private final Map<String, DoPreservationRuleGroup> ruleGroupMap;

  private final String nullabilityAnnotationPackage;
  private final String nonNullAnnotationName;

  public ProcessorModel(String nullabilityAnnotationPackage, String nonNullAnnotationName) {
    ruleGroupMap = new HashMap<>();
    this.nullabilityAnnotationPackage = nullabilityAnnotationPackage;
    this.nonNullAnnotationName = nonNullAnnotationName;
  }

  public Map<String, DoPreservationRuleGroup> getRuleGroupMap() {
    return ruleGroupMap;
  }

  public ClassName getNullableAnnotationClassName() {
    return ClassName.get(nullabilityAnnotationPackage, "Nullable");
  }

  public ClassName getNonNullAnnotationClassName() {
    return ClassName.get(nullabilityAnnotationPackage, nonNullAnnotationName);
  }
}
