package ristretto.compiler.plugin;

interface LocalVariable {
    boolean isAnnotatedAsMutable();
    boolean hasFinalModifier();
    void addFinalModifier();
    String position();
}
