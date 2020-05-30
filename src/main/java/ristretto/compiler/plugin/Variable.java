package ristretto.compiler.plugin;

interface Variable {
    boolean hasFinalModifier();
    boolean hasPublicModifier();
    boolean hasProtectedModifier();
    boolean hasPrivateModifier();

    void addFinalModifier();
    void addPublicModifier();

    boolean isAnnotatedAsMutable();
    boolean hasPackagePrivateAnnotation();

    String position();

}
