package com.cjsoftware.processor.ucs.builder;

import com.cjsoftware.library.ucs.CachedMethodCall;
import com.cjsoftware.processor.ucs.model.ProcessorModel;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Type;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author chris
 * @date 27 Aug 2017
 */

public class ProxyQueueBuilder extends AbstractClassBuilder<TypeElement> {

    private static final String GENERATED_PROXY_QUEUE_EXECUTOR_CLASS_NAME_TEMPLATE = "%1s_ProxyQueueExecutor";
    private static final String GENERATED_CALL_CACHE_CLASS_NAME_TEMPLATE = "CachedCall$%1s";
    private static final String BOUND_INTERFACE_FIELD_NAME = "mBoundInterface";
    private static final String METHOD_CALL_QUEUE_FIELD_NAME = "mMethodCallQueue";
    private static final String METHOD_TYPE_MAP_FIELD_NAME = "mMethodTypeMap";
    private static final String EXECUTOR_FIELD_NAME = "mQueueExecutor";
    private static final String EXECUTOR_RUNNING_FLAG_FIELD_NAME = "mQueueRunning";
    private static final String METHOD_NAME_SUBMIT_CALL = "submitCall";
    private static final String METHOD_NAME_PROCESS_QUEUE = "processQueue";
    private static final String JAVA_LANG_PACKAGE_NAME = "java.lang";

    private final boolean strongReference;
    private final boolean keepLastCallOnly;
    private final String qualifiedPackageName;

    public ProxyQueueBuilder(ProcessingEnvironment processingEnvironment,
                             ProcessorModel model,
                             String packageName,
                             boolean strongReference,
                             boolean keepLastCallOnly) {
        super(processingEnvironment, model);
        this.qualifiedPackageName = packageName;
        this.keepLastCallOnly = keepLastCallOnly;
        this.strongReference = strongReference;
    }

    @Override
    public ClassName buildClass(TypeElement proxiedInterface) {

        ClassName generatedProxyQueueExecutorClassName = ClassName.get(
                this.qualifiedPackageName,
                String.format(GENERATED_PROXY_QUEUE_EXECUTOR_CLASS_NAME_TEMPLATE,
                        sanitiseClassName(proxiedInterface.getSimpleName().toString()))
        );

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedProxyQueueExecutorClassName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(TypeName.get(proxiedInterface.asType()))
                .addSuperinterface(Runnable.class);

        addStandardCode(classBuilder, proxiedInterface, strongReference, keepLastCallOnly);

        int methodId = 1;


        List<? extends Element> relevantElements = getRelevantMembers(proxiedInterface);

        for (Element interfaceElement : relevantElements) {

            if (interfaceElement.getKind() == ElementKind.METHOD) {

                ExecutableElement methodElement = (ExecutableElement) interfaceElement;


                if (!methodElement.getModifiers().contains(Modifier.FINAL)) {
                    if (TypeKind.VOID == methodElement.getReturnType().getKind()) {

                        TypeSpec cacheClass = buildCacheClass(proxiedInterface, methodElement, methodId);
                        classBuilder.addType(cacheClass);

                        addImplementation(classBuilder, proxiedInterface, (ExecutableElement) interfaceElement, cacheClass);

                        methodId++;

                    } else {
                        warningMessage("Methods with return types not allowed in ProxyQueueExecutor", interfaceElement);
                    }

                }

            }
        }

        TypeSpec builtClass = classBuilder.build();


        try {
            JavaFile javaFile = JavaFile.builder(this.qualifiedPackageName, builtClass)
                    .addFileComment("Generated Code, do not modify.")
                    .build();

            javaFile.writeTo(processingEnvironment.getFiler());


        } catch (IOException e) {
            fatalErrorMessage("Exception writing java file: " + e.toString(), proxiedInterface);
        }

        return generatedProxyQueueExecutorClassName;
    }

    private void addStandardCode(TypeSpec.Builder classBuilder, TypeElement proxiedInterface, boolean strongReference, boolean keepLastCallOnly) {

        classBuilder.addMethod(
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(Executor.class, "executor")
                        .addStatement("this.$N = executor", EXECUTOR_FIELD_NAME)
                        .build());


        if (strongReference) {
            classBuilder.addField(FieldSpec.builder(TypeName.get(proxiedInterface.asType()), BOUND_INTERFACE_FIELD_NAME)
                    .initializer("null")
                    .build());

            classBuilder.addMethod(
                    MethodSpec.methodBuilder("setProxiedInterface")
                            .addParameter(ClassName.get(proxiedInterface), "implementation")
                            .addStatement("this.$N = implementation", BOUND_INTERFACE_FIELD_NAME)
                            .addStatement("processQueue()")
                            .addModifiers(Modifier.PUBLIC)
                            .build());

            classBuilder.addMethod(
                    MethodSpec.methodBuilder("getProxiedInterface")
                            .returns(ClassName.get(proxiedInterface))
                            .addStatement("return $N", BOUND_INTERFACE_FIELD_NAME)
                            .addModifiers(Modifier.PUBLIC)
                            .build()
            );

        } else {
            classBuilder.addField(FieldSpec.builder(
                    ParameterizedTypeName.get(ClassName.get(WeakReference.class), TypeName.get(proxiedInterface.asType())),
                    BOUND_INTERFACE_FIELD_NAME)
                    .initializer("new WeakReference<>(null)")

                    .build());

            classBuilder.addMethod(
                    MethodSpec.methodBuilder("setProxiedInterface")
                            .addParameter(ClassName.get(proxiedInterface), "implementation")
                            .addStatement("this.$N = new WeakReference<>(implementation)", BOUND_INTERFACE_FIELD_NAME)
                            .addStatement("processQueue()")
                            .addModifiers(Modifier.PUBLIC)
                            .build());

            classBuilder.addMethod(
                    MethodSpec.methodBuilder("getProxiedInterface")
                            .returns(ClassName.get(proxiedInterface))
                            .addStatement("return $N.get()", BOUND_INTERFACE_FIELD_NAME)
                            .addModifiers(Modifier.PUBLIC)
                            .build()
            );
        }


        classBuilder.addField(FieldSpec.builder(
                ParameterizedTypeName.get(ClassName.get(Deque.class),
                        ParameterizedTypeName.get(ClassName.get(CachedMethodCall.class),
                                ClassName.get(proxiedInterface))),
                METHOD_CALL_QUEUE_FIELD_NAME, Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $N<>()", ClassName.get(LinkedList.class).toString())
                .build());

        classBuilder.addField(Executor.class, EXECUTOR_FIELD_NAME, Modifier.PRIVATE, Modifier.FINAL);
        classBuilder.addField(
                FieldSpec.builder(AtomicBoolean.class, EXECUTOR_RUNNING_FLAG_FIELD_NAME, Modifier.PRIVATE, Modifier.FINAL)
                        .initializer("new AtomicBoolean()")
                        .build());

        classBuilder.addMethod(MethodSpec.methodBuilder(METHOD_NAME_PROCESS_QUEUE)
                .beginControlFlow("if($N.compareAndSet(false,true))", EXECUTOR_RUNNING_FLAG_FIELD_NAME)
                .addStatement("$N.execute(this)", EXECUTOR_FIELD_NAME)
                .endControlFlow()
                .build()
        );


        MethodSpec.Builder submitCallMethod = MethodSpec.methodBuilder(METHOD_NAME_SUBMIT_CALL)
                .addModifiers(Modifier.PRIVATE)
                .addParameter(ParameterSpec.builder(CachedMethodCall.class, "cachedCall").build())
                .beginControlFlow("synchronized($N)", METHOD_CALL_QUEUE_FIELD_NAME);

        if (keepLastCallOnly) {

            classBuilder.addField(FieldSpec.builder(
                    ParameterizedTypeName.get(ClassName.get(Map.class),
                            ClassName.get(Integer.class),
                            ParameterizedTypeName.get(ClassName.get(CachedMethodCall.class), ClassName.get(proxiedInterface))
                    ),
                    METHOD_TYPE_MAP_FIELD_NAME, Modifier.FINAL, Modifier.PRIVATE)
                    .initializer("new $T<>()", ClassName.get(HashMap.class))
                    .build());

            submitCallMethod
                    .addStatement("$T previousCall = null", ParameterizedTypeName.get(ClassName.get(CachedMethodCall.class), ClassName.get(proxiedInterface)))
                    .beginControlFlow("synchronized($N)", METHOD_TYPE_MAP_FIELD_NAME)
                    .addStatement("previousCall = $N.remove(cachedCall.methodId())", METHOD_TYPE_MAP_FIELD_NAME)
                    .endControlFlow()
                    .beginControlFlow("if(previousCall != null)")
                    .beginControlFlow("synchronized($N)", METHOD_CALL_QUEUE_FIELD_NAME)
                    .addStatement("$N.remove(previousCall)", METHOD_CALL_QUEUE_FIELD_NAME)
                    .endControlFlow()
                    .endControlFlow();
        }

        submitCallMethod
                .addStatement("$N.add(cachedCall)", METHOD_CALL_QUEUE_FIELD_NAME)
                .endControlFlow()
                .addStatement("$N()", METHOD_NAME_PROCESS_QUEUE);

        classBuilder.addMethod(submitCallMethod.build());

        String boundInterfaceRetrieve = strongReference ? "" : ".get()";

        classBuilder.addMethod(
                MethodSpec.methodBuilder("run")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .beginControlFlow("while($N.size()>0)", METHOD_CALL_QUEUE_FIELD_NAME)
                        .addStatement("$T boundInterface = $N$L", proxiedInterface, BOUND_INTERFACE_FIELD_NAME, boundInterfaceRetrieve)
                        .beginControlFlow("if(boundInterface != null)")
                        .addStatement("$T methodCall = null",
                                ParameterizedTypeName.get(ClassName.get(CachedMethodCall.class), ClassName.get(proxiedInterface.asType())))
                        .beginControlFlow("synchronized($N)", METHOD_CALL_QUEUE_FIELD_NAME)
                        .addStatement("methodCall = $N.removeFirst()", METHOD_CALL_QUEUE_FIELD_NAME)
                        .endControlFlow()
                        .addStatement("methodCall.execute(boundInterface)")
                        .nextControlFlow("else")
                        .addStatement("break")
                        .endControlFlow()
                        .endControlFlow()
                        .addStatement("$N.set(false)", EXECUTOR_RUNNING_FLAG_FIELD_NAME)
                        .build()
        );
    }

    /**
     * Get all members of the supplied type *excluding* members from java.lang (inherited from
     * object)
     *
     * @return list of all members without members from java.Lang.Object
     */
    private List<? extends Element> getRelevantMembers(TypeElement interfaceElement) {

        Elements elementUtils = processingEnvironment.getElementUtils();
        List<? extends Element> allElements = elementUtils.getAllMembers(interfaceElement);

        List<Element> relevantElements = new ArrayList<>();

        for (Element element : allElements) {
            if (!elementUtils.getPackageOf(element).getQualifiedName().contentEquals(JAVA_LANG_PACKAGE_NAME)) {
                relevantElements.add(element);
            }
        }

        return relevantElements;
    }

    private TypeSpec buildCacheClass(TypeElement proxiedInterface, ExecutableElement interfaceMethod, int methodId) {

        Types typeUtils = processingEnvironment.getTypeUtils();
        DeclaredType declaredType = (DeclaredType) proxiedInterface.asType();

        ExecutableType executableType = (ExecutableType) typeUtils.asMemberOf(declaredType, interfaceMethod);
        List<? extends TypeMirror> resolvedParameterTypes = executableType.getParameterTypes();

        TypeSpec.Builder methodCallCacheClass = TypeSpec.classBuilder(
                String.format(GENERATED_CALL_CACHE_CLASS_NAME_TEMPLATE,
                        interfaceMethod.getSimpleName().toString()))
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(CachedMethodCall.class), ClassName.get(proxiedInterface)));

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();

        StringBuilder params = new StringBuilder();

        List<? extends VariableElement> parameters  = interfaceMethod.getParameters();

        for(int paramIndex = 0; paramIndex < parameters.size(); paramIndex++) {

            VariableElement variableElement = parameters.get(paramIndex);
            TypeName resolvedTypeName = TypeName.get(resolvedParameterTypes.get(paramIndex));
            String paramName = variableElement.getSimpleName().toString();

            if (params.length() > 0) {
                params.append(", ");
            }

            params.append("this.");
            params.append(variableElement.getSimpleName());

            methodCallCacheClass.addField(resolvedTypeName, paramName, Modifier.PRIVATE, Modifier.FINAL);

            constructorBuilder.addParameter(resolvedTypeName,paramName);
            constructorBuilder.addStatement("this.$N = $N", paramName, paramName);

        }



        methodCallCacheClass.addMethod(constructorBuilder.build());

        MethodSpec.Builder methodCallImplementation = MethodSpec.methodBuilder("execute")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(proxiedInterface), "implementation").build())
                .addCode(CodeBlock.builder()
                        .addStatement("implementation.$N($L)", interfaceMethod.getSimpleName(), params.toString())
                        .build());

        methodCallCacheClass.addMethod(methodCallImplementation.build());

        methodCallCacheClass.addMethod(MethodSpec.methodBuilder("methodId")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.INT)
                .addCode("return $L;", methodId)
                .addCode("\n")
                .build());

        return methodCallCacheClass.build();
    }

    private void addImplementation(TypeSpec.Builder classBuilder, TypeElement declaringElement, ExecutableElement interfaceMethod, TypeSpec cacheClass) {

        DeclaredType declaredType = (DeclaredType) declaringElement.asType();

        StringBuilder constructorArgs = new StringBuilder();

        for (VariableElement methodParam : interfaceMethod.getParameters()) {

            String paramName = methodParam.getSimpleName().toString();

            if (constructorArgs.length() > 0) {
                constructorArgs.append(", ");
            }

            constructorArgs.append(paramName);
        }

        MethodSpec.Builder interfaceProxyMethod = MethodSpec.overriding(interfaceMethod, declaredType, processingEnvironment.getTypeUtils())
                .addModifiers(Modifier.PUBLIC)
                .addCode("$1N cachedCall = new $1N($2L);", cacheClass, constructorArgs)
                .addCode("\n")
                .addCode("submitCall(cachedCall);")
                .addCode("\n");

        classBuilder.addMethod(interfaceProxyMethod.build());
    }

}
