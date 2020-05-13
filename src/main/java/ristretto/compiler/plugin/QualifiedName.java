package ristretto.compiler.plugin;

import java.util.Objects;

final class QualifiedName implements ClassReference {

    private final PackageName packageName;
    private final SimpleName simpleName;

    QualifiedName(PackageName packageName, SimpleName simpleName) {
        this.packageName = packageName;
        this.simpleName = simpleName;
    }

    static QualifiedName of(Class<?> aClass) {
        return new QualifiedName(new PackageName(aClass), new SimpleName(aClass));
    }

    PackageName packageName() {
        return packageName;
    }

    SimpleName simpleName() {
        return simpleName;
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
}
