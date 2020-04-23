package ristretto.compiler.plugin;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

final class NullCheckForPublicMethodParameter extends TreeScanner<AnnotationNameResolver, AnnotationNameResolver> {

    private final Context context;

    private NullCheckForPublicMethodParameter(Context context) {
        this.context = context;
    }

    static NullCheckForPublicMethodParameter of(Context context) {
        return new NullCheckForPublicMethodParameter(context);
    }

    @Override
    public AnnotationNameResolver visitImport(ImportTree importTree, AnnotationNameResolver resolver) {
        resolver.importClass(importTree.getQualifiedIdentifier().toString());
        return super.visitImport(importTree, resolver);
    }

    @Override
    public AnnotationNameResolver visitMethod(MethodTree method, AnnotationNameResolver resolver) {
        if (isAbstractOrNative(method)) {
            return super.visitMethod(method, resolver);
        }

        JCTreeCatalog catalog = JCTreeCatalog.of(context);

        List<JCTree.JCStatement> nullChecks = method.getParameters()
            .stream()
            .filter(JCTreeCatalog::notPrimitiveType)
            .filter(parameter -> noNullableAnnotation(resolver, parameter))
            .map(catalog::nullCheck)
            .collect(List.collector());

        JCTree.JCBlock body = (JCTree.JCBlock) method.getBody();
        if (isCallingSuper(body)) {
            return super.visitMethod(method, resolver);
        }

        body.stats = body.stats.prependList(nullChecks);

        return super.visitMethod(method, resolver);
    }

    private boolean isCallingSuper(JCTree.JCBlock body) {
        JCTree.JCStatement firstStatement = body.stats.head;
        if (firstStatement.getKind().equals(Tree.Kind.EXPRESSION_STATEMENT)) {
            if (firstStatement instanceof JCTree.JCExpressionStatement) {
                JCTree.JCExpressionStatement expressionStatement = (JCTree.JCExpressionStatement) firstStatement;
                if (expressionStatement.getExpression() instanceof JCTree.JCMethodInvocation) {
                    JCTree.JCMethodInvocation methodInvocation = (JCTree.JCMethodInvocation) expressionStatement.getExpression();
                    JCTree.JCExpression methodSelect = methodInvocation.getMethodSelect();
                    if (methodSelect.getKind().equals(Tree.Kind.IDENTIFIER)) {
                        if (((JCTree.JCIdent) methodSelect).getName().contentEquals("super")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isAbstractOrNative(MethodTree method) {
        return method.getBody() == null;
    }

    private boolean noNullableAnnotation(AnnotationNameResolver resolver, VariableTree parameter) {
        return parameter.getModifiers()
            .getAnnotations()
            .stream()
            .map(AnnotationTree::getAnnotationType)
            .map(Object::toString)
            .noneMatch(resolver::isNullable);
    }
}
