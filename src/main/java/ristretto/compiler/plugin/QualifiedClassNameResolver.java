package ristretto.compiler.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;

final class QualifiedClassNameResolver {

    private final Map<String, List<QualifiedClassName>> classesOfInterest;
    private final Map<String, QualifiedClassName> importedClasses = new HashMap<>();

    private QualifiedClassNameResolver(QualifiedClassName... classesOfInterest) {
        this.classesOfInterest = Stream.of(classesOfInterest).collect(groupingBy(this::packageName));
    }

    private String packageName(QualifiedClassName className) {
        return className.packageName()
            .orElseThrow(() -> new IllegalArgumentException(String.format("illegal class of interest: '%s'", className)));
    }

    static QualifiedClassNameResolver newResolver(QualifiedClassName... classesOfInterest) {
        return new QualifiedClassNameResolver(classesOfInterest);
    }

    void importClass(QualifiedImport importDeclaration) {
        var className = importDeclaration.className();
        if (className.isPresent()) {
            importClass(className.get());
            return;
        }

        classesOfInterest.getOrDefault(importDeclaration.packageName(), emptyList()).forEach(this::importClass);
    }

    private void importClass(QualifiedClassName qualifiedClassName) {
        importedClasses.put(qualifiedClassName.simpleName(), qualifiedClassName);
    }

    QualifiedClassName resolve(QualifiedName qualifiedName) {
        if (qualifiedName.packageName().isPresent()) {
            return QualifiedClassName.of(qualifiedName);
        }

        var className = importedClasses.get(qualifiedName.simpleName());
        if (className == null) {
            return QualifiedClassName.of(qualifiedName);
        }
        return className;
    }

}
