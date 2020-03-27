package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JavacPluginTest {

    private TestCompiler compiler;

    @BeforeEach
    void beforeEach() {
        compiler = TestCompiler.newInstance();
    }

    @Test
    void indicates_when_it_is_loaded() {
        var anEmptyClass = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "" +
            "package ristretto.test;" +
            "" +
            "public class TestSample {}"
        );

        var result = compiler.compile(anEmptyClass);

        assertThat(result.additionalOutput, containsString("ristretto plugin loaded"));
    }

    @Test
    void enforces_method_parameters_to_be_final() {
        var mutableParameter = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "" +
            "package ristretto.test;" +
            "" +
            "public class TestSample {" +
            "  public static String hello(String name) {" +
            "    name = \"hello \" + name;" +
            "    return name;" +
            "  }" +
            "}"
        );

        var result = compiler.compile(mutableParameter);

        assertThat(result.diagnostics(), containsString("TestSample.java:1: error: final parameter name may not be assigned"));
    }

    @Test
    void enforces_non_private_method_parameters_to_not_be_null() {
        var singleParameterMethod = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "" +
            "package ristretto.test;" +
            "" +
            "public class TestSample {" +
            "  public static String hello(String name) {" +
            "    if (name == null) throw new NullPointerException(\"name is null\");" +
            "    return \"hello \" + name;" +
            "  }" +
            "}"
        );

        var exception = assertThrows(
            NullPointerException.class,
            () ->
                compiler
                    .compile(singleParameterMethod)
                    .loadClass("ristretto.test.TestSample")
                    .invoke("hello", null)
        );

        assertThat(exception.getMessage(), is("name is null"));
    }
}
