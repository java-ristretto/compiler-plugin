package ristretto.compiler.plugin;

interface Variable {
    boolean hasFinalModifier();
    boolean hasPublicModifier();
    boolean hasProtectedModifier();
    boolean hasPrivateModifier();

    void addFinalModifier();
    void addPublicModifier();

    boolean hasMutableAnnotation();
    boolean hasPackagePrivateAnnotation();

    String position();
}
