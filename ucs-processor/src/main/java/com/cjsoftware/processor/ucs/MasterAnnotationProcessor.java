package com.cjsoftware.processor.ucs;

import com.cjsoftware.library.ucs.UcsContract;
import com.cjsoftware.library.uistatepreservation.Preserve;
import com.cjsoftware.library.uistatepreservation.rule.PreservationRule;
import com.cjsoftware.library.uistatepreservation.rule.PreservationRuleGroup;
import com.cjsoftware.processor.ucs.model.ProcessorModel;
import com.cjsoftware.processor.ucs.subprocessors.AbstractUcsElementSetProcessor;
import com.cjsoftware.processor.ucs.subprocessors.PreservationRuleGroupProcessor;
import com.cjsoftware.processor.ucs.subprocessors.PreservationRuleProcessor;
import com.cjsoftware.processor.ucs.subprocessors.PreserveUiStateProcessor;
import com.cjsoftware.processor.ucs.subprocessors.UcsContractProcessor;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * @author chris
 * @date 23 Jul 2017
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)

// @AutoService(UcsAnnotationProcessor.class)
public class MasterAnnotationProcessor extends AbstractProcessor {

  private ProcessingEnvironment processingEnvironment;
  private ProcessorModel model;

  @Override
  public synchronized void init(ProcessingEnvironment env) {
    super.init(env);
    processingEnvironment = env;
    model = new ProcessorModel();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> types = new LinkedHashSet<>();

    types.add(UcsContract.class.getCanonicalName());
    types.add(Preserve.class.getCanonicalName());
    types.add(PreservationRuleGroup.class.getCanonicalName());
    types.add(PreservationRule.class.getCanonicalName());

    return types;
  }


  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

    boolean fatalError = false;

    // Build preservation strategy rules first (preservation is dependent on them)

    if (!fatalError) {
      fatalError = passElementSetToProcessor(roundEnv,
          new PreservationRuleGroupProcessor(processingEnvironment, model),
          PreservationRuleGroup.class);
    }

    if (!fatalError) {
      fatalError = passElementSetToProcessor(roundEnv,
          new PreservationRuleProcessor(processingEnvironment, model),
          PreservationRule.class);
    }

    if (!fatalError) {
      fatalError = passElementSetToProcessor(roundEnv,
          new PreserveUiStateProcessor(processingEnvironment, model),
          Preserve.class);
    }

    if (!fatalError) {
      fatalError = passElementSetToProcessor(roundEnv,
          new UcsContractProcessor(processingEnvironment, model),
          UcsContract.class);
    }

    return false;
  }

  private boolean passElementSetToProcessor(RoundEnvironment roundEnv, AbstractUcsElementSetProcessor processor, Class<? extends Annotation> annotation) {
    Set<? extends Element> elementSet = roundEnv.getElementsAnnotatedWith(annotation);

    if (elementSet.size() > 0) {

      processor.processElements(elementSet);
      return processor.isFatalErrorEncountered();

    } else {
      return false;
    }
  }

}
