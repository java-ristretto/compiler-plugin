package ristretto.compiler.plugin;

final class PackageName extends StringTypeAlias {

    private PackageName(String name) {
        super(name);
    }

    QualifiedName qualify(SimpleName simpleName) {
        return new QualifiedName(this, simpleName);
    }

    static PackageName of(Class<?> aClass) {
        return of(aClass.getPackageName());
    }

    static PackageName of(String name) {
        return new PackageName(name);
    }
}
