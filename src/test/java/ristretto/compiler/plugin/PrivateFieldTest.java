package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

class PrivateFieldTest extends JavacPluginBaseTest {

    TestCompiler.SourceCode anotherClass;

    @BeforeEach
    void beforeEach() {
        anotherClass = TestCompiler.SourceCode.of("ristretto.test", "AnotherClass", "",
            "package ristretto.test;",
            "",
            "public class AnotherClass {",
            "  ",
            "  public static String test(String value) {",
            "    TestSample sample = new TestSample(value);",
            "    return sample.msg;",
            "  }",
            "  ",
            "}"
        );
    }

    @Test
    void sets_field_as_private_when_no_access_modifier_is_present() {
        var classWithField = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "public class TestSample {",
            "  ",
            "  String msg;",
            "  ",
            "  public TestSample(String msg) {",
            "    this.msg = msg;",
            "  }",
            "  ",
            "}"
        );

        TestCompiler.Result result = compile(List.of(classWithField, anotherClass));

        assertThat(result.diagnostics(), containsString("AnotherClass.java:8: error: msg has private access in ristretto.test.TestSample"));
    }

    @Test
    void skips_public_fields() {
        var classWithField = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "public class TestSample {",
            "  ",
            "  public String msg;",
            "  ",
            "  public TestSample(String msg) {",
            "    this.msg = msg;",
            "  }",
            "  ",
            "}"
        );

        String result = compile(List.of(classWithField, anotherClass)).invoke("ristretto.test.AnotherClass", "test", "hello world");

        assertThat(result, is("hello world"));
    }

    @Test
    void skips_protected_fields() {
        var classWithField = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "public class TestSample {",
            "  ",
            "  protected String msg;",
            "  ",
            "  public TestSample(String msg) {",
            "    this.msg = msg;",
            "  }",
            "  ",
            "}"
        );

        String result = compile(List.of(classWithField, anotherClass)).invoke("ristretto.test.AnotherClass", "test", "hello world");

        assertThat(result, is("hello world"));
    }

    @Test
    void skips_annotated_fields() {
        var classWithField = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "import ristretto.PackagePrivate;",
            "",
            "public class TestSample {",
            "  ",
            "  @PackagePrivate String msg;",
            "  ",
            "  public TestSample(String msg) {",
            "    this.msg = msg;",
            "  }",
            "  ",
            "}"
        );

        String result = compile(List.of(classWithField, anotherClass)).invoke("ristretto.test.AnotherClass", "test", "hello world");

        assertThat(result, is("hello world"));
    }

}
