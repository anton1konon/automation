package org.example.processor;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.*;
import org.example.annotations.InterfaceMethod;
import org.example.annotations.ProxyMethod;
import org.example.annotations.WithInterface;
import org.example.annotations.WithProxy;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
public class ProxyGenerator extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elementUtils;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(
                "org.example.annotations.WithProxy",
                "org.example.annotations.ProxyMethod");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element typeElem: roundEnvironment.getElementsAnnotatedWith(WithProxy.class)) {
            if (typeElem.getKind() != ElementKind.CLASS) {
                showError(typeElem, "Annotation @WithProxy can only be used with classes!");
                return true;
            }
            WithProxy annotation = typeElem.getAnnotation(WithProxy.class);
            String newInterfaceName = annotation.proxyName().trim().replaceAll("\\s+","");
            if (newInterfaceName.isEmpty() || !newInterfaceName.matches("[\\w]+")) {
                showError(typeElem, "Class name must not be empty and must contain only characters 0-9, a-z, A-Z, _!");
                return true;
            }
            List<MethodSpec> methodSpecs = new ArrayList<>();
            for (Element element: roundEnvironment.getElementsAnnotatedWith(ProxyMethod.class)) {
                ExecutableElement method = (ExecutableElement) element;
                if (isNotPublic(method)) {
                    showError(element, "Annotation @ProxyMethod can only be used with public methods!");
                    return true;
                }
                methodSpecs.add(generateMethod(method));
            }

            FieldSpec proxiedObject = FieldSpec
                    .builder(ClassName.get(typeElem.asType()), "proxiedObject")
                    .addModifiers(Modifier.PRIVATE)
                    .build();

            MethodSpec constructor = MethodSpec
                    .constructorBuilder()
                    .addParameter(ParameterSpec.builder(ClassName.get(typeElem.asType()), "object").build())
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("this.proxiedObject = object")
                    .build();

            TypeSpec newInterface = TypeSpec
                    .classBuilder(newInterfaceName)
                    .addField(proxiedObject)
                    .addMethod(constructor)
                    .addMethods(methodSpecs)
                    .addModifiers(Modifier.PUBLIC)
                    .build();
            PackageElement pkg = elementUtils.getPackageOf(typeElem);
            String packg;
            if (!pkg.isUnnamed()) {
                packg = pkg.getQualifiedName().toString();
            } else {
                packg = "";
            }
            JavaFile javaFile = JavaFile
                    .builder(packg, newInterface)
                    .indent("    ")
                    .build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return true;
    }


    private MethodSpec generateMethod(ExecutableElement method) {
        List<ParameterSpec> parameters = new ArrayList<>();
        for (VariableElement parameter : method.getParameters()) {
            parameters.add(
                    ParameterSpec
                    .builder(ClassName.get(parameter.asType()),parameter.getSimpleName().toString())
                    .build()
            );
        }

        String logStmt = "System.out.println(\"Method "+method.getSimpleName()+" executed\")";
        String stmt = "proxiedObject."+method.getSimpleName()+"("+parameters.stream().map(p -> p.name).collect(Collectors.joining(", "))+")";

        if (!ClassName.get(method.getReturnType()).toString().equals("void")) {
            stmt = "return "+stmt;
        }

        return MethodSpec
                .methodBuilder(method.getSimpleName().toString())
                .addParameters(parameters)
                .addStatement(logStmt)
                .addStatement(stmt)
                .returns(ClassName.get(method.getReturnType()))
                .addModifiers(method.getModifiers())
                .build();
    }

    private boolean isNotPublic(ExecutableElement method) {
        return !method.getModifiers().contains(Modifier.PUBLIC);
    }

    private void showError(Element e, String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg, e);
    }




}
