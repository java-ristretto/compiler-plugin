package ristretto.compiler.plugin;

import ristretto.Mutable;
import ristretto.PackagePrivate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;

final class AnnotationNameResolver {

    private static final QualifiedName MUTABLE = QualifiedName.of(Mutable.class);
    private static final QualifiedName PACKAGE_PRIVATE = QualifiedName.of(PackagePrivate.class);

    private static final Map<PackageName, List<QualifiedName>> KNOWN_ANNOTATIONS =
        Stream.of(MUTABLE, PACKAGE_PRIVATE).collect(groupingBy(QualifiedName::packageName));

    private final Map<ClassReference, ClassReference> importedClasses = new HashMap<>();

    AnnotationNameResolver(Set<ImportDeclaration> importDeclarations) {
        importDeclarations.forEach(this::importClass);
    }

    // TODO: remove
    AnnotationNameResolver() {
    }

    // TODO: remove
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

    boolean isPackagePrivate(String annotationName) {
        return PACKAGE_PRIVATE.equals(resolve(annotationName));
    }

    private ClassReference resolve(String classReference) {
        return resolve(ClassReference.parse(classReference));
    }

    private ClassReference resolve(ClassReference classReference) {
        return importedClasses.getOrDefault(classReference, classReference);
    }
}
