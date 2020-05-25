package ristretto.compiler.plugin;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

class JavacPluginTest extends JavacPluginBaseTest {

    @Test
    void indicates_when_it_is_loaded() {
        var code = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "import ristretto.PackagePrivate;",
            "",
            "public class TestSample {",
            "",
            "  String field1;",
            "  final String field2;",
            "",
            "  public void sampleMethod(String parameter) {",
            "    String s = parameter;",
            "  }",
            "",
            "  @PackagePrivate void anotherMethod() {",
            "  }",
            "",
            "}"
        );

        var result = compile(code);

        assertThat(result.additionalOutput, containsString("ristretto plugin loaded"));

        assertThat(result.additionalOutput, containsString("/test/TestSample.java:9 variable field2 has unnecessary final modifier"));

        assertThat(result.additionalOutput, containsString("immutable by default summary:"));
        assertThat(result.additionalOutput, containsString("| var type  | inspected   | final   | skipped | annotated |"));
        assertThat(result.additionalOutput, containsString("|-----------|-------------|---------|---------|-----------|"));
        assertThat(result.additionalOutput, containsString("| field     |           2 |  50.00% |  50.00% |     0.00% |"));
        assertThat(result.additionalOutput, containsString("| local     |           1 | 100.00% |   0.00% |     0.00% |"));
        assertThat(result.additionalOutput, containsString("| parameter |           1 | 100.00% |   0.00% |     0.00% |"));

        assertThat(result.additionalOutput, containsString("default field access rule summary:"));
        assertThat(result.additionalOutput, containsString("| member    | inspected   | marked  | skipped | annotated |"));
        assertThat(result.additionalOutput, containsString("|-----------|-------------|---------|---------|-----------|"));
        assertThat(result.additionalOutput, containsString("| DefaultFieldAccessRule |           2 | 100.00% |   0.00% |     0.00% |"));
    }

    @Test
    void ignores_specified_package() {
        var code = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
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
}
