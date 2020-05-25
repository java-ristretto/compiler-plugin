package ristretto.compiler.plugin;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

final class VariableScanner extends TreeScanner<Void, Scope> {

    private final Visitor visitor;

    private VariableScanner(Visitor visitor) {
        this.visitor = visitor;
    }

    static void scan(CompilationUnitTree compilationUnit, Visitor visitor) {
        compilationUnit.accept(new VariableScanner(visitor), Scope.COMPILATION_UNIT);
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
    public Void visitForLoop(ForLoopTree forLoop, Scope scope) {
        return super.visitForLoop(forLoop, Scope.FOR_LOOP);
    }

    @Override
    public Void visitBlock(BlockTree block, Scope scope) {
        return super.visitBlock(block, Scope.BLOCK);
    }

    @Override
    public Void visitVariable(VariableTree variable, Scope scope) {
        switch (scope) {
            case BLOCK:
                visitor.visitLocalVariable(variable);
                break;
            case CLASS:
                visitor.visitField(variable);
                visitor.visitClassField(variable);
                break;
            case ENUM:
                visitor.visitField(variable);
                visitor.visitEnumField(variable);
                break;
            case METHOD:
                visitor.visitParameter(variable);
                break;
        }
        return super.visitVariable(variable, scope);
    }

    interface Visitor {

        default void visitLocalVariable(VariableTree localVariable) {
        }

        default void visitField(VariableTree field) {
        }

        default void visitClassField(VariableTree field) {
        }

        default void visitEnumField(VariableTree field) {
        }

        default void visitParameter(VariableTree parameter) {
        }
    }

}
