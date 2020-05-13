package ristretto.compiler.plugin;

final class SimpleName extends StringTypeAlias implements ClassReference {

    SimpleName(String name) {
        super(name);
    }

    SimpleName(Class<?> aClass) {
        this(aClass.getSimpleName());
    }
}
