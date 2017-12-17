package com.cjsoftware.ucs_processor.model;


import java.util.HashMap;
import java.util.Map;

/**
 * @author chris
 * @date 16 Sep 2017
 */

public class ProcessorModel {
  private final Map<String, DoPreservationRuleGroup> ruleGroupMap;

  public ProcessorModel() {
    ruleGroupMap = new HashMap<>();
  }

  public Map<String, DoPreservationRuleGroup> getRuleGroupMap() {
    return ruleGroupMap;
  }
}
