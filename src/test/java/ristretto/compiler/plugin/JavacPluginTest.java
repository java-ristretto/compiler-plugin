package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
        var anEmptyClass = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "public class TestSample {}"
        );

        var result = compiler.compile(anEmptyClass);

        assertThat(result.additionalOutput, containsString("ristretto plugin loaded"));
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

    @Nested
    class null_check_for_public_method_parameters {

        TestCompiler.ClassWrapper aClass;

        @BeforeEach
        void beforeEach() {
            var sourceCode = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
                "package ristretto.test;",
                "",
                "public class TestSample {",
                "  public static String hello(String name) {",
                "    return \"hello \" + name;",
                "  }",
                "}"
            );

            aClass = compiler.compile(sourceCode).loadClass("ristretto.test.TestSample");
        }

        @Test
        void enforces_non_null_values() {
            var exception = assertThrows(NullPointerException.class, () -> aClass.invoke("hello", null));

            assertThat(exception.getMessage(), is("name is null"));
        }

        @Test
        void points_to_the_parameter_declaration_line() {
            var exception = assertThrows(NullPointerException.class, () -> aClass.invoke("hello", null));

            StackTraceElement[] stackTrace = exception.getStackTrace();

            assertThat(stackTrace[0].getLineNumber(), is(5));
        }

        @Test
        void keeps_original_behavior() {
            String result = aClass.invoke("hello", "world");

            assertThat(result, is("hello world"));
        }

        @Test
        void skips_primitive_types() {
            var sourceCode = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
                "package ristretto.test;",
                "",
                "public class TestSample {",
                "  public static int addOne(int value) {",
                "    return value + 1;",
                "  }",
                "}"
            );

            int result = compiler
                .compile(sourceCode)
                .loadClass("ristretto.test.TestSample")
                .invoke("addOne", 0);

            assertThat(result, is(1));
        }
    }
}
