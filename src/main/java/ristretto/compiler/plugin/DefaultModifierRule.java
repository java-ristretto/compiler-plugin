package ristretto.compiler.plugin;

interface DefaultModifierRule {

  interface Listener {

    default void modifierAdded(DefaultModifierRule source, ModifierTarget target) {
    }

    default void modifierAlreadyPresent(DefaultModifierRule source, ModifierTarget target) {
    }

    default void modifierNotAdded(DefaultModifierRule source, ModifierTarget target) {
    }

    default Listener andThen(Listener next) {
      return new Listener() {

        @Override
        public void modifierAdded(DefaultModifierRule source, ModifierTarget target) {
          Listener.this.modifierAdded(source, target);
          next.modifierAdded(source, target);
        }

        @Override
        public void modifierAlreadyPresent(DefaultModifierRule source, ModifierTarget target) {
          Listener.this.modifierAlreadyPresent(source, target);
          next.modifierAlreadyPresent(source, target);
        }

        @Override
        public void modifierNotAdded(DefaultModifierRule source, ModifierTarget target) {
          Listener.this.modifierNotAdded(source, target);
          next.modifierNotAdded(source, target);
        }
      };
    }
  }
}
