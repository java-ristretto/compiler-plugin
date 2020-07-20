package ristretto.compiler.plugin;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

final class RistrettoOptions {

  private boolean standardErrorOutputEnabled = false;
  private final Set<PackageName> packagesToIgnore = new HashSet<>();
  private Optional<File> configFile = Optional.empty();

  private RistrettoOptions() {
  }

  static RistrettoOptions parse(String... args) {
    var options = new RistrettoOptions();
    for (var arg : args) {
      if ("--output=stderr".equals(arg)) {
        options.standardErrorOutputEnabled = true;
        continue;
      }

      if (arg.startsWith("--ignore-packages=")) {
        String[] keyValue = arg.split("=");
        String[] packages = keyValue[1].split(",");
        Stream.of(packages)
          .map(PackageName::new)
          .forEach(options.packagesToIgnore::add);
        continue;
      }

      if (arg.startsWith("--config-file=")) {
        String[] keyValue = arg.split("=");
        options.configFile = Optional.of(new File(keyValue[1]));
      }
    }
    return options;
  }

  boolean isStandardErrorOutputEnabled() {
    return standardErrorOutputEnabled;
  }

  boolean isIncluded(PackageName packageName) {
    return !packagesToIgnore.contains(packageName);
  }

  Optional<File> configFile() {
    return configFile;
  }
}
