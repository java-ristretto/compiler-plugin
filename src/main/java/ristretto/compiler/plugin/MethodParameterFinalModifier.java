package ristretto.compiler.plugin;

import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import ristretto.Mutable;

final class MethodParameterFinalModifier extends TreeScanner<QualifiedClassNameResolver, QualifiedClassNameResolver> {

    static final TreeScanner<QualifiedClassNameResolver, QualifiedClassNameResolver> INSTANCE = new MethodParameterFinalModifier();

    private MethodParameterFinalModifier() {
    }

    @Override
    public QualifiedClassNameResolver visitImport(ImportTree importTree, QualifiedClassNameResolver resolver) {
        resolver.importClass(QualifiedImport.of(QualifiedName.parse(importTree.getQualifiedIdentifier().toString())));
        return super.visitImport(importTree, resolver);
    }

    @Override
    public QualifiedClassNameResolver visitMethod(MethodTree method, QualifiedClassNameResolver resolver) {
        method.getParameters()
            .stream()
            .filter(parameter ->
                parameter.getModifiers()
                    .getAnnotations()
                    .stream()
                    .map(annotation -> QualifiedName.parse(annotation.getAnnotationType().toString()))
                    .map(resolver::resolve)
                    .noneMatch(annotationName -> {
                        QualifiedClassName mutableAnnotation = QualifiedClassName.of(QualifiedName.parse(Mutable.class.getName()));
                        return mutableAnnotation.equals(annotationName);
                    })
            )
            .forEach(parameter -> {
                JCTree.JCVariableDecl declaration = (JCTree.JCVariableDecl) parameter;
                declaration.mods.flags |= Flags.FINAL;
            });
        return super.visitMethod(method, resolver);
    }
}
