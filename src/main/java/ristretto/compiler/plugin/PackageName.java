package ristretto.compiler.plugin;

import com.sun.source.tree.CompilationUnitTree;

final class PackageName extends StringTypeAlias {

  PackageName(String name) {
    super(name);
  }

  PackageName(Class<?> aClass) {
    this(aClass.getPackageName());
  }

  PackageName(CompilationUnitTree compilationUnit) {
    this(compilationUnit.getPackageName().toString());
  }

  QualifiedName qualify(SimpleName simpleName) {
    return new QualifiedName(this, simpleName);
  }
}
