package com.cjsoftware.processor.ucs.model;

import java.util.List;

import javax.lang.model.type.TypeMirror;

/**
 * @author chris
 * @date 08 Oct 2017
 */

public class DoPreservationRule {
  private final List<? extends TypeMirror> ancestorList;
  private final List<? extends TypeMirror> instanceList;
  private final List<? extends TypeMirror> strategyList;

  public DoPreservationRule(List<? extends TypeMirror> ancestorList, List<? extends TypeMirror> instanceList, List<? extends TypeMirror> strategyList) {
    this.ancestorList = ancestorList;
    this.instanceList = instanceList;
    this.strategyList = strategyList;
  }

  public List<? extends TypeMirror> getAncestorList() {
    return ancestorList;
  }

  public List<? extends TypeMirror> getInstanceList() {
    return instanceList;
  }

  public List<? extends TypeMirror> getStrategyList() {
    return strategyList;
  }
}
