package ristretto.compiler.plugin;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

import javax.tools.JavaFileObject;

final class VariableScanner extends TreeScanner<Void, Scope> {

    private final JavaFileObject javaFile;
    private final Visitor visitor;
    private final AnnotationNameResolver resolver;

    private VariableScanner(JavaFileObject javaFile, Visitor visitor, AnnotationNameResolver resolver) {
        this.javaFile = javaFile;
        this.visitor = visitor;
        this.resolver = resolver;
    }

    static void scan(CompilationUnitTree compilationUnit, Visitor visitor) {
        AnnotationNameResolver resolver = new AnnotationNameResolver(ImportDeclaration.of(compilationUnit.getImports()));

        compilationUnit.accept(
            new VariableScanner(compilationUnit.getSourceFile(), visitor, resolver),
            Scope.COMPILATION_UNIT
        );
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
        JCVariableDeclWrapper wrapper = new JCVariableDeclWrapper(javaFile, variable, resolver);

        switch (scope) {
            case BLOCK:
                visitor.visitLocalVariable(wrapper);
                break;
            case CLASS:
                visitor.visitField(wrapper);
                visitor.visitClassField(wrapper);
                break;
            case ENUM:
                visitor.visitField(wrapper);
                visitor.visitEnumField(wrapper);
                break;
            case METHOD:
                visitor.visitParameter(wrapper);
                break;
        }
        return super.visitVariable(variable, scope);
    }

    interface Visitor {

        default void visitLocalVariable(Variable localVariable) {
        }

        default void visitField(Variable field) {
        }

        default void visitClassField(Variable field) {
        }

        default void visitEnumField(Variable field) {
        }

        default void visitParameter(Variable parameter) {
        }
    }

}
