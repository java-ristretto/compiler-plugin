package ristretto.compiler.plugin;

import java.util.Objects;
import java.util.Optional;

final class QualifiedName {

    private static final char SEPARATOR = '.';

    private final Optional<String> packageName;
    private final String simpleName;

    private QualifiedName(Optional<String> packageName, String simpleName) {
        this.packageName = packageName;
        this.simpleName = simpleName;
    }

    static QualifiedName parse(String qualifiedName) {
        int separatorIndex = qualifiedName.lastIndexOf(SEPARATOR);

        if (separatorIndex == -1) {
            return new QualifiedName(Optional.empty(), qualifiedName);
        }

        String packageName = qualifiedName.substring(0, separatorIndex);
        String simpleName = qualifiedName.substring(separatorIndex + 1);

        return new QualifiedName(Optional.of(packageName), simpleName);
    }

    static QualifiedName of(Class<?> aClass) {
        Optional<String> packageName;
        if (aClass.getPackageName().isEmpty()) {
            packageName = Optional.empty();
        } else {
            packageName = Optional.of(aClass.getPackageName());
        }
        return new QualifiedName(packageName, aClass.getSimpleName());
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
        QualifiedName that = (QualifiedName) o;
        return packageName.equals(that.packageName) &&
            simpleName.equals(that.simpleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, simpleName);
    }

    @Override
    public String toString() {
        return packageName.map(name -> name + "." + simpleName).orElse(simpleName);
    }

}
