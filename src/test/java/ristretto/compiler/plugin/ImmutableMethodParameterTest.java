package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

class ImmutableMethodParameterTest {

    private TestCompiler compiler;

    @BeforeEach
    void beforeEach() {
        compiler = new TestCompiler();
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
    class when_parameter_is_annotated_with_full_qualified_mutable {

        @Test
        void skips_parameter() {
            var sourceCode = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
                "package ristretto.test;",
                "",
                "public class TestSample {",
                "  public static String hello(@ristretto.Mutable String name) {",
                "    name = \"hello \" + name;",
                "    return name;",
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

    @Nested
    class when_parameter_is_annotated_with_full_qualified_mutable_and_there_is_an_import_statement {

        @Test
        void skips_parameter() {
            var sourceCode = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
                "package ristretto.test;",
                "",
                "import ristretto.Mutable;",
                "",
                "public class TestSample {",
                "  public static String hello(@ristretto.Mutable String name) {",
                "    name = \"hello \" + name;",
                "    return name;",
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

    @Nested
    class when_parameter_is_annotated_with_mutable {

        @Test
        void skips_parameter() {
            var sourceCode = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
                "package ristretto.test;",
                "",
                "import ristretto.Mutable;",
                "",
                "public class TestSample {",
                "  public static String hello(@Mutable String name) {",
                "    name = \"hello \" + name;",
                "    return name;",
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

    @Nested
    class when_parameter_is_annotated_with_mutable_and_there_is_an_import_star {

        @Test
        void skips_parameter() {
            var sourceCode = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
                "package ristretto.test;",
                "",
                "import ristretto.*;",
                "",
                "public class TestSample {",
                "  public static String hello(@Mutable String name) {",
                "    name = \"hello \" + name;",
                "    return name;",
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
}
