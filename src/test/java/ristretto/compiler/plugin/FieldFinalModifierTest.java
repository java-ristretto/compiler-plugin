package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

class FieldFinalModifierTest {

    private TestCompiler compiler;

    @BeforeEach
    void beforeEach() {
        compiler = TestCompiler.newInstance();
    }

    @Test
    void enforces_fields_to_be_final() {
        var code = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "public class TestSample {",
            "  ",
            "  String msg;",
            "  ",
            "  public static String hello(String value) {",
            "    TestSample sample = new TestSample();",
            "    sample.msg = value;",
            "    return sample.getMsg();",
            "  }",
            "  ",
            "  String getMsg() {",
            "    return msg;",
            "  }",
            "}"
        );

        TestCompiler.Result result = compiler
            .compile(code);

        assertThat(result.diagnostics(), containsString("TestSample.java:10: error: cannot assign a value to final variable msg"));
    }

    @Test
    void skips_annotated_fields() {
        var code = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "import ristretto.Mutable;",
            "",
            "public class TestSample {",
            "  ",
            "  @Mutable",
            "  String msg;",
            "  ",
            "  public static String hello(String value) {",
            "    TestSample sample = new TestSample();",
            "    sample.msg = value;",
            "    return sample.getMsg();",
            "  }",
            "  ",
            "  String getMsg() {",
            "    return msg;",
            "  }",
            "}"
        );

        var result = compiler
            .compile(code)
            .loadClass("ristretto.test.TestSample")
            .invoke("hello", "hello world");

        assertThat(result, is("hello world"));
    }
}
