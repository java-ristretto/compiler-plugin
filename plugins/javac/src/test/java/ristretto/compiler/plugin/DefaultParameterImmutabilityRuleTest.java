package ristretto.compiler.plugin;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class DefaultParameterImmutabilityRuleTest extends JavacPluginBaseTest {

  @Test
  void enforces_method_parameters_to_be_final() {
    var code = TestCompiler.SourceCode.of(
      "package ristretto.test;",
      "",
      "public class TestSample {",
      "  public static String hello(String name) {",
      "    name = \"hello \" + name;",
      "    return name;",
      "  }",
      "}"
    );

    var result = compile(code);

    assertThat(result.diagnostics(), containsString("TestSample.java:5: error: final parameter name may not be assigned"));
  }

  @Test
  void skips_abstract_method_parameters_in_interfaces() {
    var code = TestCompiler.SourceCode.of(
      "package ristretto.test;",
      "",
      "public class TestSample {",
      "  ",
      "  interface SomeInterface {",
      "    String hello(String name);",
      "  }",
      "  ",
      "}"
    );

    var result = compile(code);

    assertThat(result.additionalOutput, not(containsString("DefaultParameterImmutabilityRule /test/TestSample.java:6 MODIFIER_ADDED")));
  }

  @Test
  void skips_abstract_method_parameters_in_abstract_classes() {
    var code = TestCompiler.SourceCode.of(
      "package ristretto.test;",
      "",
      "public class TestSample {",
      "  ",
      "  abstract class SomeInterface {",
      "    abstract String hello(String name);",
      "  }",
      "  ",
      "}"
    );

    var result = compile(code);

    assertThat(result.additionalOutput, not(containsString("DefaultParameterImmutabilityRule /test/TestSample.java:6 MODIFIER_ADDED")));
  }

  @Nested
  class when_parameter_is_annotated_with_full_qualified_mutable {

    @Test
    void skips_parameter() {
      var code = TestCompiler.SourceCode.of(
        "package ristretto.test;",
        "",
        "public class TestSample {",
        "  public static String hello(@ristretto.Mutable String name) {",
        "    name = \"hello \" + name;",
        "    return name;",
        "  }",
        "}"
      );

      String result = compile(code).invoke("ristretto.test.TestSample", "hello", "world");

      assertThat(result, is("hello world"));
    }
  }

  @Nested
  class when_parameter_is_annotated_with_full_qualified_mutable_and_there_is_an_import_statement {

    @Test
    void skips_parameter() {
      var code = TestCompiler.SourceCode.of(
        "package ristretto.test;",
        "",
        "import ristretto.Mutable;",
        "",
        "public class TestSample {",
        "  public static String hello(@ristretto.Mutable String name) {",
        "    name = \"hello \" + name;",
        "    return name;",
        "  }",
        "}"
      );

      String result = compile(code).invoke("ristretto.test.TestSample", "hello", "world");

      assertThat(result, is("hello world"));
    }
  }

  @Nested
  class when_parameter_is_annotated_with_mutable {

    @Test
    void skips_parameter() {
      var code = TestCompiler.SourceCode.of(
        "package ristretto.test;",
        "",
        "import ristretto.Mutable;",
        "",
        "public class TestSample {",
        "  public static String hello(@Mutable String name) {",
        "    name = \"hello \" + name;",
        "    return name;",
        "  }",
        "}"
      );

      String result = compile(code).invoke("ristretto.test.TestSample", "hello", "world");

      assertThat(result, is("hello world"));
    }
  }

  @Nested
  class when_parameter_is_annotated_with_mutable_and_there_is_an_import_star {

    @Test
    void skips_parameter() {
      var code = TestCompiler.SourceCode.of(
        "package ristretto.test;",
        "",
        "import ristretto.*;",
        "",
        "public class TestSample {",
        "  public static String hello(@Mutable String name) {",
        "    name = \"hello \" + name;",
        "    return name;",
        "  }",
        "}"
      );

      String result = compile(code).invoke("ristretto.test.TestSample", "hello", "world");

      assertThat(result, is("hello world"));
    }
  }
}
