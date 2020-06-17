package ristretto.compiler.plugin;

import com.sun.source.tree.ImportTree;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

final class ImportDeclaration {

  private final PackageName packageName;
  private final Optional<SimpleName> simpleName;

  private ImportDeclaration(PackageName packageName, Optional<SimpleName> simpleName) {
    this.packageName = packageName;
    this.simpleName = simpleName;
  }

  static Set<ImportDeclaration> of(List<? extends ImportTree> imports) {
    return imports.stream()
      .map(ImportTree::getQualifiedIdentifier)
      .map(Object::toString)
      .map(ImportDeclaration::parse)
      .collect(Collectors.toUnmodifiableSet());
  }

  static ImportDeclaration parse(String declaration) {
    int separatorIndex = declaration.lastIndexOf('.');
    if (separatorIndex == -1) {
      throw new IllegalArgumentException(String.format("illegal import declaration: '%s'", declaration));
    }

    String packageName = declaration.substring(0, separatorIndex);
    String simpleName = declaration.substring(separatorIndex + 1);

    if ("*".equals(simpleName)) {
      return new ImportDeclaration(new PackageName(packageName), Optional.empty());
    }
    return new ImportDeclaration(new PackageName(packageName), Optional.of(new SimpleName(simpleName)));
  }

  PackageName packageName() {
    return packageName;
  }

  Optional<QualifiedName> qualifiedName() {
    return simpleName.map(packageName::qualify);
  }
}
