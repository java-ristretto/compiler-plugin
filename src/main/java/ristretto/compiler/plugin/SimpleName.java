package ristretto.compiler.plugin;

final class SimpleName extends StringTypeAlias implements ClassReference {

    private SimpleName(String name) {
        super(name);
    }

    static SimpleName of(Class<?> aClass) {
        return of(aClass.getSimpleName());
    }

    static SimpleName of(String name) {
        return new SimpleName(name);
    }
}
