package ristretto.compiler.plugin;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

final class DefaultImmutabilityRule extends TreeScanner<Void, Scope> {

    private final AnnotationNameResolver resolver;
    private final Observer observer;

    DefaultImmutabilityRule(AnnotationNameResolver resolver, Observer observer) {
        this.resolver = resolver;
        this.observer = observer;
    }

    @Override
    public Void visitClass(ClassTree aClass, Scope scope) {
        if (aClass.getKind().equals(Tree.Kind.ENUM)) {
            return super.visitClass(aClass, Scope.ENUM);
        }
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
    public Void visitForLoop(ForLoopTree forLoop, Scope scope) {
        return super.visitForLoop(forLoop, Scope.FOR_LOOP);
    }

    @Override
    public Void visitEnhancedForLoop(EnhancedForLoopTree forLoop, Scope scope) {
        return super.visitEnhancedForLoop(forLoop, Scope.FOR_LOOP);
    }

    @Override
    public Void visitVariable(VariableTree variable, Scope scope) {
        if (Scope.FOR_LOOP.equals(scope)) {
            return super.visitVariable(variable, scope);
        }

        if (JCTreeCatalog.isAnnotatedAsMutable(variable, resolver)) {
            observer.annotatedAsMutable(EventSource.from(scope));
            return super.visitVariable(variable, scope);
        }

        if (JCTreeCatalog.hasFinalModifier(variable)) {
            if (!Scope.ENUM.equals(scope) || !JCTreeCatalog.hasStaticModifier(variable)) {
                observer.alreadyMarkedAsFinal(variable, EventSource.from(scope));
            }
            return super.visitVariable(variable, scope);
        }

        JCTreeCatalog.addFinalModifier(variable);
        observer.markedAsFinal(EventSource.from(scope));
        return super.visitVariable(variable, scope);
    }

    enum EventSource {
        LOCAL, FIELD, PARAMETER;

        private static EventSource from(Scope scope) {
            switch (scope) {
                case BLOCK:
                    return EventSource.LOCAL;
                case CLASS:
                case ENUM:
                    return EventSource.FIELD;
                case METHOD:
                    return EventSource.PARAMETER;
                default:
                    throw new AssertionError("cannot handle variable scope " + scope);
            }
        }
    }

    interface Observer {
        void markedAsFinal(EventSource eventSource);
        void alreadyMarkedAsFinal(VariableTree variable, EventSource eventSource);
        void annotatedAsMutable(EventSource eventSource);
    }
}
