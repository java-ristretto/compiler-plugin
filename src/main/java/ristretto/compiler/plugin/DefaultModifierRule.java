package ristretto.compiler.plugin;

import com.sun.source.tree.VariableTree;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

interface DefaultModifierRule {
    interface Listener {

        default void modifierAdded(DefaultModifierRule source, VariableTree target) {

        }

        default void modifierAlreadyPresent(DefaultModifierRule source, VariableTree target) {

        }

        default void modifierNotAdded(DefaultModifierRule source, VariableTree target) {

        }

        default void modifierAdded(DefaultModifierRule source, LocalVariable target) {

        }

        default void modifierAlreadyPresent(DefaultModifierRule source, LocalVariable target) {

        }

        default void modifierNotAdded(DefaultModifierRule source, LocalVariable target) {

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
        public void modifierAdded(DefaultModifierRule source, LocalVariable target) {
            listeners.forEach(listener -> listener.modifierAdded(source, target));
        }

        @Override
        public void modifierAlreadyPresent(DefaultModifierRule source, LocalVariable target) {
            listeners.forEach(listener -> listener.modifierAlreadyPresent(source, target));
        }

        @Override
        public void modifierNotAdded(DefaultModifierRule source, LocalVariable target) {
            listeners.forEach(listener -> listener.modifierNotAdded(source, target));
        }
    }
}
