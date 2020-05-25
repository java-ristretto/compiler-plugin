package ristretto.compiler.plugin;

import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.VariableTree;

import javax.lang.model.element.Modifier;
import java.util.Set;

final class DefaultFieldAccessRule implements VariableScanner.Visitor {

    private final AnnotationNameResolver resolver;
    private final Observer observer;

    DefaultFieldAccessRule(AnnotationNameResolver resolver, Observer observer) {
        this.resolver = resolver;
        this.observer = observer;
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
            observer.annotatedAsPackagePrivate(EventSource.FIELD);
            return;
        }

        JCTreeCatalog.setPrivateModifier(field);
        observer.markedAsPrivate(EventSource.FIELD);
    }

    enum EventSource {
        FIELD
    }

    interface Observer {
        void markedAsPrivate(EventSource eventSource);
        void annotatedAsPackagePrivate(EventSource eventSource);
    }
}
