package org.example.processor;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.*;
import org.example.annotations.WithInterface;
import org.example.annotations.InterfaceMethod;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
public class InterfaceGenerator extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elementUtils;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(
                "org.example.annotations.Method",
                "org.example.annotations.WithInterface");
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
        for (Element typeElem: roundEnvironment.getElementsAnnotatedWith(WithInterface.class)) {
            if (typeElem.getKind() != ElementKind.CLASS) {
                showError(typeElem, "Annotation @WithInterface can only be used with classes!");
                return true;
            }
            WithInterface annotation = typeElem.getAnnotation(WithInterface.class);
            String newInterfaceName = annotation.interfaceName().trim().replaceAll("\\s+","");
            if (newInterfaceName.isEmpty() || !newInterfaceName.matches("[\\w]+")) {
                showError(typeElem, "Interface name must not be empty and must contain only characters 0-9, a-z, A-Z, _!");
                return true;
            }
            List<MethodSpec> methodSpecs = new ArrayList<>();
            for (Element element: roundEnvironment.getElementsAnnotatedWith(InterfaceMethod.class)) {
                ExecutableElement method = (ExecutableElement) element;
                if (isNotPublic(method)) {
                    showError(element, "Annotation @InterfaceMethod can only be used with public methods!");
                    return true;
                }
                methodSpecs.add(generateMethod(method));
            }
            TypeSpec newInterface = TypeSpec
                    .interfaceBuilder(newInterfaceName)
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

    private boolean isNotPublic(ExecutableElement method) {
        return !method.getModifiers().contains(Modifier.PUBLIC);
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
        return MethodSpec
                .methodBuilder(method.getSimpleName().toString())
                .addParameters(parameters)
                .returns(ClassName.get(method.getReturnType()))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .build();
    }


    private void showError(Element e, String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg, e);
    }




}
