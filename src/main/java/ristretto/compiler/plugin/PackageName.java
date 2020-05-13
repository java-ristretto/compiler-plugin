package ristretto.compiler.plugin;

final class PackageName extends StringTypeAlias {

    PackageName(String name) {
        super(name);
    }

    PackageName(Class<?> aClass) {
        this(aClass.getPackageName());
    }

    QualifiedName qualify(SimpleName simpleName) {
        return new QualifiedName(this, simpleName);
    }
}
