package ristretto.compiler.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;

final class QualifiedClassNameResolver {

    private final Map<String, List<Class<?>>> classesOfInterest;
    private final Map<String, QualifiedClassName> importedClasses = new HashMap<>();

    private QualifiedClassNameResolver(Class<?>... classesOfInterest) {
        this.classesOfInterest = Stream.of(classesOfInterest)
            .collect(groupingBy(Class::getPackageName));
    }

    static QualifiedClassNameResolver newResolver(Class<?>... classesOfInterest) {
        return new QualifiedClassNameResolver(classesOfInterest);
    }

    void importClass(QualifiedImport importDeclaration) {
        var className = importDeclaration.className();
        if (className.isPresent()) {
            importClass(className.get());
            return;
        }

        classesOfInterest.getOrDefault(importDeclaration.packageName(), emptyList())
            .stream()
            .map(QualifiedClassName::of)
            .forEach(this::importClass);
    }

    private void importClass(QualifiedClassName qualifiedClassName) {
        importedClasses.put(qualifiedClassName.simpleName(), qualifiedClassName);
    }

    QualifiedClassName resolve(QualifiedClassName qualifiedClassName) {
        if (qualifiedClassName.packageName().isPresent()) {
            return qualifiedClassName;
        }

        return importedClasses.getOrDefault(qualifiedClassName.simpleName(), qualifiedClassName);
    }
}
