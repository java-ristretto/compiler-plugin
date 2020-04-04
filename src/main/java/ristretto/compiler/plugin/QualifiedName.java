package ristretto.compiler.plugin;

import java.util.Objects;
import java.util.Optional;

final class QualifiedName {

    private static final char SEPARATOR = '.';

    private final Optional<String> packageName;
    private final String simpleName;
    private final String qualifiedName;

    private QualifiedName(Optional<String> packageName, String simpleName, String qualifiedName) {
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.qualifiedName = qualifiedName;
    }

    static QualifiedName parse(String qualifiedName) {
        int separatorIndex = qualifiedName.lastIndexOf(SEPARATOR);

        if (separatorIndex == -1) {
            return new QualifiedName(Optional.empty(), qualifiedName, qualifiedName);
        }

        String packageName = qualifiedName.substring(0, separatorIndex);
        String simpleName = qualifiedName.substring(separatorIndex + 1);

        return new QualifiedName(Optional.of(packageName), simpleName, qualifiedName);
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
        return qualifiedName.equals(that.qualifiedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName);
    }

    @Override
    public String toString() {
        return qualifiedName;
    }

}
