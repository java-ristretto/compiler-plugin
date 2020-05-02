package ristretto.compiler.plugin;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

import java.util.LinkedList;

final class LocalVariableFinalModifier extends TreeScanner<LocalVariableFinalModifier.Context, LocalVariableFinalModifier.Context> {

    static final TreeScanner<Context, Context> INSTANCE = new LocalVariableFinalModifier();

    private LocalVariableFinalModifier() {
    }

    static Context newContext(Observer observer) {
        return new Context(observer);
    }

    @Override
    public Context visitImport(ImportTree importTree, Context context) {
        context.resolver.importClass(importTree.getQualifiedIdentifier().toString());
        return super.visitImport(importTree, context);
    }

    @Override
    public Context visitClass(ClassTree node, Context context) {
        context.enterClass();
        Context result = super.visitClass(node, context);
        context.leave();
        return result;
    }

    @Override
    public Context visitBlock(BlockTree block, Context context) {
        context.enterBlock();
        Context result = super.visitBlock(block, context);
        context.leave();
        return result;
    }

    @Override
    public Context visitForLoop(ForLoopTree forLoop, Context context) {
        context.enterForLoop();
        Context result = super.visitForLoop(forLoop, context);
        context.leave();
        return result;
    }

    @Override
    public Context visitEnhancedForLoop(EnhancedForLoopTree forLoop, Context context) {
        context.enterForLoop();
        Context result = super.visitEnhancedForLoop(forLoop, context);
        context.leave();
        return result;
    }

    @Override
    public Context visitVariable(VariableTree variable, Context context) {
        if (context.isOutsideBlock()) {
            return super.visitVariable(variable, context);
        }

        if (JCTreeCatalog.isAnnotatedAsMutable(variable, context.resolver)) {
            context.observer.localVariableSkipped();
            return super.visitVariable(variable, context);
        }

        JCTreeCatalog.addFinalModifier(variable);
        context.observer.localVariableMarkedAsFinal();
        return super.visitVariable(variable, context);
    }

    private enum Scope {
        BLOCK, CLASS, FOR_LOOP
    }

    public static final class Context {

        private final AnnotationNameResolver resolver;
        private final LinkedList<Scope> scopeStack = new LinkedList<>();
        private final Observer observer;

        private Context(Observer observer) {
            this.observer = observer;
            this.resolver = AnnotationNameResolver.newResolver();
        }

        private void enterClass() {
            scopeStack.push(Scope.CLASS);
        }

        private void enterBlock() {
            scopeStack.push(Scope.BLOCK);
        }

        private void enterForLoop() {
            scopeStack.push(Scope.FOR_LOOP);
        }

        private void leave() {
            scopeStack.pop();
        }

        private boolean isOutsideBlock() {
            return !Scope.BLOCK.equals(scopeStack.peek());
        }
    }

    public interface Observer {
        void localVariableMarkedAsFinal();
        void localVariableSkipped();
    }
}
