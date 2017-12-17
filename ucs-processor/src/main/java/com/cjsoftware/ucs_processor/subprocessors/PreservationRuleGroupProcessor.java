package com.cjsoftware.ucs_processor.subprocessors;

import com.cjsoftware.ucs_processor.model.ProcessorModel;
import com.cjsoftware.ucs_library.uistatepreservation.rule.PreservationRule;
import com.cjsoftware.ucs_library.uistatepreservation.rule.PreservationRuleGroup;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

/**
 * @author chris
 * @date 08 Oct 2017
 */

public class PreservationRuleGroupProcessor extends AbstractUcsElementSetProcessor {
  private PreservationRuleModelBuilder modelBuilder;

  public PreservationRuleGroupProcessor(ProcessingEnvironment processingEnvironment, ProcessorModel processorModel) {
    super(processingEnvironment, processorModel);
    modelBuilder = new PreservationRuleModelBuilder(processingEnvironment, processorModel);
  }

  @Override
  protected boolean isAcceptableElement(Element element) {
    return true;
  }

  @Override
  public void processElement(Element element) {

    PreservationRuleGroup preservationRuleGroup = element.getAnnotation(PreservationRuleGroup.class);

    PreservationRule[] ruleList = preservationRuleGroup.rules();
    for(PreservationRule rule : ruleList) {
      modelBuilder.addRuleGroup(preservationRuleGroup.groupName(), preservationRuleGroup.inheritFrom());
      modelBuilder.addRuleToModel(preservationRuleGroup.groupName(), rule);
      if (modelBuilder.isFatalErrorEncountered()) {
        break;
      }
    }

  }
}
