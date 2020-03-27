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
        var anEmptyClass = "" +
            "package ristretto.test;" +
            "" +
            "public class TestSample {}";

        var result = compiler.compile(anEmptyClass);

        assertThat(result.additionalOutput, containsString("ristretto plugin loaded"));
    }

    @Test
    void enforces_method_parameters_to_be_final() {
        var mutableParameter = "" +
            "package ristretto.test;" +
            "" +
            "public class TestSample {" +
            "  public static String hello(String name) {" +
            "    name = \"hello \" + name;" +
            "    return name;" +
            "  }" +
            "}";

        var result = compiler.compile(mutableParameter);

        assertThat(result.diagnostics(), containsString("TestSample.java:1: error: final parameter name may not be assigned"));
    }
}
