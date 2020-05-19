package ristretto.compiler.plugin;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

class ImmutableLocalVariableTest extends JavacPluginBaseTest {

    @Test
    void enforces_local_variable_to_be_final() {
        var code = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "public class TestSample {",
            "  public static String hello() {",
            "    String name = \"world\";",
            "    name = \"hello \" + name;",
            "    return name;",
            "  }",
            "}"
        );

        var result = compile(code);

        assertThat(result.diagnostics(), containsString("TestSample.java:7: error: cannot assign a value to final variable name"));
    }

    @Test
    void skips_annotated_local_variable_annotated() {
        var code = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "import ristretto.Mutable;",
            "",
            "public class TestSample {",
            "  public static String hello(String name) {",
            "    @Mutable String result = name;",
            "    result = \"hello \" + result;",
            "    return result;",
            "  }",
            "}"
        );

        var result = compile(code).invoke("ristretto.test.TestSample", "hello", "world");

        assertThat(result, is("hello world"));
    }

    @Test
    void enforces_local_variables_in_anonymous_blocks() {
        var code = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "public class TestSample {",
            "  ",
            "  {",
            "    String msg = \"hello\";",
            "    msg += \" world\";",
            "  }",
            "  ",
            "  public static void hello() {",
            "    TestSample sample = new TestSample();",
            "  }",
            "}"
        );

        var result = compile(code);

        assertThat(result.diagnostics(), containsString("TestSample.java:8: error: cannot assign a value to final variable msg"));
    }

    @Nested
    class for_loops {

        @Test
        void skips_variable() {
            var code = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
                "package ristretto.test;",
                "",
                "public class TestSample {",
                "  public static String reverse(String s) {",
                "    StringBuilder builder = new StringBuilder();",
                "    for (int i = s.length() - 1; i >= 0; i--) {",
                "      builder.append(s.charAt(i));",
                "    }",
                "    return builder.toString();",
                "  }",
                "}"
            );

            var result = compile(code).invoke("ristretto.test.TestSample", "reverse", "hello world");

            assertThat(result, is("dlrow olleh"));
        }

        @Test
        void enforces_local_variable_initialized_but_not_defined() {
            var code = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
                "package ristretto.test;",
                "",
                "public class TestSample {",
                "  public static void hello(int p) {",
                "    int i;",
                "    for (i = p; i > 10; i++);",
                "  }",
                "}"
            );

            var result = compile(code);

            assertThat(result.diagnostics(), containsString("TestSample.java:7: error: variable i might already have been assigned"));
        }

        @Test
        void enforces_local_variable_defined_in_the_block() {
            var code = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
                "package ristretto.test;",
                "",
                "public class TestSample {",
                "  public static void hello() {",
                "    for (int i = 0; i > 2; i++) {",
                "      String name = \"world\";",
                "      name = \"hello \" + name;",
                "    }",
                "  }",
                "}"
            );

            var result = compile(code);

            assertThat(result.diagnostics(), containsString("TestSample.java:8: error: cannot assign a value to final variable name"));
        }
    }

    @Nested
    class enhanced_for_loops {

        @Test
        void skips_variable() {
            var code = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
                "package ristretto.test;",
                "",
                "public class TestSample {",
                "  public static String reverse(String s) {",
                "    StringBuilder builder = new StringBuilder();",
                "    for (char c : s.toCharArray()) {",
                "      builder.append(c);",
                "    }",
                "    return builder.reverse().toString();",
                "  }",
                "}"
            );

            var result = compile(code).invoke("ristretto.test.TestSample", "reverse", "hello world");

            assertThat(result, is("dlrow olleh"));
        }

        @Test
        void enforces_local_variable_defined_in_the_block() {
            var code = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
                "package ristretto.test;",
                "",
                "public class TestSample {",
                "  public static void hello(String s) {",
                "    for (char c : s.toCharArray()) {",
                "      String name = \"world\";",
                "      name = c + name;",
                "    }",
                "  }",
                "}"
            );

            var result = compile(code);

            assertThat(result.diagnostics(), containsString("TestSample.java:8: error: cannot assign a value to final variable name"));
        }
    }
}
