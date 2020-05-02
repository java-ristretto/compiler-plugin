package ristretto.compiler.plugin;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

import java.util.LinkedList;

final class MethodParameterFinalModifier extends TreeScanner<MethodParameterFinalModifier.Context, MethodParameterFinalModifier.Context> {

    static final TreeScanner<Context, Context> INSTANCE = new MethodParameterFinalModifier();

    private MethodParameterFinalModifier() {
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
    public Context visitMethod(MethodTree method, Context context) {
        context.enterMethod();
        Context result = super.visitMethod(method, context);
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
    public Context visitVariable(VariableTree variable, Context context) {
        if (context.isNotMethod() || !noMutableAnnotation(context, variable)) {
            return super.visitVariable(variable, context);
        }

        JCTreeCatalog.addFinalModifier(variable);
        return super.visitVariable(variable, context);
    }

    private boolean noMutableAnnotation(Context context, VariableTree parameter) {
        if (JCTreeCatalog.isAnnotatedAsMutable(parameter, context.resolver)) {
            context.observer.parameterSkipped();
            return false;
        }

        context.observer.parameterMarkedAsFinal();
        return true;
    }

    private enum VariableScope {
        BLOCK, CLASS, METHOD, FOR_LOOP
    }

    public static final class Context {

        private final AnnotationNameResolver resolver;
        private final Observer observer;
        private final LinkedList<VariableScope> variableScopeStack = new LinkedList<>();

        private Context(AnnotationNameResolver resolver, Observer observer) {
            this.resolver = resolver;
            this.observer = observer;
        }

        static Context of(Observer observer) {
            return new Context(AnnotationNameResolver.newResolver(), observer);
        }

        private void enterClass() {
            variableScopeStack.push(VariableScope.CLASS);
        }

        private void enterMethod() {
            variableScopeStack.push(VariableScope.METHOD);
        }

        private void enterBlock() {
            variableScopeStack.push(VariableScope.BLOCK);
        }

        private void enterForLoop() {
            variableScopeStack.push(VariableScope.FOR_LOOP);
        }

        private void leave() {
            variableScopeStack.pop();
        }

        private boolean isNotMethod() {
            return !VariableScope.METHOD.equals(variableScopeStack.peek());
        }
    }

    public interface Observer {
        void parameterMarkedAsFinal();
        void parameterSkipped();
    }

}
