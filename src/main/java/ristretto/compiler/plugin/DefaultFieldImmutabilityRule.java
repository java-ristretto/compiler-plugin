package ristretto.compiler.plugin;

final class DefaultFieldImmutabilityRule implements VariableScanner.Visitor, DefaultModifierRule {

    private final Listener listener;

    DefaultFieldImmutabilityRule(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void visitClassField(ModifierTarget field) {
        handleVariable(field);
    }

    @Override
    public void visitEnumField(ModifierTarget field) {
        if (field.hasStaticModifier()) {
            return;
        }

        handleVariable(field);
    }

    private void handleVariable(ModifierTarget variable) {
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
