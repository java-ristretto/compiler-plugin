package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NullCheckForPublicMethodParameterTest {

    private TestCompiler compiler;
    private TestCompiler.ClassWrapper aClass;

    @BeforeEach
    void beforeEach() {
        compiler = TestCompiler.newInstance();

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

    @Test
    void skips_annotated_parameter() {
        var sourceCode = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "import ristretto.Nullable;",
            "",
            "public class TestSample {",
            "  public static String hello(@Nullable String name) {",
            "    return \"hello \" + name;",
            "  }",
            "}"
        );

        String result = compiler
            .compile(sourceCode)
            .loadClass("ristretto.test.TestSample")
            .invoke("hello", null);

        assertThat(result, is("hello null"));
    }

    @Test
    void skips_abstract_method() {
        var sourceCode = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "public class TestSample {",
            "  ",
            "  static abstract class Base {",
            "    abstract String hello(String name);",
            "  }",
            "  ",
            "  static class Sample extends Base {",
            "    String hello(String name) {",
            "      return \"hello \" + name;",
            "    }",
            "  }",
            "  ",
            "  public static String hello(String name) {",
            "      return new Sample().hello(name);",
            "  }",
            "}"
        );

        String result = compiler
            .compile(sourceCode)
            .loadClass("ristretto.test.TestSample")
            .invoke("hello", "world");

        assertThat(result, is("hello world"));
    }
}
