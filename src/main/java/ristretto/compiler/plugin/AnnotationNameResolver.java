package ristretto.compiler.plugin;

import ristretto.Mutable;
import ristretto.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;

final class AnnotationNameResolver {

    private static final QualifiedName MUTABLE = QualifiedName.of(Mutable.class);
    private static final QualifiedName NULLABLE = QualifiedName.of(Nullable.class);

    private static final Map<PackageName, List<QualifiedName>> KNOWN_ANNOTATIONS =
        Stream.of(MUTABLE, NULLABLE).collect(groupingBy(QualifiedName::packageName));

    private final Map<ClassReference, ClassReference> importedClasses = new HashMap<>();

    private AnnotationNameResolver() {
    }

    static AnnotationNameResolver newResolver() {
        return new AnnotationNameResolver();
    }

    void importClass(String importDeclaration) {
        importClass(ImportDeclaration.parse(importDeclaration));
    }

    private void importClass(ImportDeclaration importDeclaration) {
        Optional<QualifiedName> qualifiedName = importDeclaration.qualifiedName();
        if (qualifiedName.isPresent()) {
            importClass(qualifiedName.get());
            return;
        }

        KNOWN_ANNOTATIONS.getOrDefault(importDeclaration.packageName(), emptyList()).forEach(this::importClass);
    }

    private void importClass(QualifiedName qualifiedName) {
        importedClasses.put(qualifiedName.simpleName(), qualifiedName);
    }

    boolean isMutable(String annotationName) {
        return MUTABLE.equals(resolve(annotationName));
    }

    boolean isNullable(String annotationName) {
        return NULLABLE.equals(resolve(annotationName));
    }

    private ClassReference resolve(String classReference) {
        return resolve(ClassReference.parse(classReference));
    }

    private ClassReference resolve(ClassReference classReference) {
        return importedClasses.getOrDefault(classReference, classReference);
    }
}
