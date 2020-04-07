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

    @Override
    public QualifiedClassNameResolver visitImport(ImportTree importTree, QualifiedClassNameResolver resolver) {
        resolver.importClass(toQualifiedImport(importTree));
        return super.visitImport(importTree, resolver);
    }

    private QualifiedImport toQualifiedImport(ImportTree importTree) {
        return QualifiedImport.of(QualifiedName.parse(importTree.getQualifiedIdentifier().toString()));
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
            .map(this::toQualifiedName)
            .map(resolver::resolve)
            .noneMatch(QualifiedClassName.NULLABLE_ANNOTATION::equals);
    }

    private QualifiedClassName toQualifiedName(AnnotationTree annotation) {
        return QualifiedClassName.parse(annotation.getAnnotationType().toString());
    }

    static NullCheckForPublicMethodParameter of(Context context) {
        return new NullCheckForPublicMethodParameter(context);
    }

}
