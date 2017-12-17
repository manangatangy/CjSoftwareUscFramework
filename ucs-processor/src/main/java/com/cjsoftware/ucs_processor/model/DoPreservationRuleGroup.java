package com.cjsoftware.ucs_processor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author chris
 * @date 08 Oct 2017
 */

public class DoPreservationRuleGroup {
  private final String name;
  private final Set<String> inheritGroups;
  private final List<DoPreservationRule> ruleGroupMemberList;

  public DoPreservationRuleGroup(String name, Set<String> inheritGroups) {
    this.name = name;
    this.inheritGroups = inheritGroups;
    ruleGroupMemberList = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public Set<String> getInheritGroups() {
    return inheritGroups;
  }

  public List<DoPreservationRule> getRuleGroupMemberList() {
    return ruleGroupMemberList;
  }
}
