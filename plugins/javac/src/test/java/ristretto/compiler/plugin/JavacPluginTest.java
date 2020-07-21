package ristretto.compiler.plugin;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JavacPluginTest extends JavacPluginBaseTest {

  @Test
  void ignores_specified_package() {
    var code = TestCompiler.SourceCode.of(
      "package ristretto.test;",
      "",
      "public class TestSample {",
      "",
      "  public static String test(String parameter) {",
      "    parameter += \":value\";",
      "    return parameter;",
      "  }",
      "",
      "}"
    );

    var result = compile(code, "--ignore-packages=ristretto.test").invoke("ristretto.test.TestSample", "test", "value");

    assertThat(result, is("value:value"));
  }

  @Test
  void does_not_allow_unsupported_spec() {
    var code = TestCompiler.SourceCode.of(
      "package ristretto.test;",
      "",
      "public class TestSample {",
      "",
      "  public static String test(String parameter) {",
      "    parameter += \":value\";",
      "    return parameter;",
      "  }",
      "",
      "}"
    );

    var file = new File(RistrettoOptionsTest.class.getResource("/ristretto.properties").getFile());

    var exception = assertThrows(IllegalArgumentException.class, () -> compile(code, "--config-file=" + file.getAbsolutePath()));

    assertThat(exception.getMessage(), is("No enum constant ristretto.spec.Spec.UNSUPPORTED_VERSION"));
  }
}
