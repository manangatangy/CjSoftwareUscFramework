package com.cjsoftware.ucs_processor.builder;

import com.cjsoftware.ucs_processor.subprocessors.AbstractUcsElementProcessor;
import com.cjsoftware.ucs_processor.model.ProcessorModel;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

/**
 * @author chris
 * @date 10 Sep 2017
 */

public abstract class AbstractClassBuilder<ElementT extends Element> extends AbstractUcsElementProcessor<ElementT> {



  public AbstractClassBuilder(ProcessingEnvironment processingEnvironment, ProcessorModel model) {
    super(processingEnvironment, model);
  }

  public abstract ClassName buildClass(ElementT element);

}
