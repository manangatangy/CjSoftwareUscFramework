package com.cjsoftware.processor.ucs.subprocessors;

import com.cjsoftware.library.ucs.AbstractUcsContract;
import com.cjsoftware.library.ucs.accessor.CoordinatorAccessor;
import com.cjsoftware.library.ucs.accessor.StateManagerAccessor;
import com.cjsoftware.library.ucs.binder.ScreenNavigationBinder;
import com.cjsoftware.library.ucs.binder.UiBinder;
import com.cjsoftware.processor.ucs.builder.ProxyQueueBuilder;
import com.cjsoftware.processor.ucs.model.ProcessorModel;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * @author chris
 * @date 07 Sep 2017
 */

public class UcsContractProcessor extends AbstractUcsElementSetProcessor<TypeElement> {

    private static final String GENERATED_CONTRACT_BROKER_CLASS_NAME_TEMPLATE = "%1s_ContractBroker";
    private static final String COORDINATOR_PARAM_NAME = "coordinator";
    private static final String COORDINATOR_FIELD_NAME = "mCoordinator";
    private static final String COORDINATOR_PROXY_QUEUE_FIELD_NAME = "mCoordinatorQueue";
    private static final String USER_NAVIGATION_REQUEST_PROXY_QUEUE_FIELD_NAME = "mUserNavigationRequestQueue";
    private static final String UI_PROXY_QUEUE_FIELD_NAME = "mUiProxyQueue";
    private static final String SCREEN_NAVIGATION_PROXY_QUEUE_FIELD_NAME = "mScreenNavigationQueue";

    public UcsContractProcessor(ProcessingEnvironment processingEnvironment, ProcessorModel model) {
        super(processingEnvironment, model);
    }

    @Override
    protected boolean isAcceptableElement(Element element) {
        return ElementKind.INTERFACE.equals(element.getKind());
    }

    @Override
    public void processElement(TypeElement contractSpec) {
        PackageElement packageElement = processingEnvironment.getElementUtils().getPackageOf(contractSpec);

        ClassName generatedContractBrokerClassName = ClassName.get(
                packageElement.getQualifiedName().toString(),
                String.format(GENERATED_CONTRACT_BROKER_CLASS_NAME_TEMPLATE,
                        sanitiseClassName(contractSpec.getSimpleName().toString()))
        );

        String qualifiedPackageName = packageElement.getQualifiedName().toString();


        TypeElement uiContract = findDescendentOf(contractSpec, AbstractUcsContract.AbstractUi.class);
        ProxyQueueBuilder uiProxyQueueBuilder = new ProxyQueueBuilder(processingEnvironment, processorModel, qualifiedPackageName, false, true);
        ClassName uiProxyQueueClass = uiProxyQueueBuilder.buildClass(uiContract);

        TypeElement coordinatorContract = findDescendentOf(contractSpec, AbstractUcsContract.AbstractCoordinator.class);
        ProxyQueueBuilder coordinatorProxyQueueBuilder = new ProxyQueueBuilder(processingEnvironment, processorModel, qualifiedPackageName, true, true);
        ClassName coordinatorProxyQueueClass = coordinatorProxyQueueBuilder.buildClass(coordinatorContract);

        TypeElement screenNavigationContract = findDescendentOf(contractSpec, AbstractUcsContract.AbstractScreenNavigation.class);
        ProxyQueueBuilder screenNavigationProxyQueueBuilder = new ProxyQueueBuilder(processingEnvironment, processorModel, qualifiedPackageName, false, true);
        ClassName screenNavigationProxyQueueClass = screenNavigationProxyQueueBuilder.buildClass(screenNavigationContract);


        TypeElement stateManagerContract = findDescendentOf(contractSpec, AbstractUcsContract.AbstractStateManager.class);

        TypeSpec.Builder contractBrokerClass = TypeSpec.classBuilder(generatedContractBrokerClassName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(UiBinder.class), ClassName.get(uiContract)))
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(ScreenNavigationBinder.class), ClassName.get(screenNavigationContract)))
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(CoordinatorAccessor.class), ClassName.get(coordinatorContract)))
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(StateManagerAccessor.class), ClassName.get(stateManagerContract)));

        contractBrokerClass.addField(TypeName.get(coordinatorContract.asType()), COORDINATOR_FIELD_NAME, Modifier.PRIVATE);
        contractBrokerClass.addField(coordinatorProxyQueueClass, COORDINATOR_PROXY_QUEUE_FIELD_NAME, Modifier.PRIVATE);
        contractBrokerClass.addField(uiProxyQueueClass, UI_PROXY_QUEUE_FIELD_NAME, Modifier.PRIVATE);
        contractBrokerClass.addField(screenNavigationProxyQueueClass, SCREEN_NAVIGATION_PROXY_QUEUE_FIELD_NAME, Modifier.PRIVATE);

        contractBrokerClass.addMethod(
                MethodSpec.constructorBuilder()
                        .addAnnotation(ClassName.get("javax.inject", "Inject"))
                        .addModifiers(Modifier.PUBLIC)

                        .addParameter(ParameterSpec.builder(ClassName.get(Executor.class), "uiExecutor")
                                .addAnnotation(AnnotationSpec.builder(ClassName.get("javax.inject", "Named"))
                                        .addMember("value", "$S", "uiExecutor")
                                        .build())
                                .build())
                        .addParameter(ClassName.get(Executor.class), "backgroundExecutor")
                        .addParameter(TypeName.get(coordinatorContract.asType()), COORDINATOR_PARAM_NAME)

                        .addStatement("this.$N = $N", COORDINATOR_FIELD_NAME, COORDINATOR_PARAM_NAME)

                        .addStatement("this.$N = new $T($N)", UI_PROXY_QUEUE_FIELD_NAME, uiProxyQueueClass, "uiExecutor")

                        .addStatement("this.$N = new $T($N)", SCREEN_NAVIGATION_PROXY_QUEUE_FIELD_NAME, screenNavigationProxyQueueClass, "uiExecutor")

                        .addStatement("$N = new $T($N)", COORDINATOR_PROXY_QUEUE_FIELD_NAME, coordinatorProxyQueueClass, "backgroundExecutor")
                        .addStatement("$N.setProxiedInterface($N)", COORDINATOR_PROXY_QUEUE_FIELD_NAME, COORDINATOR_PARAM_NAME)

                        .addStatement(
                                "(($T<$T>) this.$N).bindToImplementation($N)",
                                UiBinder.class, uiContract, COORDINATOR_FIELD_NAME, UI_PROXY_QUEUE_FIELD_NAME)

                        .addStatement(
                                "(($T<$T>) this.$N).bindToImplementation($N)",
                                ScreenNavigationBinder.class, screenNavigationContract,
                                COORDINATOR_FIELD_NAME, SCREEN_NAVIGATION_PROXY_QUEUE_FIELD_NAME)

                        .build());


        contractBrokerClass.addMethod(
                MethodSpec.methodBuilder("bindToImplementation")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
                        .addParameter(ParameterSpec.builder(TypeName.get(uiContract.asType()), "ui").build())
                        .addStatement("this.$N.setProxiedInterface($N)", UI_PROXY_QUEUE_FIELD_NAME, "ui")
                        .build()
        );

        contractBrokerClass.addMethod(
                MethodSpec.methodBuilder("bindToImplementation")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
                        .addParameter(ParameterSpec.builder(TypeName.get(screenNavigationContract.asType()), "navigation").build())
                        .addStatement("this.$N.setProxiedInterface($N)", SCREEN_NAVIGATION_PROXY_QUEUE_FIELD_NAME, "navigation")
                        .build()
        );

        contractBrokerClass.addMethod(
                MethodSpec.methodBuilder("getCoordinator")
                        .addAnnotation(Override.class)
                        .addAnnotation(processorModel.getNonNullAnnotationClassName())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.get(coordinatorContract.asType()))
                        .addStatement("return $N", COORDINATOR_PROXY_QUEUE_FIELD_NAME)
                        .build()
        );

        contractBrokerClass.addMethod(
                MethodSpec.methodBuilder("getStateManager")
                        .addAnnotation(Override.class)
                        .addAnnotation(processorModel.getNonNullAnnotationClassName())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.get(stateManagerContract.asType()))
                        .addStatement("return (($T)this.$N).getStateManager()",
                                ParameterizedTypeName.get(ClassName.get(StateManagerAccessor.class), ClassName.get(stateManagerContract.asType())),
                                COORDINATOR_FIELD_NAME)
                        .build()
        );


        try {
            JavaFile javaFile = JavaFile.builder(packageElement.getQualifiedName().toString(), contractBrokerClass.build())
                    .addFileComment("Generated Code, do not modify.")
                    .build();

            javaFile.writeTo(processingEnvironment.getFiler());


        } catch (IOException e) {
            fatalErrorMessage("Exception writing java file: " + e.toString(), contractSpec);
        }

    }

    private TypeElement findDescendentOf(TypeElement enclosingElement, Class<?> ancestorClass) {

        TypeElement foundDescendent = findDescendentInEnclosed(enclosingElement, ancestorClass);

        if (foundDescendent == null) {
            // Didn't find in this interface, start searching for it up the heirarchy

            List<? extends TypeMirror> interfaceList = enclosingElement.getInterfaces();

            for (TypeMirror typeMirror : interfaceList) {
                if (typeMirror.getKind() == TypeKind.DECLARED) {
                    foundDescendent = findDescendentOf((TypeElement) ((DeclaredType) typeMirror).asElement(), ancestorClass);
                    if (foundDescendent != null) {
                        break;
                    }
                }
            }
        }

        return foundDescendent;
    }

    private TypeElement findDescendentInEnclosed(TypeElement encloser, Class<?> ancestorClass) {
        TypeElement foundDescendent = null;

        List<? extends Element> elementList = encloser.getEnclosedElements();
        for (Element element : elementList) {
            if (element.getKind().isInterface()) {
                if (TypeName.get(element.asType()).equals(TypeName.get(ancestorClass)) || ancestryContains(ancestorClass, (TypeElement) element)) {
                    foundDescendent = (TypeElement) element;
                    break;
                }
            }
        }

        return foundDescendent;
    }


    private boolean ancestryContains(Class<?> ancestor, TypeElement element) {

        List<? extends TypeMirror> interfaceList = element.getInterfaces();

        for (TypeMirror typeMirror : interfaceList) {
            if (typeMirror.getKind() == TypeKind.DECLARED) {
                if (TypeName.get(ancestor).equals(TypeName.get(typeMirror))) {
                    return true;
                } else {
                    DeclaredType declaredType = (DeclaredType) typeMirror;
                    return ancestryContains(ancestor, (TypeElement) declaredType.asElement());
                }
            }
        }
        return false;
    }


}
