package ristretto.compiler.plugin;

final class FinalModifierSetter implements DefaultModifierRule.Listener {

    static final DefaultModifierRule.Listener INSTANCE = new FinalModifierSetter();

    private FinalModifierSetter() {
    }

    @Override
    public void modifierAdded(DefaultModifierRule source, Variable target) {
        target.addFinalModifier();
    }
}
