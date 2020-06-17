package ristretto.compiler.plugin;

interface ClassReference {

  static ClassReference parse(String classReference) {
    int separatorIndex = classReference.lastIndexOf('.');
    if (separatorIndex == -1) {
      return new SimpleName(classReference);
    }

    String packageName = classReference.substring(0, separatorIndex);
    String simpleName = classReference.substring(separatorIndex + 1);

    return new QualifiedName(new PackageName(packageName), new SimpleName(simpleName));
  }
}
