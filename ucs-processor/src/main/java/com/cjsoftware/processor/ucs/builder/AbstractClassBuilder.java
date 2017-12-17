package com.cjsoftware.processor.ucs.builder;

import com.cjsoftware.processor.ucs.model.ProcessorModel;
import com.cjsoftware.processor.ucs.subprocessors.AbstractUcsElementProcessor;
import com.squareup.javapoet.ClassName;

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
