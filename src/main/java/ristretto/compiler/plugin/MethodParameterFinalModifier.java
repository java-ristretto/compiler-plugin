package ristretto.compiler.plugin;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

final class MethodParameterFinalModifier extends TreeScanner<MethodParameterFinalModifier.Context, MethodParameterFinalModifier.Context> {

    static final TreeScanner<Context, Context> INSTANCE = new MethodParameterFinalModifier();

    private MethodParameterFinalModifier() {
    }

    @Override
    public Context visitImport(ImportTree importTree, Context context) {
        context.resolver.importClass(importTree.getQualifiedIdentifier().toString());
        return super.visitImport(importTree, context);
    }

    @Override
    public Context visitMethod(MethodTree method, Context context) {
        method.getParameters()
            .stream()
            .filter(parameter -> noMutableAnnotation(context, parameter))
            .forEach(JCTreeCatalog::addFinalModifier);
        return super.visitMethod(method, context);
    }

    private boolean noMutableAnnotation(Context context, VariableTree parameter) {
        if (noMutableAnnotation(context.resolver, parameter)) {
            context.observer.parameterMarkedAsFinal();
            return true;
        }

        context.observer.parameterSkipped();
        return false;
    }

    private boolean noMutableAnnotation(AnnotationNameResolver resolver, VariableTree parameter) {
        return parameter.getModifiers()
            .getAnnotations()
            .stream()
            .map(AnnotationTree::getAnnotationType)
            .map(Object::toString)
            .noneMatch(resolver::isMutable);
    }

    public static final class Context {

        private final AnnotationNameResolver resolver;
        private final Observer observer;

        private Context(AnnotationNameResolver resolver, Observer observer) {
            this.resolver = resolver;
            this.observer = observer;
        }

        static Context of(Observer observer) {
            return new Context(AnnotationNameResolver.newResolver(), observer);
        }
    }

    public interface Observer {
        void parameterMarkedAsFinal();
        void parameterSkipped();
    }

}
