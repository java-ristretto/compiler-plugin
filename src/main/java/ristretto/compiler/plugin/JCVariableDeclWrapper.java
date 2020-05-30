package ristretto.compiler.plugin;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.DiagnosticSource;

import javax.lang.model.element.Modifier;
import javax.tools.JavaFileObject;

final class JCVariableDeclWrapper implements Variable {

    private final JCTree.JCVariableDecl variable;
    private final AnnotationNameResolver resolver;
    private final JavaFileObject javaFile;

    JCVariableDeclWrapper(JavaFileObject javaFile, VariableTree variable, AnnotationNameResolver resolver) {
        this.javaFile = javaFile;
        this.variable = (JCTree.JCVariableDecl) variable;
        this.resolver = resolver;
    }

    @Override
    public String position() {
        String filePath = javaFile.toUri().getPath();
        int lineNumber = new DiagnosticSource(javaFile, null).getLineNumber(variable.getPreferredPosition());
        return filePath + ":" + lineNumber;
    }

    @Override
    public boolean hasFinalModifier() {
        return variable.getModifiers().getFlags().contains(Modifier.FINAL);
    }

    @Override
    public boolean hasPublicModifier() {
        return variable.getModifiers().getFlags().contains(Modifier.PUBLIC);
    }

    @Override
    public boolean hasProtectedModifier() {
        return variable.getModifiers().getFlags().contains(Modifier.PROTECTED);
    }

    @Override
    public boolean hasPrivateModifier() {
        return variable.getModifiers().getFlags().contains(Modifier.PRIVATE);
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
    public boolean hasPackagePrivateAnnotation() {
        return variable.getModifiers()
            .getAnnotations()
            .stream()
            .map(AnnotationTree::getAnnotationType)
            .map(Object::toString)
            .anyMatch(resolver::isPackagePrivate);
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
    public void addPublicModifier() {
        JCTree.JCModifiers modifiers = variable.mods;
        modifiers.flags |= Flags.PRIVATE;
    }

}
