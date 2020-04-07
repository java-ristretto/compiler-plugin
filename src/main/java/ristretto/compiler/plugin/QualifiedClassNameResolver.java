package ristretto.compiler.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static ristretto.compiler.plugin.QualifiedClassName.MUTABLE_ANNOTATION;
import static ristretto.compiler.plugin.QualifiedClassName.NULLABLE_ANNOTATION;

final class QualifiedClassNameResolver {

    private static final Map<String, List<QualifiedClassName>> CLASSES_OF_INTEREST =
        Stream.of(MUTABLE_ANNOTATION, NULLABLE_ANNOTATION)
            .collect(groupingBy(qualifiedClassName -> qualifiedClassName.packageName().orElseThrow()));

    private final Map<String, QualifiedClassName> importedClasses = new HashMap<>();

    private QualifiedClassNameResolver() {
    }

    static QualifiedClassNameResolver newResolver() {
        return new QualifiedClassNameResolver();
    }

    void importClass(String importDeclaration) {
        importClass(QualifiedImport.parse(importDeclaration));
    }

    private void importClass(QualifiedImport importDeclaration) {
        var className = importDeclaration.className();
        if (className.isPresent()) {
            importClass(className.get());
            return;
        }

        CLASSES_OF_INTEREST.getOrDefault(importDeclaration.packageName(), emptyList()).forEach(this::importClass);
    }

    private void importClass(QualifiedClassName qualifiedClassName) {
        importedClasses.put(qualifiedClassName.simpleName(), qualifiedClassName);
    }

    boolean isMutable(String annotationName) {
        return MUTABLE_ANNOTATION.equals(resolve(QualifiedClassName.parse(annotationName)));
    }

    boolean isNullable(String annotationName) {
        return NULLABLE_ANNOTATION.equals(resolve(QualifiedClassName.parse(annotationName)));
    }

    private QualifiedClassName resolve(QualifiedClassName qualifiedClassName) {
        if (qualifiedClassName.packageName().isPresent()) {
            return qualifiedClassName;
        }

        return importedClasses.getOrDefault(qualifiedClassName.simpleName(), qualifiedClassName);
    }
}
