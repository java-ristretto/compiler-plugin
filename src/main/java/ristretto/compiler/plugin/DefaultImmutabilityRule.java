package ristretto.compiler.plugin;

import com.sun.source.tree.VariableTree;

final class DefaultImmutabilityRule implements VariableScanner.Visitor {

    private final AnnotationNameResolver resolver;
    private final Listener listener;

    DefaultImmutabilityRule(AnnotationNameResolver resolver, Listener listener) {
        this.resolver = resolver;
        this.listener = listener;
    }

    @Override
    public void visitLocalVariable(VariableTree localVariable) {
        handleVariable(localVariable, EventSource.LOCAL);
    }

    @Override
    public void visitParameter(VariableTree parameter) {
        handleVariable(parameter, EventSource.PARAMETER);
    }

    @Override
    public void visitClassField(VariableTree field) {
        handleVariable(field, EventSource.FIELD);
    }

    @Override
    public void visitEnumField(VariableTree field) {
        if (JCTreeCatalog.hasStaticModifier(field)) {
            return;
        }

        handleVariable(field, EventSource.FIELD);
    }

    private void handleVariable(VariableTree variable, EventSource local) {
        if (JCTreeCatalog.isAnnotatedAsMutable(variable, resolver)) {
            listener.annotatedAsMutable(this, variable, local);
            return;
        }

        if (JCTreeCatalog.hasFinalModifier(variable)) {
            listener.alreadyMarkedAsFinal(this, variable, local);
            return;
        }

        JCTreeCatalog.addFinalModifier(variable);
        listener.markedAsFinal(this, variable, local);
    }

    enum EventSource {
        LOCAL, FIELD, PARAMETER;
    }

    interface Listener {
        void markedAsFinal(DefaultImmutabilityRule source, VariableTree target, EventSource eventSource);
        void alreadyMarkedAsFinal(DefaultImmutabilityRule source, VariableTree target, EventSource eventSource);
        void annotatedAsMutable(DefaultImmutabilityRule source, VariableTree target, EventSource eventSource);
    }
}
