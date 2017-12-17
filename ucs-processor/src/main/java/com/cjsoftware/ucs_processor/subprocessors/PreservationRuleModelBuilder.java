package com.cjsoftware.ucs_processor.subprocessors;

import com.cjsoftware.ucs_processor.model.DoPreservationRule;
import com.cjsoftware.ucs_processor.model.DoPreservationRuleGroup;
import com.cjsoftware.ucs_processor.model.ProcessorModel;
import com.cjsoftware.ucs_library.uistatepreservation.rule.PreservationRule;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

/**
 * @author chris
 * @date 08 Oct 2017
 */

public class PreservationRuleModelBuilder extends AbstractUcsElementProcessor {

  public PreservationRuleModelBuilder(ProcessingEnvironment processingEnvironment, ProcessorModel processorModel) {
    super(processingEnvironment, processorModel);
  }

  public DoPreservationRuleGroup getRuleGroup(String ruleGroupName) {
    Map<String, DoPreservationRuleGroup> ruleGroupMap = processorModel.getRuleGroupMap();
    DoPreservationRuleGroup ruleGroup = ruleGroupMap.get(ruleGroupName);
    return ruleGroup;
  }

  public DoPreservationRuleGroup addRuleGroup(String ruleGroupName, String[] inheritGroups) {
    Map<String, DoPreservationRuleGroup> ruleGroupMap = processorModel.getRuleGroupMap();

    Set<String> inheritedGroups = new HashSet<>();
    for(String groupName : inheritedGroups) {
      if(groupName.compareToIgnoreCase(ruleGroupName)!=0) {
        inheritedGroups.add(groupName.toLowerCase());
      }
    }

    DoPreservationRuleGroup ruleGroup = ruleGroupMap.get(ruleGroupName.toLowerCase());
    if (ruleGroup == null) {
      ruleGroup = new DoPreservationRuleGroup(ruleGroupName.toLowerCase(), inheritedGroups);
      ruleGroupMap.put(ruleGroupName, ruleGroup);
    }

    return ruleGroup;
  }

  public void addRuleToModel(String ruleGroupName, PreservationRule rule) {
    DoPreservationRuleGroup ruleGroup = getRuleGroup(ruleGroupName);

    List<? extends TypeMirror> strategyList = null;
    try {
      rule.applyStrategies();
    } catch (MirroredTypesException mte) {
      strategyList = mte.getTypeMirrors();
    }

    List<? extends TypeMirror> ancestorClassList = null;
    try {
      rule.isDescendantOf();
    }catch (MirroredTypesException mte) {
      ancestorClassList = mte.getTypeMirrors();
    }

    List<? extends TypeMirror> instanceClassList = null;
    try {
      rule.isInstanceOf();
    } catch (MirroredTypesException mte) {
      instanceClassList = mte.getTypeMirrors();
    }

    DoPreservationRule newRule = new DoPreservationRule(ancestorClassList, instanceClassList, strategyList);
    ruleGroup.getRuleGroupMemberList().add(newRule);

  }


}
