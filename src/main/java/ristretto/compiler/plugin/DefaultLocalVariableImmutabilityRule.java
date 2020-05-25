package ristretto.compiler.plugin;

import com.sun.source.tree.VariableTree;

final class DefaultLocalVariableImmutabilityRule implements VariableScanner.Visitor, DefaultModifierRule {

    private final AnnotationNameResolver resolver;
    private final Listener listener;

    DefaultLocalVariableImmutabilityRule(AnnotationNameResolver resolver, Listener listener) {
        this.resolver = resolver;
        this.listener = listener;
    }

    @Override
    public void visitLocalVariable(VariableTree localVariable) {
        if (JCTreeCatalog.isAnnotatedAsMutable(localVariable, resolver)) {
            listener.modifierNotAdded(this, localVariable);
            return;
        }

        if (JCTreeCatalog.hasFinalModifier(localVariable)) {
            listener.modifierAlreadyPresent(this, localVariable);
            return;
        }

        JCTreeCatalog.addFinalModifier(localVariable);
        listener.modifierAdded(this, localVariable);
    }
}
