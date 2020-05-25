package ristretto.compiler.plugin;

import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.VariableTree;

import javax.lang.model.element.Modifier;
import java.util.Set;

final class DefaultFieldAccessRule implements VariableScanner.Visitor {

    private final AnnotationNameResolver resolver;
    private final Listener listener;

    DefaultFieldAccessRule(AnnotationNameResolver resolver, Listener listener) {
        this.resolver = resolver;
        this.listener = listener;
    }

    private static boolean hasExplicitAccessModifier(ModifiersTree modifiers) {
        Set<Modifier> flags = modifiers.getFlags();
        return flags.contains(Modifier.PUBLIC) || flags.contains(Modifier.PROTECTED);
    }

    @Override
    public void visitField(VariableTree field) {
        if (hasExplicitAccessModifier(field.getModifiers())) {
            return;
        }

        if (JCTreeCatalog.isAnnotatedAsPackagePrivate(field, resolver)) {
            listener.annotatedAsPackagePrivate(this, field);
            return;
        }

        JCTreeCatalog.setPrivateModifier(field);
        listener.markedAsPrivate(this, field);
    }

    interface Listener {
        void markedAsPrivate(DefaultFieldAccessRule source, VariableTree target);
        void annotatedAsPackagePrivate(DefaultFieldAccessRule source, VariableTree target);
    }
}
