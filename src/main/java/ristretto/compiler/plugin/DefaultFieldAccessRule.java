package ristretto.compiler.plugin;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

import javax.lang.model.element.Modifier;
import java.util.Set;

final class DefaultFieldAccessRule extends TreeScanner<Void, Scope> {

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
    public Void visitClass(ClassTree aClass, Scope scope) {
        return super.visitClass(aClass, Scope.CLASS);
    }

    @Override
    public Void visitMethod(MethodTree method, Scope scope) {
        return super.visitMethod(method, Scope.METHOD);
    }

    @Override
    public Void visitBlock(BlockTree block, Scope scope) {
        return super.visitBlock(block, Scope.BLOCK);
    }

    @Override
    public Void visitVariable(VariableTree variable, Scope scope) {
        if (!Scope.CLASS.equals(scope)) {
            return super.visitVariable(variable, scope);
        }

        if (hasExplicitAccessModifier(variable.getModifiers())) {
            return super.visitVariable(variable, scope);
        }

        if (JCTreeCatalog.isAnnotatedAsPackagePrivate(variable, resolver)) {
            observer.annotatedAsPackagePrivate(EventSource.FIELD);
            return super.visitVariable(variable, scope);
        }

        JCTreeCatalog.setPrivateModifier(variable);
        observer.markedAsPrivate(EventSource.FIELD);
        return super.visitVariable(variable, scope);
    }

    enum EventSource {
        FIELD
    }

    interface Observer {
        void markedAsPrivate(EventSource eventSource);
        void annotatedAsPackagePrivate(EventSource eventSource);
    }
}
