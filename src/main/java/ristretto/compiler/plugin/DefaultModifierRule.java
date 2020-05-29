package ristretto.compiler.plugin;

import com.sun.source.tree.VariableTree;

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
}
