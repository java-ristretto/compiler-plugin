package ristretto.compiler.plugin;

final class DefaultFieldImmutabilityRule implements VariableScanner.Visitor, DefaultModifierRule {

    private final Listener listener;

    DefaultFieldImmutabilityRule(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void visitClassField(Variable field) {
        handleVariable(field);
    }

    @Override
    public void visitEnumField(Variable field) {
        if (field.hasStaticModifier()) {
            return;
        }

        handleVariable(field);
    }

    private void handleVariable(Variable variable) {
        if (variable.hasMutableAnnotation()) {
            listener.modifierNotAdded(this, variable);
            return;
        }

        if (variable.hasFinalModifier()) {
            listener.modifierAlreadyPresent(this, variable);
            return;
        }

        listener.modifierAdded(this, variable);
    }
}
