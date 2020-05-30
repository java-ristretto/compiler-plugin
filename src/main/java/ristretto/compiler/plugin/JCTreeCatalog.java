package ristretto.compiler.plugin;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;

import javax.lang.model.element.Modifier;

final class JCTreeCatalog {

    private JCTreeCatalog() {
    }

    static boolean hasFinalModifier(VariableTree variable) {
        return variable.getModifiers().getFlags().contains(Modifier.FINAL);
    }

    static boolean hasStaticModifier(VariableTree variable) {
        return variable.getModifiers().getFlags().contains(Modifier.STATIC);
    }

    static void addFinalModifier(VariableTree parameter) {
        JCTree.JCModifiers modifiers = ((JCTree.JCVariableDecl) parameter).mods;
        if ((modifiers.flags & Flags.VOLATILE) != 0) {
            return;
        }
        modifiers.flags |= Flags.FINAL;
    }

    static boolean isAnnotatedAsMutable(VariableTree variable, AnnotationNameResolver resolver) {
        return variable.getModifiers()
            .getAnnotations()
            .stream()
            .map(AnnotationTree::getAnnotationType)
            .map(Object::toString)
            .anyMatch(resolver::isMutable);
    }

}
