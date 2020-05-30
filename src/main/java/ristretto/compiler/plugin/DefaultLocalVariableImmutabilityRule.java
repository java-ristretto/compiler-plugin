package ristretto.compiler.plugin;

final class DefaultLocalVariableImmutabilityRule implements VariableScanner.Visitor, DefaultModifierRule {

    private final Listener listener;

    DefaultLocalVariableImmutabilityRule(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void visitLocalVariable(Variable localVariable) {
        if (localVariable.isAnnotatedAsMutable()) {
            listener.modifierNotAdded(this, localVariable);
            return;
        }

        if (localVariable.hasFinalModifier()) {
            listener.modifierAlreadyPresent(this, localVariable);
            return;
        }

        listener.modifierAdded(this, localVariable);
    }
}
