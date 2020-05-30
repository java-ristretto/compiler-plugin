package ristretto.compiler.plugin;

final class PrivateModifierSetter implements DefaultModifierRule.Listener {

    static final DefaultModifierRule.Listener INSTANCE = new PrivateModifierSetter();

    private PrivateModifierSetter() {
    }

    @Override
    public void modifierAdded(DefaultModifierRule source, ModifierTarget target) {
        target.addPrivateModifier();
    }
}
