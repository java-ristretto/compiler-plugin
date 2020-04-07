package ristretto.compiler.plugin;

import ristretto.Mutable;
import ristretto.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;

final class AnnotationNameResolver {

    private static final QualifiedClassName MUTABLE = QualifiedClassName.parse(Mutable.class.getName());
    private static final QualifiedClassName NULLABLE = QualifiedClassName.parse(Nullable.class.getName());
    private static final Map<String, List<QualifiedClassName>> KNOWN_ANNOTATIONS =
        Stream.of(MUTABLE, NULLABLE)
            .collect(groupingBy(qualifiedClassName -> qualifiedClassName.packageName().orElseThrow()));

    private final Map<String, QualifiedClassName> importedClasses = new HashMap<>();

    private AnnotationNameResolver() {
    }

    static AnnotationNameResolver newResolver() {
        return new AnnotationNameResolver();
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

        KNOWN_ANNOTATIONS.getOrDefault(importDeclaration.packageName(), emptyList()).forEach(this::importClass);
    }

    private void importClass(QualifiedClassName qualifiedClassName) {
        importedClasses.put(qualifiedClassName.simpleName(), qualifiedClassName);
    }

    boolean isMutable(String annotationName) {
        return MUTABLE.equals(resolve(annotationName));
    }

    boolean isNullable(String annotationName) {
        return NULLABLE.equals(resolve(annotationName));
    }

    private QualifiedClassName resolve(String qualifiedClassName) {
        return resolve(QualifiedClassName.parse(qualifiedClassName));
    }

    private QualifiedClassName resolve(QualifiedClassName qualifiedClassName) {
        if (qualifiedClassName.packageName().isPresent()) {
            return qualifiedClassName;
        }

        return importedClasses.getOrDefault(qualifiedClassName.simpleName(), qualifiedClassName);
    }
}
