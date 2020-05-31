package ristretto.compiler.plugin;

// TODO: skip interface methods
final class DefaultMethodAccessRule implements VariableScanner.Visitor, DefaultModifierRule {

    private final Listener listener;

    DefaultMethodAccessRule(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void visitEnumMethod(ModifierTarget method) {
        handle(method);
    }

    @Override
    public void visitClassConstructor(ModifierTarget constructor) {
        handle(constructor);
    }

    @Override
    public void visitClassMethod(ModifierTarget method) {
        handle(method);
    }

    private void handle(ModifierTarget method) {
        if (method.hasProtectedModifier() || method.hasPrivateModifier()) {
            return;
        }

        if (method.hasPackagePrivateAnnotation()) {
            listener.modifierNotAdded(this, method);
            return;
        }

        if (method.hasPublicModifier()) {
            listener.modifierAlreadyPresent(this, method);
            return;
        }

        listener.modifierAdded(this, method);
    }
}
