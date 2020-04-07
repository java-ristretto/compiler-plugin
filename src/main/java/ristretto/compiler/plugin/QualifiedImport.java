package ristretto.compiler.plugin;

import java.util.Optional;

final class QualifiedImport {

    private static final String WILDCARD = "*";

    private final String packageName;
    private final Optional<String> simpleName;

    private QualifiedImport(String packageName, Optional<String> simpleName) {
        this.packageName = packageName;
        this.simpleName = simpleName;
    }

    static QualifiedImport parse(String qualifiedImport) {
        var qualifiedName = QualifiedClassName.parse(qualifiedImport);

        var packageName = qualifiedName.packageName()
            .orElseThrow(() -> new IllegalArgumentException(String.format("illegal import declaration: '%s'", qualifiedName)));

        Optional<String> simpleName;
        if (WILDCARD.equals(qualifiedName.simpleName())) {
            simpleName = Optional.empty();
        } else {
            simpleName = Optional.of(qualifiedName.simpleName());
        }

        return new QualifiedImport(packageName, simpleName);
    }

    Optional<QualifiedClassName> className() {
        return simpleName.map(name -> QualifiedClassName.parse(packageName + "." + name));
    }

    String packageName() {
        return packageName;
    }
}
