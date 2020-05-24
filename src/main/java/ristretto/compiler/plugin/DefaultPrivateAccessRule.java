package ristretto.compiler.plugin;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

import javax.lang.model.element.Modifier;
import java.util.Set;

final class DefaultPrivateAccessRule extends TreeScanner<Void, VariableScope> {

    private final AnnotationNameResolver resolver;
    private final Observer observer;

    DefaultPrivateAccessRule(AnnotationNameResolver resolver, Observer observer) {
        this.resolver = resolver;
        this.observer = observer;
    }

    private static boolean hasExplicitAccessModifier(ModifiersTree modifiers) {
        Set<Modifier> flags = modifiers.getFlags();
        return flags.contains(Modifier.PUBLIC) || flags.contains(Modifier.PROTECTED);
    }

    @Override
    public Void visitClass(ClassTree aClass, VariableScope scope) {
        if (hasExplicitAccessModifier(aClass.getModifiers())) {
            return super.visitClass(aClass, VariableScope.CLASS);
        }

        if (JCTreeCatalog.isAnnotatedAsPackagePrivate(aClass, resolver)) {
            observer.typeAnnotatedAsPackagePrivate();
            return super.visitClass(aClass, VariableScope.CLASS);
        }

        JCTreeCatalog.setPrivateModifier(aClass);
        observer.typeMarkedAsPrivate();
        return super.visitClass(aClass, VariableScope.CLASS);
    }

    @Override
    public Void visitMethod(MethodTree method, VariableScope scope) {
        if (hasExplicitAccessModifier(method.getModifiers())) {
            return super.visitMethod(method, VariableScope.METHOD);
        }

        if (JCTreeCatalog.isAnnotatedAsPackagePrivate(method, resolver)) {
            observer.methodAnnotatedAsPackagePrivate();
            return super.visitMethod(method, VariableScope.METHOD);
        }

        JCTreeCatalog.setPrivateModifier(method);
        observer.methodMarkedAsPrivate();
        return super.visitMethod(method, VariableScope.METHOD);
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

        if (hasExplicitAccessModifier(variable.getModifiers())) {
            return super.visitVariable(variable, scope);
        }

        if (JCTreeCatalog.isAnnotatedAsPackagePrivate(variable, resolver)) {
            observer.fieldAnnotatedAsPackagePrivate();
            return super.visitVariable(variable, scope);
        }

        JCTreeCatalog.setPrivateModifier(variable);
        observer.fieldMarkedAsPrivate();
        return super.visitVariable(variable, scope);
    }

    interface Observer {
        void fieldMarkedAsPrivate();
        void fieldAnnotatedAsPackagePrivate();

        void methodMarkedAsPrivate();
        void methodAnnotatedAsPackagePrivate();

        void typeMarkedAsPrivate();
        void typeAnnotatedAsPackagePrivate();
    }
}
