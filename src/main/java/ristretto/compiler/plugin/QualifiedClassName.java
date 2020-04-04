package ristretto.compiler.plugin;

import java.util.Objects;
import java.util.Optional;

final class QualifiedClassName {

    private final QualifiedName qualifiedName;

    private QualifiedClassName(QualifiedName qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    static QualifiedClassName of(QualifiedName qualifiedName) {
        return new QualifiedClassName(qualifiedName);
    }

    String simpleName() {
        return qualifiedName.simpleName();
    }

    Optional<String> packageName() {
        return qualifiedName.packageName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QualifiedClassName className = (QualifiedClassName) o;
        return qualifiedName.equals(className.qualifiedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName);
    }

    @Override
    public String toString() {
        return qualifiedName.toString();
    }
}
