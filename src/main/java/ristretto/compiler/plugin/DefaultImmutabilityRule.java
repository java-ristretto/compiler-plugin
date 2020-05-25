package ristretto.compiler.plugin;

import com.sun.source.tree.VariableTree;

final class DefaultImmutabilityRule implements VariableScanner.Visitor {

    private final AnnotationNameResolver resolver;
    private final Observer observer;

    DefaultImmutabilityRule(AnnotationNameResolver resolver, Observer observer) {
        this.resolver = resolver;
        this.observer = observer;
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
            observer.annotatedAsMutable(local);
            return;
        }

        if (JCTreeCatalog.hasFinalModifier(variable)) {
            observer.alreadyMarkedAsFinal(variable, local);
            return;
        }

        JCTreeCatalog.addFinalModifier(variable);
        observer.markedAsFinal(local);
    }

    enum EventSource {
        LOCAL, FIELD, PARAMETER;
    }

    interface Observer {
        void markedAsFinal(EventSource eventSource);
        void alreadyMarkedAsFinal(VariableTree variable, EventSource eventSource);
        void annotatedAsMutable(EventSource eventSource);
    }
}
