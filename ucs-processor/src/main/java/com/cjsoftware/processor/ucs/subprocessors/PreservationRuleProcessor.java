package com.cjsoftware.processor.ucs.subprocessors;

import com.cjsoftware.library.uistatepreservation.rule.PreservationRule;
import com.cjsoftware.processor.ucs.model.ProcessorModel;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

/**
 * @author chris
 * @date 08 Oct 2017
 */

public class PreservationRuleProcessor extends AbstractUcsElementSetProcessor {

  private PreservationRuleModelBuilder modelBuilder;

  public PreservationRuleProcessor(ProcessingEnvironment processingEnvironment, ProcessorModel processorModel) {
    super(processingEnvironment, processorModel);
    modelBuilder = new PreservationRuleModelBuilder(processingEnvironment, processorModel);
  }

  @Override
  protected boolean isAcceptableElement(Element element) {
    return true;
  }

  @Override
  public void processElement(Element element) {
    PreservationRule preservationRule = element.getAnnotation(PreservationRule.class);
    modelBuilder.addRuleToModel(preservationRule.ruleGroup(), preservationRule);
  }
}
