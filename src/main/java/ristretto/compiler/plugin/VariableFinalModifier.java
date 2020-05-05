package ristretto.compiler.plugin;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

final class VariableFinalModifier extends TreeScanner<Void, VariableFinalModifier.VariableScope> {

    private final AnnotationNameResolver resolver;
    private final Observer observer;

    private VariableFinalModifier(AnnotationNameResolver resolver, Observer observer) {
        this.resolver = resolver;
        this.observer = observer;
    }

    static VariableFinalModifier newInstance(Observer observer) {
        return new VariableFinalModifier(AnnotationNameResolver.newResolver(), observer);
    }

    @Override
    public Void visitImport(ImportTree importTree, VariableScope scope) {
        resolver.importClass(importTree.getQualifiedIdentifier().toString());
        return super.visitImport(importTree, scope);
    }

    @Override
    public Void visitClass(ClassTree aClass, VariableScope scope) {
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
            observer.skipped(scope);
            return super.visitVariable(variable, scope);
        }

        JCTreeCatalog.addFinalModifier(variable);
        observer.markedAsFinal(scope);
        return super.visitVariable(variable, scope);
    }

    enum VariableScope {
        BLOCK, CLASS, METHOD, FOR_LOOP
    }

    interface Observer {
        void markedAsFinal(VariableScope scope);
        void skipped(VariableScope scope);
    }
}
