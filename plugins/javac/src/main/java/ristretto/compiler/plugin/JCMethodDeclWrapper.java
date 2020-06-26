package ristretto.compiler.plugin;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.MethodTree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.DiagnosticSource;

import javax.lang.model.element.Modifier;
import javax.tools.JavaFileObject;

final class JCMethodDeclWrapper implements ModifierTarget {

  private final JCTree.JCMethodDecl method;
  private final AnnotationNameResolver resolver;
  private final JavaFileObject javaFile;

  JCMethodDeclWrapper(JavaFileObject javaFile, MethodTree method, AnnotationNameResolver resolver) {
    this.javaFile = javaFile;
    this.method = (JCTree.JCMethodDecl) method;
    this.resolver = resolver;
  }

  @Override
  public String position() {
    String filePath = javaFile.toUri().getPath();
    int lineNumber = new DiagnosticSource(javaFile, null).getLineNumber(method.getPreferredPosition());
    return filePath + ":" + lineNumber;
  }

  @Override
  public boolean hasFinalModifier() {
    return method.getModifiers().getFlags().contains(Modifier.FINAL);
  }

  @Override
  public boolean hasPublicModifier() {
    return method.getModifiers().getFlags().contains(Modifier.PUBLIC);
  }

  @Override
  public boolean hasProtectedModifier() {
    return method.getModifiers().getFlags().contains(Modifier.PROTECTED);
  }

  @Override
  public boolean hasPrivateModifier() {
    return method.getModifiers().getFlags().contains(Modifier.PRIVATE);
  }

  @Override
  public boolean hasStaticModifier() {
    return method.getModifiers().getFlags().contains(Modifier.STATIC);
  }

  @Override
  public boolean hasMutableAnnotation() {
    return method.getModifiers()
      .getAnnotations()
      .stream()
      .map(AnnotationTree::getAnnotationType)
      .map(Object::toString)
      .anyMatch(resolver::isMutable);
  }

  @Override
  public boolean hasPackagePrivateAnnotation() {
    return method.getModifiers()
      .getAnnotations()
      .stream()
      .map(AnnotationTree::getAnnotationType)
      .map(Object::toString)
      .anyMatch(resolver::isPackagePrivate);
  }

  @Override
  public void addFinalModifier() {
    JCTree.JCModifiers modifiers = method.mods;
    if ((modifiers.flags & Flags.VOLATILE) != 0) {
      return;
    }
    modifiers.flags |= Flags.FINAL;
  }

  @Override
  public void addPrivateModifier() {
    JCTree.JCModifiers modifiers = method.mods;
    modifiers.flags |= Flags.PRIVATE;
  }

  @Override
  public void addPublicModifier() {
    JCTree.JCModifiers modifiers = method.mods;
    modifiers.flags |= Flags.PUBLIC;
  }
}
