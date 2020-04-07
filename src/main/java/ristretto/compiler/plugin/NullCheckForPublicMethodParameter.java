package ristretto.compiler.plugin;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

final class NullCheckForPublicMethodParameter extends TreeScanner<QualifiedClassNameResolver, QualifiedClassNameResolver> {

    private final Context context;

    private NullCheckForPublicMethodParameter(Context context) {
        this.context = context;
    }

    static NullCheckForPublicMethodParameter of(Context context) {
        return new NullCheckForPublicMethodParameter(context);
    }

    @Override
    public QualifiedClassNameResolver visitImport(ImportTree importTree, QualifiedClassNameResolver resolver) {
        resolver.importClass(importTree.getQualifiedIdentifier().toString());
        return super.visitImport(importTree, resolver);
    }

    @Override
    public QualifiedClassNameResolver visitMethod(MethodTree method, QualifiedClassNameResolver resolver) {
        JCTreeCatalog catalog = JCTreeCatalog.of(context);

        List<JCTree.JCStatement> nullChecks = method.getParameters()
            .stream()
            .filter(JCTreeCatalog::notPrimitiveType)
            .filter(parameter -> noNullableAnnotation(resolver, parameter))
            .map(catalog::nullCheck)
            .collect(List.collector());

        JCTree.JCBlock body = (JCTree.JCBlock) method.getBody();
        body.stats = body.stats.prependList(nullChecks);

        return super.visitMethod(method, resolver);
    }

    private boolean noNullableAnnotation(QualifiedClassNameResolver resolver, VariableTree parameter) {
        return parameter.getModifiers()
            .getAnnotations()
            .stream()
            .map(AnnotationTree::getAnnotationType)
            .map(Object::toString)
            .noneMatch(resolver::isNullable);
    }
}
