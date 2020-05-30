package ristretto.compiler.plugin;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

interface DefaultModifierRule {

    interface Listener {

        default void modifierAdded(DefaultModifierRule source, Variable target) {
        }

        default void modifierAlreadyPresent(DefaultModifierRule source, Variable target) {
        }

        default void modifierNotAdded(DefaultModifierRule source, Variable target) {
        }
    }

    class Listeners implements Listener {

        private final Set<Listener> listeners;

        private Listeners(Set<Listener> listeners) {
            this.listeners = listeners;
        }

        static Listeners of(Listener... listeners) {
            return new Listeners(Stream.of(listeners).collect(Collectors.toUnmodifiableSet()));
        }

        @Override
        public void modifierAdded(DefaultModifierRule source, Variable target) {
            listeners.forEach(listener -> listener.modifierAdded(source, target));
        }

        @Override
        public void modifierAlreadyPresent(DefaultModifierRule source, Variable target) {
            listeners.forEach(listener -> listener.modifierAlreadyPresent(source, target));
        }

        @Override
        public void modifierNotAdded(DefaultModifierRule source, Variable target) {
            listeners.forEach(listener -> listener.modifierNotAdded(source, target));
        }
    }
}
