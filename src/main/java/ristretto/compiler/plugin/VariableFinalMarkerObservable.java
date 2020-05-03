package ristretto.compiler.plugin;

interface VariableFinalMarkerObservable {
    void markedAsFinal(VariableScope scope);
    void skipped(VariableScope scope);
}
