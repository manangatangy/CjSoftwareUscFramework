package com.cjsoftware.ucs_processor.subprocessors;

import com.cjsoftware.ucs_processor.model.ProcessorModel;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * @author chris
 * @date 27 Aug 2017
 */

public abstract class AbstractUcsElementProcessor<ElementT extends Element> {

  private boolean fatalError = false;
  protected ProcessorModel processorModel;
  protected ProcessingEnvironment processingEnvironment;


  public AbstractUcsElementProcessor(ProcessingEnvironment processingEnvironment, ProcessorModel processorModel) {
    this.processingEnvironment = processingEnvironment;
    this.processorModel = processorModel;
  }


  private String messageDescribeElement(String message, Element element) {
    Elements elementUtils = processingEnvironment.getElementUtils();
    return String.format("%s (%s in %s)",
        message,
        element.getSimpleName(),
        elementUtils.getPackageOf(element).getQualifiedName());
  }


  protected void warningMessage(String message, Element element) {
    processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING,
        messageDescribeElement(message, element),
        element);
  }

  protected void errorMessage(String message, Element element) {
    processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR,
        messageDescribeElement(message, element),
        element);
  }

  protected void fatalErrorMessage(String message, Element element) {
    fatalError = true;
    errorMessage(message, element);
  }

  public boolean isFatalErrorEncountered() {
    return fatalError;
  }

  protected String sanitiseClassName(String className) {
    return className.replace('.', '$');
  }

}

