package ristretto.compiler.plugin;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

final class MethodParameterFinalModifier extends TreeScanner<AnnotationNameResolver, AnnotationNameResolver> {

    static final TreeScanner<AnnotationNameResolver, AnnotationNameResolver> INSTANCE = new MethodParameterFinalModifier();

    private MethodParameterFinalModifier() {
    }

    @Override
    public AnnotationNameResolver visitImport(ImportTree importTree, AnnotationNameResolver resolver) {
        resolver.importClass(importTree.getQualifiedIdentifier().toString());
        return super.visitImport(importTree, resolver);
    }

    @Override
    public AnnotationNameResolver visitMethod(MethodTree method, AnnotationNameResolver resolver) {
        method.getParameters()
            .stream()
            .filter(parameter -> noMutableAnnotation(resolver, parameter))
            .forEach(JCTreeCatalog::addFinalModifier);
        return super.visitMethod(method, resolver);
    }

    private boolean noMutableAnnotation(AnnotationNameResolver resolver, VariableTree parameter) {
        return parameter.getModifiers()
            .getAnnotations()
            .stream()
            .map(AnnotationTree::getAnnotationType)
            .map(Object::toString)
            .noneMatch(resolver::isMutable);
    }
}
