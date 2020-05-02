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
            "  public void sampleMethod(String parameter) {",
            "    String s = parameter;",
            "  }",
            "",
            "}"
        );

        var result = compiler.compile(anEmptyClass);

        assertThat(result.additionalOutput, containsString("ristretto plugin loaded"));
        assertThat(result.additionalOutput, containsString("1 parameter(s) inspected (100.00% marked as final | 0.00% skipped)"));
        assertThat(result.additionalOutput, containsString("1 local variable(s) inspected (100.00% marked as final | 0.00% skipped)"));
    }
}
