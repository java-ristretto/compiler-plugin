package ristretto.compiler.plugin;

import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

final class LocalVariableFinalModifier extends TreeScanner<LocalVariableFinalModifier.Context, LocalVariableFinalModifier.Context> {

    static final TreeScanner<Context, Context> INSTANCE = new LocalVariableFinalModifier();

    private LocalVariableFinalModifier() {
    }

    public static Context newContext() {
        return new Context();
    }

    @Override
    public Context visitMethod(MethodTree method, Context context) {
        System.out.println("method: " + method);
        context.enterMethod(
            method.getParameters()
                .stream()
                .collect(Collectors.toUnmodifiableSet())
        );
        Context result = super.visitMethod(method, context);
        context.leaveMethod();
        return result;
    }

    @Override
    public Context visitForLoop(ForLoopTree forLoop, Context context) {
        context.enterForLoop(
            forLoop.getInitializer()
                .stream()
                .filter(statement -> statement instanceof VariableTree)
                .map(statement -> (VariableTree) statement)
                .collect(Collectors.toUnmodifiableSet())
        );
        Context result = super.visitForLoop(forLoop, context);
        context.leaveForLoop();
        return result;
    }

    @Override
    public Context visitVariable(VariableTree variable, Context context) {
        if (context.shouldSkip(variable)) {
            return super.visitVariable(variable, context);
        }

        JCTreeCatalog.addFinalModifier(variable);
        return super.visitVariable(variable, context);
    }

    public static final class Context {

        private final LinkedList<Set<VariableTree>> methodParameters = new LinkedList<>();
        private final LinkedList<Set<VariableTree>> forLoopVariables = new LinkedList<>();
        private boolean outsideMethod = true;

        private Context() {
        }

        private void enterForLoop(Set<VariableTree> variables) {
            forLoopVariables.push(variables);
        }

        private void leaveForLoop() {
            forLoopVariables.pop();
        }

        private boolean isDefinedInForLoop(VariableTree variable) {
            for (Set<VariableTree> variables : forLoopVariables) {
                if (variables.contains(variable)) {
                    return true;
                }
            }
            return false;
        }

        private void enterMethod(Set<VariableTree> variables) {
            methodParameters.push(variables);
            outsideMethod = false;
        }

        private void leaveMethod() {
            methodParameters.pop();
            outsideMethod = true;
        }

        private boolean isMethodParameter(VariableTree variable) {
            for (Set<VariableTree> variables : methodParameters) {
                if (variables.contains(variable)) {
                    return true;
                }
            }
            return false;
        }

        public boolean shouldSkip(VariableTree variable) {
            return outsideMethod || isMethodParameter(variable) || isDefinedInForLoop(variable);
        }
    }
}
