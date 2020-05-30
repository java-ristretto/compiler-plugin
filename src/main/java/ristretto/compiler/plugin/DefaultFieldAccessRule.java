package ristretto.compiler.plugin;

final class DefaultFieldAccessRule implements VariableScanner.Visitor, DefaultModifierRule {

    private final Listener listener;

    DefaultFieldAccessRule(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void visitField(Variable field) {
        if (field.hasPublicModifier() || field.hasProtectedModifier()) {
            return;
        }

        if (field.hasPackagePrivateAnnotation()) {
            listener.modifierNotAdded(this, field);
            return;
        }

        if (field.hasPrivateModifier()) {
            listener.modifierAlreadyPresent(this, field);
            return;
        }

        listener.modifierAdded(this, field);
    }
}
