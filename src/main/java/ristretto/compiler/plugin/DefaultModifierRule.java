package ristretto.compiler.plugin;

import com.sun.source.tree.VariableTree;

interface DefaultModifierRule {
    interface Listener {
        void modifierAdded(DefaultModifierRule source, VariableTree target);
        void modifierAlreadyPresent(DefaultModifierRule source, VariableTree target);
        void modifierNotAdded(DefaultModifierRule source, VariableTree target);
    }
}
