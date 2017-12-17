package com.cjsoftware.processor.ucs.subprocessors;

import com.cjsoftware.processor.ucs.model.ProcessorModel;

import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

/**
 * @author chris
 * @date 08 Oct 2017
 */

public abstract class AbstractUcsElementSetProcessor<ElementT extends Element>
    extends AbstractUcsElementProcessor<ElementT> {

  public AbstractUcsElementSetProcessor(ProcessingEnvironment processingEnvironment, ProcessorModel processorModel) {
    super(processingEnvironment, processorModel);
  }

  public void processElements(Set<? extends Element> elementSet) {

    for (Element element : elementSet) {
      if (isFatalErrorEncountered()) {
        break;
      } else {
        if (isAcceptableElement(element)) {
          processElement((ElementT) element);
        } else {
          errorMessage("Unexpected annotation target", element);
        }
      }
    }

    completeProcessing();

  }

  protected abstract boolean isAcceptableElement(Element element);

  public abstract void processElement(ElementT element);

  protected void completeProcessing() {
  }
}
