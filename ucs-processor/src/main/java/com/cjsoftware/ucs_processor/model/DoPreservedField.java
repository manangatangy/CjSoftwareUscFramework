package com.cjsoftware.ucs_processor.model;

import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * @author chris
 * @date 08 Oct 2017
 */

public class DoPreservedField {
  private final PackageElement containingPackage;
  private final TypeElement containingType;
  private final VariableElement field;

  private final List<TypeMirror> preservationStrategyList;

  public DoPreservedField(PackageElement containingPackage, TypeElement containingType, VariableElement field) {
    this.containingPackage = containingPackage;
    this.containingType = containingType;
    this.field = field;
    preservationStrategyList = new ArrayList<>();
  }

  public PackageElement getContainingPackage() {
    return containingPackage;
  }

  public TypeElement getContainingType() {
    return containingType;
  }

  public VariableElement getField() {
    return field;
  }

  public List<TypeMirror> getPreservationStrategyList() {
    return preservationStrategyList;
  }
}
