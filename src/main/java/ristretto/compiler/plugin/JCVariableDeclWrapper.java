package ristretto.compiler.plugin;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.DiagnosticSource;

import javax.lang.model.element.Modifier;
import javax.tools.JavaFileObject;

final class JCVariableDeclWrapper implements LocalVariable {

    private final JCTree.JCVariableDecl variable;
    private final AnnotationNameResolver resolver;
    private final JavaFileObject javaFile;

    JCVariableDeclWrapper(JavaFileObject javaFile, VariableTree variable, AnnotationNameResolver resolver) {
        this.javaFile = javaFile;
        this.variable = (JCTree.JCVariableDecl) variable;
        this.resolver = resolver;
    }

    @Override
    public boolean isAnnotatedAsMutable() {
        return variable.getModifiers()
            .getAnnotations()
            .stream()
            .map(AnnotationTree::getAnnotationType)
            .map(Object::toString)
            .anyMatch(resolver::isMutable);
    }

    @Override
    public boolean hasFinalModifier() {
        return variable.getModifiers().getFlags().contains(Modifier.FINAL);
    }

    @Override
    public void addFinalModifier() {
        JCTree.JCModifiers modifiers = variable.mods;
        if ((modifiers.flags & Flags.VOLATILE) != 0) {
            return;
        }
        modifiers.flags |= Flags.FINAL;
    }

    @Override
    public String position() {
        String filePath = javaFile.toUri().getPath();
        int lineNumber = new DiagnosticSource(javaFile, null).getLineNumber(variable.getPreferredPosition());
        return filePath + ":" + lineNumber;
    }
}
