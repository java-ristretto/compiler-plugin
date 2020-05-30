package ristretto.compiler.plugin;

final class DefaultParameterImmutabilityRule implements VariableScanner.Visitor, DefaultModifierRule {

    private final Listener listener;

    DefaultParameterImmutabilityRule(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void visitParameter(Variable parameter) {
        if (parameter.hasMutableAnnotation()) {
            listener.modifierNotAdded(this, parameter);
            return;
        }

        if (parameter.hasFinalModifier()) {
            listener.modifierAlreadyPresent(this, parameter);
            return;
        }

        listener.modifierAdded(this, parameter);
    }
}
