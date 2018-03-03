package com.cjsoftware.processor.ucs.subprocessors;

import com.cjsoftware.library.ucs.BaseUcsContract.BaseCoordinatorContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseScreenNavigationContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseStateManagerContract;
import com.cjsoftware.library.ucs.BaseUcsContract.BaseUiContract;
import com.cjsoftware.library.ucs.ContractBroker;
import com.cjsoftware.library.ucs.CoordinatorBinder;
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
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Executor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
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

    private static final String PROXY_BINDER_FIELD_NAME = "mProxyCoordinatorBinder";
    private static final String COORDINATOR_PARAM_NAME = "coordinator";
    private static final String COORDINATOR_FIELD_NAME = "mCoordinator";

    private static final String COORDINATOR_PROXY_QUEUE_FIELD_NAME = "mCoordinatorQueue";

    private static final String STATEMANAGER_PARAM_NAME = "stateManager";
    private static final String STATEMANAGER_FIELD_NAME = "mStateManager";

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


        TypeElement uiContract = findDescendentOf(contractSpec, BaseUiContract.class);
        ClassName uiProxyQueueClass = null;
        if (uiContract != null) {
            ProxyQueueBuilder uiProxyQueueBuilder = new ProxyQueueBuilder(processingEnvironment, processorModel, qualifiedPackageName, false, true);
            uiProxyQueueClass = uiProxyQueueBuilder.buildClass(uiContract);
        }

        TypeElement coordinatorContract = findDescendentOf(contractSpec, BaseCoordinatorContract.class);
        ClassName coordinatorProxyQueueClass = null;
        if (coordinatorContract != null) {
            ProxyQueueBuilder coordinatorProxyQueueBuilder = new ProxyQueueBuilder(processingEnvironment, processorModel, qualifiedPackageName, true, true);
            coordinatorProxyQueueClass = coordinatorProxyQueueBuilder.buildClass(coordinatorContract);
        }

        TypeElement screenNavigationContract = findDescendentOf(contractSpec, BaseScreenNavigationContract.class);
        ClassName screenNavigationProxyQueueClass = null;
        if (screenNavigationContract != null) {
            ProxyQueueBuilder screenNavigationProxyQueueBuilder = new ProxyQueueBuilder(processingEnvironment, processorModel, qualifiedPackageName, false, true);
            screenNavigationProxyQueueClass = screenNavigationProxyQueueBuilder.buildClass(screenNavigationContract);
        }

        TypeElement stateManagerContract = findDescendentOf(contractSpec, BaseStateManagerContract.class);


        ParameterizedTypeName contractBrokerSuperInterface = ParameterizedTypeName.get(ClassName.get(ContractBroker.class),
                ClassName.get(uiContract),
                ClassName.get(screenNavigationContract),
                ClassName.get(coordinatorContract),
                ClassName.get(stateManagerContract));

        TypeSpec.Builder contractBrokerClass = TypeSpec.classBuilder(generatedContractBrokerClassName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(contractBrokerSuperInterface);

        contractBrokerClass.addField(TypeName.get(CoordinatorBinder.class), PROXY_BINDER_FIELD_NAME, Modifier.PRIVATE);
        contractBrokerClass.addField(TypeName.get(coordinatorContract.asType()), COORDINATOR_FIELD_NAME, Modifier.PRIVATE);
        contractBrokerClass.addField(TypeName.get(stateManagerContract.asType()), STATEMANAGER_FIELD_NAME, Modifier.PRIVATE);

        contractBrokerClass.addField(coordinatorProxyQueueClass, COORDINATOR_PROXY_QUEUE_FIELD_NAME, Modifier.PRIVATE);
        contractBrokerClass.addField(uiProxyQueueClass, UI_PROXY_QUEUE_FIELD_NAME, Modifier.PRIVATE);

        contractBrokerClass.addField(screenNavigationProxyQueueClass, SCREEN_NAVIGATION_PROXY_QUEUE_FIELD_NAME, Modifier.PRIVATE);

        TypeSpec coordinatorBinderProxy = buildBindingProxyClass(uiContract, screenNavigationContract, stateManagerContract);
        contractBrokerClass.addType(coordinatorBinderProxy);

        contractBrokerClass.addMethod(
                MethodSpec.constructorBuilder()
                        .addAnnotation(ClassName.get("javax.inject", "Inject"))
                        .addModifiers(Modifier.PUBLIC)

                        .addParameter(ParameterSpec.builder(ClassName.get(Executor.class), "uiExecutor")
                                .addAnnotation(AnnotationSpec.builder(ClassName.get("com.cjsoftware.library.platform.android.core.facility", "MainLooper"))
                                        .build())
                                .build())

                        .addParameter(ClassName.get(Executor.class), "backgroundExecutor")
                        .addParameter(TypeName.get(coordinatorContract.asType()), COORDINATOR_PARAM_NAME)
                        .addParameter(TypeName.get(stateManagerContract.asType()), STATEMANAGER_PARAM_NAME)

                        // Save reference to coordinator (strong)
                        .addStatement("this.$N = $N", COORDINATOR_FIELD_NAME, COORDINATOR_PARAM_NAME)

                        // Save reference to state manager (strong)
                        .addStatement("this.$N = $N", STATEMANAGER_FIELD_NAME, STATEMANAGER_PARAM_NAME)

                        // Create Coordinator proxy (ui -> coordinator) - run on background thread
                        .addStatement("$N = new $T($N)", COORDINATOR_PROXY_QUEUE_FIELD_NAME, coordinatorProxyQueueClass, "backgroundExecutor")
                        .addStatement("$N.setProxiedInterface($N)", COORDINATOR_PROXY_QUEUE_FIELD_NAME, COORDINATOR_PARAM_NAME)

                        // Create Ui proxy (coordinator -> ui) - run on ui thread
                        .addStatement("this.$N = new $T($N)", UI_PROXY_QUEUE_FIELD_NAME, uiProxyQueueClass, "uiExecutor")

                        // Create Screen navigation proxy (coordinator -> screen navigation)  - run on ui thread
                        .addStatement("this.$N = new $T($N)", SCREEN_NAVIGATION_PROXY_QUEUE_FIELD_NAME, screenNavigationProxyQueueClass, "uiExecutor")

                        // Bind proxies to coordinator
                        .addStatement("(($T)this.$N).bindUi($N)", CoordinatorBinder.class, COORDINATOR_FIELD_NAME, UI_PROXY_QUEUE_FIELD_NAME)
                        .addStatement("(($T)this.$N).bindScreenNavigation(($T)$N)", CoordinatorBinder.class, COORDINATOR_FIELD_NAME, screenNavigationContract, SCREEN_NAVIGATION_PROXY_QUEUE_FIELD_NAME)

                        // Create CoordinatorBindingProxy
                        .addStatement("this.$N = new $N()", PROXY_BINDER_FIELD_NAME, coordinatorBinderProxy.name)

                        .build());

        contractBrokerClass.addMethod(
                MethodSpec.methodBuilder("getCoordinatorBinder")
                        .addAnnotation(Override.class)
                        .addAnnotation(processorModel.getNonNullAnnotationClassName())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.get(CoordinatorBinder.class))
                        .addStatement("return $N", PROXY_BINDER_FIELD_NAME)
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
                MethodSpec.methodBuilder("getUi")
                        .addAnnotation(Override.class)
                        .addAnnotation(processorModel.getNonNullAnnotationClassName())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.get(uiContract.asType()))
                        .addStatement("return this.$N", UI_PROXY_QUEUE_FIELD_NAME)
                        .build()
        );

        contractBrokerClass.addMethod(
                MethodSpec.methodBuilder("getScreenNavigation")
                        .addAnnotation(Override.class)
                        .addAnnotation(processorModel.getNonNullAnnotationClassName())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.get(screenNavigationContract.asType()))
                        .addStatement("return this.$N", SCREEN_NAVIGATION_PROXY_QUEUE_FIELD_NAME)
                        .build()
        );

        contractBrokerClass.addMethod(
                MethodSpec.methodBuilder("getStateManager")
                        .addAnnotation(Override.class)
                        .addAnnotation(processorModel.getNonNullAnnotationClassName())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.get(stateManagerContract.asType()))
                        .addStatement("return this.$N", STATEMANAGER_FIELD_NAME)
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

    private TypeSpec buildBindingProxyClass(TypeElement uiContract, TypeElement screenNavigationContract, TypeElement stateManagerContract) {


        TypeSpec.Builder bindingProxyClass = TypeSpec.classBuilder("ProxyCoordinatorBinder")
                .addSuperinterface(CoordinatorBinder.class);


        TypeVariableName uiImplementationT = TypeVariableName.get("UiImplementationT").withBounds(BaseUiContract.class);

        bindingProxyClass.addMethod(MethodSpec.methodBuilder("bindUi")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(uiImplementationT)
                .addParameter(uiImplementationT, "ui")
                .addStatement("$N.setProxiedInterface(($T)ui)", UI_PROXY_QUEUE_FIELD_NAME, uiContract)
                .build());

        TypeVariableName screenNavigationT = TypeVariableName.get("ScreenNavigationT").withBounds(BaseScreenNavigationContract.class);

        bindingProxyClass.addMethod(MethodSpec.methodBuilder("bindScreenNavigation")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(screenNavigationT)
                .addParameter(screenNavigationT, "screenNavigation")
                .addStatement("$N.setProxiedInterface(($T)screenNavigation)", SCREEN_NAVIGATION_PROXY_QUEUE_FIELD_NAME, screenNavigationContract)
                .build());

        TypeVariableName stateManagerT = TypeVariableName.get("StateManagerT").withBounds(BaseStateManagerContract.class);

        bindingProxyClass.addMethod(MethodSpec.methodBuilder("bindStateManager")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(stateManagerT)
                .addParameter(stateManagerT, "stateManager")
                .addStatement("(($T)$N).bindStateManager(($T) stateManager)", CoordinatorBinder.class, COORDINATOR_FIELD_NAME, stateManagerContract)
                .build());

        return bindingProxyClass.build();
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
        ClassName ancestorTypeName = ClassName.get(ancestor);

        for (TypeMirror typeMirror : interfaceList) {
            if (typeMirror.getKind() == TypeKind.DECLARED) {

                TypeName mirrorName = TypeName.get(typeMirror);
                if (mirrorName instanceof ParameterizedTypeName) {
                    mirrorName = ((ParameterizedTypeName) mirrorName).rawType;
                }

                if (ancestorTypeName.equals(mirrorName)) {
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
