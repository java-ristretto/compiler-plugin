package ristretto.compiler.plugin;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

import javax.lang.model.element.Modifier;

final class DefaultPrivateAccessRule extends TreeScanner<Void, DefaultPrivateAccessRule.VariableScope> {

    @Override
    public Void visitClass(ClassTree aClass, VariableScope scope) {
        return super.visitClass(aClass, VariableScope.CLASS);
    }

    @Override
    public Void visitMethod(MethodTree node, VariableScope variableScope) {
        return super.visitMethod(node, VariableScope.METHOD);
    }

    @Override
    public Void visitBlock(BlockTree block, VariableScope scope) {
        return super.visitBlock(block, VariableScope.BLOCK);
    }

    @Override
    public Void visitVariable(VariableTree variable, VariableScope scope) {
        if (!VariableScope.CLASS.equals(scope)) {
            return super.visitVariable(variable, scope);
        }

        if (variable.getModifiers().getFlags().contains(Modifier.PUBLIC)) {
            return super.visitVariable(variable, scope);
        }

        if (variable.getModifiers().getFlags().contains(Modifier.PROTECTED)) {
            return super.visitVariable(variable, scope);
        }

        JCTreeCatalog.setPrivateModifier(variable);
        return super.visitVariable(variable, scope);
    }

    enum VariableScope {
        BLOCK, METHOD, CLASS
    }

}