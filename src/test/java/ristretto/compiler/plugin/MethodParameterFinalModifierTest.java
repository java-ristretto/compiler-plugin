package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

class MethodParameterFinalModifierTest {

    private TestCompiler compiler;

    @BeforeEach
    void beforeEach() {
        compiler = TestCompiler.newInstance();
    }

    @Test
    void enforces_method_parameters_to_be_final() {
        var mutableParameter = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "public class TestSample {",
            "  public static String hello(String name) {",
            "    name = \"hello \" + name;",
            "    return name;",
            "  }",
            "}"
        );

        var result = compiler.compile(mutableParameter);

        assertThat(result.diagnostics(), containsString("TestSample.java:6: error: final parameter name may not be assigned"));
    }
}
