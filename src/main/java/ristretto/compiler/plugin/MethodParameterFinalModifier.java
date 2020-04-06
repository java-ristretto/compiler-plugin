package ristretto.compiler.plugin;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

final class MethodParameterFinalModifier extends TreeScanner<QualifiedClassNameResolver, QualifiedClassNameResolver> {

    static final TreeScanner<QualifiedClassNameResolver, QualifiedClassNameResolver> INSTANCE = new MethodParameterFinalModifier();

    private MethodParameterFinalModifier() {
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
        method.getParameters()
            .stream()
            .filter(parameter -> noMutableAnnotation(resolver, parameter))
            .forEach(JCTreeCatalog::addFinalModifier);
        return super.visitMethod(method, resolver);
    }

    private boolean noMutableAnnotation(QualifiedClassNameResolver resolver, VariableTree parameter) {
        return parameter.getModifiers()
            .getAnnotations()
            .stream()
            .map(this::toQualifiedName)
            .map(resolver::resolve)
            .noneMatch(QualifiedClassName.MUTABLE_ANNOTATION::equals);
    }

    private QualifiedName toQualifiedName(AnnotationTree annotation) {
        return QualifiedName.parse(annotation.getAnnotationType().toString());
    }
}
