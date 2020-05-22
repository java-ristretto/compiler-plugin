package ristretto.compiler.plugin;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

final class DefaultImmutabilityRule extends TreeScanner<Void, DefaultImmutabilityRule.VariableScope> {

    private final AnnotationNameResolver resolver;
    private final Observer observer;

    DefaultImmutabilityRule(AnnotationNameResolver resolver, Observer observer) {
        this.resolver = resolver;
        this.observer = observer;
    }

    @Override
    public Void visitClass(ClassTree aClass, VariableScope scope) {
        if (aClass.getKind().equals(Tree.Kind.ENUM)) {
            return super.visitClass(aClass, VariableScope.ENUM);
        }
        return super.visitClass(aClass, VariableScope.CLASS);
    }

    @Override
    public Void visitMethod(MethodTree method, VariableScope scope) {
        return super.visitMethod(method, VariableScope.METHOD);
    }

    @Override
    public Void visitBlock(BlockTree block, VariableScope scope) {
        return super.visitBlock(block, VariableScope.BLOCK);
    }

    @Override
    public Void visitForLoop(ForLoopTree forLoop, VariableScope scope) {
        return super.visitForLoop(forLoop, VariableScope.FOR_LOOP);
    }

    @Override
    public Void visitEnhancedForLoop(EnhancedForLoopTree forLoop, VariableScope scope) {
        return super.visitEnhancedForLoop(forLoop, VariableScope.FOR_LOOP);
    }

    @Override
    public Void visitVariable(VariableTree variable, VariableScope scope) {
        if (VariableScope.FOR_LOOP.equals(scope)) {
            return super.visitVariable(variable, scope);
        }

        if (JCTreeCatalog.isAnnotatedAsMutable(variable, resolver)) {
            observer.annotatedAsMutable(scope);
            return super.visitVariable(variable, scope);
        }

        if (JCTreeCatalog.hasFinalModifier(variable)) {
            if (!VariableScope.ENUM.equals(scope) || !JCTreeCatalog.hasStaticModifier(variable)) {
                observer.finalModifierAlreadyPresent(variable, scope);
            }
            return super.visitVariable(variable, scope);
        }

        JCTreeCatalog.addFinalModifier(variable);
        observer.finalModifierAdded(scope);
        return super.visitVariable(variable, scope);
    }

    enum VariableScope {
        BLOCK, CLASS, METHOD, ENUM, FOR_LOOP
    }

    interface Observer {
        void finalModifierAdded(VariableScope scope);
        void finalModifierAlreadyPresent(VariableTree variable, VariableScope scope);
        void annotatedAsMutable(VariableScope scope);
    }
}
