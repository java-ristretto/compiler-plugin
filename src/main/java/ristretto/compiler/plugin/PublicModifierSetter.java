package ristretto.compiler.plugin;

final class PublicModifierSetter implements DefaultModifierRule.Listener {

    static final DefaultModifierRule.Listener INSTANCE = new PublicModifierSetter();

    private PublicModifierSetter() {
    }

    @Override
    public void modifierAdded(DefaultModifierRule source, ModifierTarget target) {
        target.addPublicModifier();
    }
}
