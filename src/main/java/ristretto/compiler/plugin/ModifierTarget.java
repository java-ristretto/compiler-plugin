package ristretto.compiler.plugin;

interface ModifierTarget {

    boolean hasFinalModifier();
    boolean hasPublicModifier();
    boolean hasProtectedModifier();
    boolean hasPrivateModifier();
    boolean hasStaticModifier();

    void addFinalModifier();
    void addPrivateModifier();

    boolean hasMutableAnnotation();
    boolean hasPackagePrivateAnnotation();

    String position();
}
