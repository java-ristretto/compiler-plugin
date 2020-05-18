package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

class JavacPluginTest {

    private TestCompiler compiler;

    @BeforeEach
    void beforeEach() {
        compiler = new TestCompiler();
    }

    @Test
    void indicates_when_it_is_loaded() {
        var anEmptyClass = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
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
            "}"
        );

        var result = compiler.compile(anEmptyClass);

        assertThat(result.additionalOutput, containsString("ristretto plugin loaded"));
        assertThat(result.additionalOutput, containsString("/test/TestSample.java:7 variable field2 has unnecessary final modifier"));
        assertThat(result.additionalOutput, containsString("immutable by default summary:"));
        assertThat(result.additionalOutput, containsString("| var type  | inspected   | final   | skipped | annotated |"));
        assertThat(result.additionalOutput, containsString("|-----------|-------------|---------|---------|-----------|"));
        assertThat(result.additionalOutput, containsString("| field     |           2 |  50.00% |  50.00% |     0.00% |"));
        assertThat(result.additionalOutput, containsString("| local     |           1 | 100.00% |   0.00% |     0.00% |"));
        assertThat(result.additionalOutput, containsString("| parameter |           1 | 100.00% |   0.00% |     0.00% |"));
    }

    @Test
    void ignores_specified_package() {
        var anEmptyClass = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
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

        var result = compiler
            .compile(anEmptyClass, "--ignore-packages=ristretto.test")
            .loadClass("ristretto.test.TestSample")
            .invoke("test", "value");

        assertThat(result, is("value:value"));
    }
}
