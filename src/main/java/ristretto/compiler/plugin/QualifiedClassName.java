package ristretto.compiler.plugin;

import ristretto.Mutable;
import ristretto.Nullable;

import java.util.Objects;
import java.util.Optional;

final class QualifiedClassName {

    static final QualifiedClassName MUTABLE_ANNOTATION = of(Mutable.class);
    static final QualifiedClassName NULLABLE_ANNOTATION = of(Nullable.class);

    private final QualifiedName qualifiedName;

    private QualifiedClassName(QualifiedName qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    static QualifiedClassName of(Class<?> aClass) {
        return new QualifiedClassName(QualifiedName.of(aClass));
    }

    static QualifiedClassName parse(String qualifiedName) {
        return new QualifiedClassName(QualifiedName.parse(qualifiedName));
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
