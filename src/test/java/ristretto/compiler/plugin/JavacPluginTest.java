package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

class JavacPluginTest {

    private TestCompiler compiler;

    @BeforeEach
    void beforeEach() {
        compiler = TestCompiler.newInstance();
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
        assertThat(result.additionalOutput, containsString("immutable by default summary:"));
        assertThat(result.additionalOutput, containsString("| var type  | inspected   | final   | skipped | annotated |"));
        assertThat(result.additionalOutput, containsString("|-----------|-------------|---------|---------|-----------|"));
        assertThat(result.additionalOutput, containsString("| field     |           2 |  50.00% |  50.00% |     0.00% |"));
        assertThat(result.additionalOutput, containsString("| local     |           1 | 100.00% |   0.00% |     0.00% |"));
        assertThat(result.additionalOutput, containsString("| parameter |           1 | 100.00% |   0.00% |     0.00% |"));
    }
}
