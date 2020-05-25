package ristretto.compiler.plugin;

import com.sun.source.tree.VariableTree;

final class DefaultParameterImmutabilityRule implements VariableScanner.Visitor, DefaultModifierRule {

    private final AnnotationNameResolver resolver;
    private final Listener listener;

    DefaultParameterImmutabilityRule(AnnotationNameResolver resolver, Listener listener) {
        this.resolver = resolver;
        this.listener = listener;
    }

    @Override
    public void visitParameter(VariableTree parameter) {
        if (JCTreeCatalog.isAnnotatedAsMutable(parameter, resolver)) {
            listener.modifierNotAdded(this, parameter);
            return;
        }

        if (JCTreeCatalog.hasFinalModifier(parameter)) {
            listener.modifierAlreadyPresent(this, parameter);
            return;
        }

        JCTreeCatalog.addFinalModifier(parameter);
        listener.modifierAdded(this, parameter);
    }
}
