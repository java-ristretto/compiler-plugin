package ristretto.compiler.plugin;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RistrettoOptionsTest {

  @Test
  void indicates_when_standard_error_output_is_enabled() {
    RistrettoOptions options = RistrettoOptions.parse("--output=stderr");

    assertThat(options.isStandardErrorOutputEnabled(), is(true));
  }

  @Test
  void indicates_when_standard_error_output_is_disabled() {
    RistrettoOptions options = RistrettoOptions.parse();

    assertThat(options.isStandardErrorOutputEnabled(), is(false));
  }

  @Test
  void indicates_when_packages_are_included() {
    RistrettoOptions options = RistrettoOptions.parse("--ignore-packages=some.package.name1,some.package.name2");

    assertThat(options.isIncluded(new PackageName("some.package.name1")), is(false));
    assertThat(options.isIncluded(new PackageName("some.package.name2")), is(false));
    assertThat(options.isIncluded(new PackageName("some.package.name3")), is(true));
  }

  @Test
  void has_the_configuration_file_path() {
    RistrettoOptions options = RistrettoOptions.parse("--config-file=./ristretto.properties");

    assertThat(options.configFile(), is(Optional.of(new File("./ristretto.properties"))));
  }
}