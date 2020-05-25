package ristretto.compiler.plugin;

import com.sun.source.tree.VariableTree;

final class DefaultFieldImmutabilityRule implements VariableScanner.Visitor, DefaultModifierRule {

    private final AnnotationNameResolver resolver;
    private final Listener listener;

    DefaultFieldImmutabilityRule(AnnotationNameResolver resolver, Listener listener) {
        this.resolver = resolver;
        this.listener = listener;
    }

    @Override
    public void visitClassField(VariableTree field) {
        handleVariable(field);
    }

    @Override
    public void visitEnumField(VariableTree field) {
        if (JCTreeCatalog.hasStaticModifier(field)) {
            return;
        }

        handleVariable(field);
    }

    private void handleVariable(VariableTree variable) {
        if (JCTreeCatalog.isAnnotatedAsMutable(variable, resolver)) {
            listener.modifierNotAdded(this, variable);
            return;
        }

        if (JCTreeCatalog.hasFinalModifier(variable)) {
            listener.modifierAlreadyPresent(this, variable);
            return;
        }

        JCTreeCatalog.addFinalModifier(variable);
        listener.modifierAdded(this, variable);
    }
}
