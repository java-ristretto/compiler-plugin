package ristretto.compiler.plugin;

interface DefaultModifierRule {

    interface Listener {

        default void modifierAdded(DefaultModifierRule source, Variable target) {
        }

        default void modifierAlreadyPresent(DefaultModifierRule source, Variable target) {
        }

        default void modifierNotAdded(DefaultModifierRule source, Variable target) {
        }

        default Listener andThen(Listener next) {
            return new Listener() {

                @Override
                public void modifierAdded(DefaultModifierRule source, Variable target) {
                    Listener.this.modifierAdded(source, target);
                    next.modifierAdded(source, target);
                }

                @Override
                public void modifierAlreadyPresent(DefaultModifierRule source, Variable target) {
                    Listener.this.modifierAlreadyPresent(source, target);
                    next.modifierAlreadyPresent(source, target);
                }

                @Override
                public void modifierNotAdded(DefaultModifierRule source, Variable target) {
                    Listener.this.modifierNotAdded(source, target);
                    next.modifierNotAdded(source, target);
                }
            };
        }
    }
}
