package ristretto.compiler.plugin;

import ristretto.Mutable;
import ristretto.Nullable;

import java.util.Objects;
import java.util.Optional;

final class QualifiedClassName {

    static final QualifiedClassName MUTABLE_ANNOTATION = of(Mutable.class);
    static final QualifiedClassName NULLABLE_ANNOTATION = of(Nullable.class);

    private static final char SEPARATOR = '.';

    private final Optional<String> packageName;
    private final String simpleName;

    private QualifiedClassName(Optional<String> packageName, String simpleName) {
        this.packageName = packageName;
        this.simpleName = simpleName;
    }

    static QualifiedClassName of(Class<?> aClass) {
        Optional<String> packageName;
        if (aClass.getPackageName().isEmpty()) {
            packageName = Optional.empty();
        } else {
            packageName = Optional.of(aClass.getPackageName());
        }
        return new QualifiedClassName(packageName, aClass.getSimpleName());
    }

    static QualifiedClassName parse(String qualifiedName) {
        int separatorIndex = qualifiedName.lastIndexOf(SEPARATOR);

        if (separatorIndex == -1) {
            return new QualifiedClassName(Optional.empty(), qualifiedName);
        }

        String packageName = qualifiedName.substring(0, separatorIndex);
        String simpleName = qualifiedName.substring(separatorIndex + 1);

        return new QualifiedClassName(Optional.of(packageName), simpleName);
    }

    String simpleName() {
        return simpleName;
    }

    Optional<String> packageName() {
        return packageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QualifiedClassName that = (QualifiedClassName) o;
        return packageName.equals(that.packageName) &&
            simpleName.equals(that.simpleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, simpleName);
    }

    @Override
    public String toString() {
        return packageName.map(name -> name + SEPARATOR + simpleName).orElse(simpleName);
    }
}
