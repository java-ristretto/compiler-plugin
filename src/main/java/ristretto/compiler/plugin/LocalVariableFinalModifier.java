package ristretto.compiler.plugin;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

final class LocalVariableFinalModifier extends TreeScanner<LocalVariableFinalModifier.Context, LocalVariableFinalModifier.Context> {

    static final TreeScanner<Context, Context> INSTANCE = new LocalVariableFinalModifier();

    private LocalVariableFinalModifier() {
    }

    @Override
    public Context visitClass(ClassTree node, Context context) {
        return super.visitClass(node, new Context());
    }

    @Override
    public Context visitBlock(BlockTree block, Context context) {
        context.enterBlock();
        Context result = super.visitBlock(block, context);
        context.leaveBlock();
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

        private final LinkedList<Set<VariableTree>> forLoopVariables = new LinkedList<>();
        private int blockLevel;

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

        public boolean shouldSkip(VariableTree variable) {
            return blockLevel == 0 || isDefinedInForLoop(variable);
        }

        public void enterBlock() {
            blockLevel += 1;
        }

        public void leaveBlock() {
            blockLevel -= 1;
        }
    }
}
