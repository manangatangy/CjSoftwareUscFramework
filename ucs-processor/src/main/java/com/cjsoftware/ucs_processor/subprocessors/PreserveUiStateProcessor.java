package com.cjsoftware.ucs_processor.subprocessors;

import com.cjsoftware.ucs_processor.model.DoPreservationRule;
import com.cjsoftware.ucs_processor.model.DoPreservationRuleGroup;
import com.cjsoftware.ucs_processor.model.DoPreservedField;
import com.cjsoftware.ucs_processor.model.ProcessorModel;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.cjsoftware.ucs_library.uistatepreservation.Preserve;
import com.cjsoftware.ucs_library.uistatepreservation.StatePreservationManager;
import com.cjsoftware.ucs_library.uistatepreservation.strategy.StatePreservationStrategy;
import com.cjsoftware.ucs_library.uistatepreservation.strategy.ValuePreservationStrategy;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author chris
 * @date 28 Aug 2017
 */

public class PreserveUiStateProcessor extends AbstractUcsElementSetProcessor<VariableElement> {

  private static final String GENERATED_PRESERVED_FIELD_LIST_CLASS_NAME_TEMPLATE = "%1s_StatePreservationManager";
  private static final String FIELD_PRESERVE_CLASS_NAME_TEMPLATE = "Preserve_%1s";
  private static final String PARAMETER_FIELD_OWNER_NAME = "fieldOwner";
  private static final String PARAMETER_EXPLICIT_STRATEGY_LIST_NAME = "preservationStrategyList";
  private static final String METHOD_SAVE_FIELD_NAME_TEMPLATE = "%s$save";
  private static final String METHOD_RESTORE_FIELD_NAME_TEMPLATE = "%s$restore";
  private static final String PARAMETER_SOURCE_FIELD = "sourceField";
  private static final String PARAMETER_DESTIN_FIELD = "destinationField";
  private static final String FIELD_PRESERVE_STRATEGY_INSTANCE = "%1s$%2s";
  private static final String FIELD_HAVE_STATE = "haveState";

  private Map<TypeElement, List<DoPreservedField>> preservedFieldGroups = new HashMap<>();

  public PreserveUiStateProcessor(ProcessingEnvironment processingEnvironment, ProcessorModel model) {
    super(processingEnvironment, model);
  }

  @Override
  protected boolean isAcceptableElement(Element element) {
    return element.getKind().isField();
  }

  @Override
  public void processElement(VariableElement element) {
    // Group all of the preserved fields by the class they are in

    TypeElement topMostElement = getTopmostEnclosingElement(element);
    List<DoPreservedField> preservedFieldList = preservedFieldGroups.get(topMostElement);

    if (preservedFieldList == null) {
      preservedFieldList = new LinkedList<>();
      preservedFieldGroups.put(topMostElement, preservedFieldList);
    }

    if (element.getModifiers().contains(Modifier.PRIVATE)) {
      warningMessage("Cannot preserve a private field", element);
    } else {
      preservedFieldList.add(buildPreservedField(element.getAnnotation(Preserve.class), element));
    }

  }

  private DoPreservedField buildPreservedField(Preserve preserveAnnotation, VariableElement element) {
    DoPreservedField newPreservedField = null;

    String[] ruleGroups = preserveAnnotation.applyRuleGroups();

    Set<String> ruleGroupSet = new HashSet<>();

    for (String groupName : ruleGroups) {
      addRuleGroups(element, groupName, ruleGroupSet);
    }

    Elements elementUtils = processingEnvironment.getElementUtils();

    TypeElement containingType = (TypeElement) element.getEnclosingElement();

    newPreservedField = new DoPreservedField(elementUtils.getPackageOf(containingType), containingType, element);
    newPreservedField.getPreservationStrategyList().addAll(resolveStrategies(ruleGroupSet, element));

    List<? extends TypeMirror> strategies = null;

    try {
      preserveAnnotation.addStrategies();
    } catch (MirroredTypesException mte) {
      strategies = mte.getTypeMirrors();
    }

    if (strategies != null) {
      newPreservedField.getPreservationStrategyList().addAll(strategies);
    }

    try {
      preserveAnnotation.removeStrategies();
    } catch (MirroredTypesException mte) {
      strategies = mte.getTypeMirrors();
    }

    if (strategies != null) {
      newPreservedField.getPreservationStrategyList().removeAll(strategies);
    }

    return newPreservedField;
  }

  private Set<TypeMirror> resolveStrategies(Set<String> ruleSet, VariableElement element) {
    Set<TypeMirror> preservationStrategies = new HashSet<>();

    TypeMirror testableField = element.asType();
    Types typeUtils = processingEnvironment.getTypeUtils();

    if (element.asType().getKind().isPrimitive()) {
      // It's a primative. Need to box it to compare.. (eg, can use Byte.class in rule, but not byte)
      testableField = typeUtils.boxedClass((PrimitiveType) testableField).asType();
    }

    for (String ruleGroupName : ruleSet) {
      DoPreservationRuleGroup ruleGroup = processorModel.getRuleGroupMap().get(ruleGroupName);

      for (DoPreservationRule preservationRule : ruleGroup.getRuleGroupMemberList()) {
        if (isInstanceOfOneOf(testableField, preservationRule.getInstanceList()) ||
            isDescendantOfOneOf(testableField, preservationRule.getAncestorList())) {

          preservationStrategies.addAll(preservationRule.getStrategyList());
        }
      }

    }

    return preservationStrategies;
  }


  boolean isInstanceOfOneOf(TypeMirror fieldType, List<? extends TypeMirror> typeList) {
    boolean foundMatchingInstance = false;

    Types typeUtils = processingEnvironment.getTypeUtils();

    for (TypeMirror typeMirror : typeList) {
      if (typeUtils.isSameType(fieldType, typeMirror)) {
        foundMatchingInstance = true;
        break;
      }
    }

    return foundMatchingInstance;
  }

  boolean isDescendantOfOneOf(TypeMirror fieldType, List<? extends TypeMirror> typeList) {
    boolean foundMatchingInstance = false;

    Types typeUtils = processingEnvironment.getTypeUtils();

    for (TypeMirror typeMirror : typeList) {
      if (typeUtils.isSubtype(fieldType, typeMirror)) {
        foundMatchingInstance = true;
        break;
      }
    }

    return foundMatchingInstance;
  }

  private void addRuleGroups(VariableElement element, String ruleGroupName, Set<String> ruleGroupSet) {

    DoPreservationRuleGroup ruleGroup = processorModel.getRuleGroupMap().get(ruleGroupName);
    if (ruleGroup != null) {
      ruleGroupSet.add(ruleGroupName);
      for (String inheritedGroupName : ruleGroup.getInheritGroups()) {
        addRuleGroups(element, inheritedGroupName, ruleGroupSet);
      }
    } else {
      warningMessage(String.format("Preservation rule group \"%s\" not found.", ruleGroupName), element);
    }
  }


  @Override
  protected void completeProcessing() {
    super.completeProcessing();

    if (isFatalErrorEncountered()) {
      // If we hit a fatal error, bail.
      return;
    }

    for (TypeElement preservedFieldContainer : preservedFieldGroups.keySet()) {
      PackageElement packageElement = processingEnvironment.getElementUtils().getPackageOf(preservedFieldContainer);

      ClassName generatedFieldListClassName = ClassName.get(
          packageElement.getQualifiedName().toString(),
          String.format(GENERATED_PRESERVED_FIELD_LIST_CLASS_NAME_TEMPLATE,
              sanitiseClassName(preservedFieldContainer.getSimpleName().toString()))
      );

      TypeSpec.Builder preserveStateClass = TypeSpec.classBuilder(generatedFieldListClassName)
          .addSuperinterface(ParameterizedTypeName.get(
              ClassName.get(StatePreservationManager.class),
              ClassName.get(preservedFieldContainer.asType())))
          .addField(FieldSpec.builder(TypeName.BOOLEAN, FIELD_HAVE_STATE)
              .initializer("false")
              .build());

      List<DoPreservedField> preservedFieldList = preservedFieldGroups.get(preservedFieldContainer);

      addStrategyFields(preserveStateClass, preservedFieldList, preservedFieldContainer.asType());

      preserveStateClass.addMethod(addSaveMethod(TypeName.get(preservedFieldContainer.asType()), preservedFieldList));
      for (DoPreservedField field : preservedFieldList) {
        preserveStateClass.addMethod(addSaveFieldMethod(TypeName.get(preservedFieldContainer.asType()), field));
      }

      preserveStateClass.addMethod(addRestoreMethod(TypeName.get(preservedFieldContainer.asType()), preservedFieldList));
      for (DoPreservedField field : preservedFieldList) {
        preserveStateClass.addMethod(addRestoreFieldMethod(TypeName.get(preservedFieldContainer.asType()), field));
      }

      try {
        JavaFile javaFile = JavaFile.builder(packageElement.getQualifiedName().toString(), preserveStateClass.build())
            .addFileComment("Generated Code, do not modify.")
            .build();

        javaFile.writeTo(processingEnvironment.getFiler());


      } catch (IOException e) {
        fatalErrorMessage("Exception writing java file: " + e.toString(), preservedFieldContainer);
      }
    }

  }

  private void addStrategyFields(TypeSpec.Builder preservationClass, List<DoPreservedField> fieldList, TypeMirror preservedFieldContainingType) {
    Types typeUtils = processingEnvironment.getTypeUtils();
    Elements elementUtils = processingEnvironment.getElementUtils();
    TypeMirror valueStrategy = elementUtils.getTypeElement(ValuePreservationStrategy.class.getCanonicalName()).asType();

    for (DoPreservedField field : fieldList) {
      for (TypeMirror strategy : field.getPreservationStrategyList()) {

        String fieldName = String.format(FIELD_PRESERVE_STRATEGY_INSTANCE, field.getField().getSimpleName(), typeUtils.asElement(strategy).getSimpleName());
        FieldSpec.Builder strategyInstanceField;

        if (typeUtils.isAssignable(strategy, valueStrategy)) {
          // Value preservation strategy is parametric on preserved value type

          TypeMirror safeType = field.getField().asType();
          if (safeType.getKind().isPrimitive()) {
            safeType = typeUtils.boxedClass((PrimitiveType) safeType).asType();
          }

          strategyInstanceField = FieldSpec.builder(ParameterizedTypeName.get((ClassName) ClassName.get(strategy),
              TypeName.get(preservedFieldContainingType),
              TypeName.get(safeType)),
              fieldName, Modifier.PRIVATE)
              .initializer("new $T<>()", strategy);

        } else {
          strategyInstanceField =
              FieldSpec.builder(TypeName.get(strategy), fieldName, Modifier.PRIVATE)
                  .initializer("new $T()", strategy);
        }

        preservationClass.addField(strategyInstanceField.build());
      }
    }
  }

  private MethodSpec addSaveMethod(TypeName valueOwnerType, List<DoPreservedField> fieldList) {

    MethodSpec.Builder saveMethod = MethodSpec.methodBuilder("savePreservedFields")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ParameterSpec.builder(valueOwnerType, PARAMETER_FIELD_OWNER_NAME).build());

    for (DoPreservedField field : fieldList) {
      saveMethod.addStatement("$N($N)",
          String.format(METHOD_SAVE_FIELD_NAME_TEMPLATE, field.getField()), PARAMETER_FIELD_OWNER_NAME);
    }

    saveMethod.addStatement("$N = true", FIELD_HAVE_STATE);

    return saveMethod.build();
  }


  private MethodSpec addRestoreMethod(TypeName valueOwnerType, List<DoPreservedField> fieldList) {
    MethodSpec.Builder saveMethod = MethodSpec.methodBuilder("restorePreservedFields")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ParameterSpec.builder(valueOwnerType, PARAMETER_FIELD_OWNER_NAME).build());

    saveMethod.beginControlFlow("if($N)", FIELD_HAVE_STATE)
        .addStatement("$N = false", FIELD_HAVE_STATE);

    for (DoPreservedField field : fieldList) {
      saveMethod.addStatement("$N($N)",
          String.format(METHOD_RESTORE_FIELD_NAME_TEMPLATE, field.getField()),
          PARAMETER_FIELD_OWNER_NAME);
    }

    saveMethod.endControlFlow();

    return saveMethod.build();
  }

  private MethodSpec addSaveFieldMethod(TypeName valueOwnerType, DoPreservedField field) {
    MethodSpec.Builder saveFieldMethod = MethodSpec.methodBuilder(String.format(METHOD_SAVE_FIELD_NAME_TEMPLATE, field.getField().getSimpleName()))
        .addParameter(valueOwnerType, PARAMETER_FIELD_OWNER_NAME);

    Types typeUtils = processingEnvironment.getTypeUtils();
    Elements elementUtils = processingEnvironment.getElementUtils();
    TypeMirror valueStrategy = elementUtils.getTypeElement(ValuePreservationStrategy.class.getCanonicalName()).asType();

    for (TypeMirror strategy : field.getPreservationStrategyList()) {
      String fieldName = String.format(FIELD_PRESERVE_STRATEGY_INSTANCE, field.getField().getSimpleName(), typeUtils.asElement(strategy).getSimpleName());
      if (implementsInterface(typeUtils, valueStrategy, strategy)) {
        saveFieldMethod.addStatement("$N.saveValue($N, $N.$N)", fieldName, PARAMETER_FIELD_OWNER_NAME, PARAMETER_FIELD_OWNER_NAME, field.getField().getSimpleName());
      } else {
        saveFieldMethod.addStatement("$N.saveState($N, $N.$N)", fieldName, PARAMETER_FIELD_OWNER_NAME, PARAMETER_FIELD_OWNER_NAME, field.getField().getSimpleName());
      }
    }


    return saveFieldMethod.build();
  }

  private boolean implementsInterface(Types typeUtils, TypeMirror interfaceType, TypeMirror type) {
    if (typeUtils.isAssignable(type, interfaceType)) {
      return true;
    } else {
      TypeElement typeElement = (TypeElement) ((DeclaredType) type).asElement();
      for(TypeMirror implementedType : typeElement.getInterfaces()) {
        if (typeUtils.isAssignable(typeUtils.erasure(implementedType), typeUtils.erasure(interfaceType))) {
          return true;
        }
      }
    }

    return false;
  }

  private MethodSpec addRestoreFieldMethod(TypeName valueOwnerType, DoPreservedField field) {
    MethodSpec.Builder restoreFieldMethod = MethodSpec.methodBuilder(String.format(METHOD_RESTORE_FIELD_NAME_TEMPLATE, field.getField().getSimpleName()))
        .addParameter(valueOwnerType, PARAMETER_FIELD_OWNER_NAME);

    Types typeUtils = processingEnvironment.getTypeUtils();
    Elements elementUtils = processingEnvironment.getElementUtils();
    TypeMirror valueStrategy = elementUtils.getTypeElement(ValuePreservationStrategy.class.getCanonicalName()).asType();

    for (TypeMirror strategy : field.getPreservationStrategyList()) {
      String fieldName = String.format(FIELD_PRESERVE_STRATEGY_INSTANCE, field.getField().getSimpleName(), typeUtils.asElement(strategy).getSimpleName());
      if (implementsInterface(typeUtils, valueStrategy, strategy)) {
        restoreFieldMethod.addStatement("$N.$N = $N.retrieveValue($N)", PARAMETER_FIELD_OWNER_NAME, field.getField().getSimpleName(), fieldName, PARAMETER_FIELD_OWNER_NAME);
      } else {
        restoreFieldMethod.addStatement("$N.restoreState($N, $N.$N)", fieldName, PARAMETER_FIELD_OWNER_NAME, PARAMETER_FIELD_OWNER_NAME, field.getField().getSimpleName());
      }
    }

    return restoreFieldMethod.build();
  }


  private TypeElement getTopmostEnclosingElement(Element element) {
    TypeElement topMost = (TypeElement) element.getEnclosingElement();
    while (topMost.getEnclosingElement() != null && topMost.getEnclosingElement().getKind().isClass()) {
      topMost = (TypeElement) topMost.getEnclosingElement();
    }

    return topMost;
  }

  private void addPreservedFieldListBuilder(TypeSpec.Builder ownerClass, List<DoPreservedField> preservedFieldList) {

//    CodeBlock.Builder initializer = CodeBlock.builder()
//        .beginControlFlow("return new $T[]", PreservedField.class);
//
//
//    for (int preservedFieldIndex = 0; preservedFieldIndex < preservedFieldList.size(); preservedFieldIndex++) {
//      VariableElement preservedField = preservedFieldList.get(preservedFieldIndex);
//      if (preservedFieldIndex < preservedFieldList.size() - 1) {
//        initializer.add("new $L(),\n", String.format(FIELD_PRESERVE_CLASS_NAME_TEMPLATE, sanitiseClassName(preservedField.getSimpleName().toString())));
//      } else {
//        initializer.add("new $L()\n", String.format(FIELD_PRESERVE_CLASS_NAME_TEMPLATE, sanitiseClassName(preservedField.getSimpleName().toString())));
//      }
//    }
//
//    initializer.endControlFlow("");
//
//    MethodSpec.Builder listBuilder = MethodSpec.methodBuilder("getPreservedFieldList")
//        .returns(ArrayTypeName.of(PreservedField.class))
//        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//        .addCode(initializer.build());
//
//    ownerClass.addMethod(listBuilder.build());
  }

  private void addPreservedFieldClass(TypeSpec.Builder ownerClass,
                                      TypeElement fieldOwner,
                                      DoPreservedField preservedField) {


    CodeBlock.Builder configureRulesCode = CodeBlock.builder();


    List<? extends TypeMirror> typeMirrorList = null;


//    MethodSpec.Builder configure = MethodSpec.methodBuilder("configureRules")
//        .addModifiers(Modifier.PROTECTED)
//        .addParameter(PreservedField.RuleListBuilder.class, PARAMETER_RULE_LIST_BUILDER_NAME)
//        .addCode(configureRulesCode.build());

    CodeBlock.Builder addExplicitStrategiesCode = CodeBlock.builder();


    MethodSpec addExplicitStrategies = MethodSpec.methodBuilder("addExplicitStrategies")
        .addModifiers(Modifier.PROTECTED)
        .addParameter(ParameterizedTypeName.get(List.class, StatePreservationStrategy.class), PARAMETER_EXPLICIT_STRATEGY_LIST_NAME)
        .addCode(addExplicitStrategiesCode.build())
        .build();


    MethodSpec getPreservedField = MethodSpec.methodBuilder("getPreservedField")
        .addModifiers(Modifier.PROTECTED)
        .returns(TypeName.OBJECT)
        .addParameter(TypeName.OBJECT, PARAMETER_FIELD_OWNER_NAME)
        .addStatement("return (($T)$N).$N", fieldOwner.asType(), PARAMETER_FIELD_OWNER_NAME, preservedField.getField().getSimpleName())
        .build();


    MethodSpec getPreservedFieldType = MethodSpec.methodBuilder("getPreservedFieldType")
        .addModifiers(Modifier.PROTECTED)
        .returns(ParameterizedTypeName.get(ClassName.get(Class.class), ClassName.get(preservedField.getField().asType())))
        .addStatement("return $T.class", preservedField.getField().asType())
        .build();

    TypeSpec.Builder preserveClass = TypeSpec.classBuilder(
        String.format(FIELD_PRESERVE_CLASS_NAME_TEMPLATE, sanitiseClassName(preservedField.getField().getSimpleName().toString())))
//        .superclass(PreservedField.class)
        .addModifiers(Modifier.STATIC)
        .addMethod(getPreservedField)
        .addMethod(getPreservedFieldType)
//        .addMethod(configure.build())
        .addMethod(addExplicitStrategies);


    ownerClass.addType(preserveClass.build());
  }
}
