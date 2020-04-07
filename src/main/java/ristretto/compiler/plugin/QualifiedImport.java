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

    static QualifiedImport parse(String importDeclaration) {
        int separatorIndex = importDeclaration.lastIndexOf('.');

        if (separatorIndex == -1) {
            throw new IllegalArgumentException(String.format("illegal import declaration: '%s'", importDeclaration));
        }

        String packageName = importDeclaration.substring(0, separatorIndex);
        String simpleName = importDeclaration.substring(separatorIndex + 1);

        if (WILDCARD.equals(simpleName)) {
            return new QualifiedImport(packageName, Optional.empty());
        }

        return new QualifiedImport(packageName, Optional.of(simpleName));
    }

    Optional<QualifiedClassName> className() {
        return simpleName.map(name -> QualifiedClassName.of(packageName, name));
    }

    String packageName() {
        return packageName;
    }
}
