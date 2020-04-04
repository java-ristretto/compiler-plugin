package ristretto.compiler.plugin;

import java.util.Optional;

final class QualifiedImport {

    private static final String WILDCARD = "*";

    private final String packageName;
    private final QualifiedName qualifiedName;

    private QualifiedImport(String packageName, QualifiedName qualifiedName) {
        this.packageName = packageName;
        this.qualifiedName = qualifiedName;
    }

    static QualifiedImport of(QualifiedName qualifiedName) {
        var packageName = qualifiedName.packageName()
            .orElseThrow(() -> new IllegalArgumentException(String.format("illegal import declaration: '%s'", qualifiedName)));
        return new QualifiedImport(packageName, qualifiedName);
    }

    Optional<QualifiedClassName> className() {
        if (WILDCARD.equals(qualifiedName.simpleName())) {
            return Optional.empty();
        }
        return Optional.of(QualifiedClassName.of(qualifiedName));
    }

    String packageName() {
        return packageName;
    }

    @Override
    public String toString() {
        return qualifiedName.toString();
    }
}
