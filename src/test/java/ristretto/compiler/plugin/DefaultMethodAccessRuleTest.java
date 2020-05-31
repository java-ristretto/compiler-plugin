package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static ristretto.compiler.plugin.TestCompilerMatchers.hasOutput;

class DefaultMethodAccessRuleTest extends JavacPluginBaseTest {

    TestCompiler.SourceCode anotherClass;

    @BeforeEach
    void beforeEach() {
        anotherClass = TestCompiler.SourceCode.of(
            "package ristretto.test.client;",
            "",
            "import ristretto.test.TestSample;",
            "",
            "public class AnotherClass {",
            "  public static String test(String value) {",
            "    return new TestSample().msg(value);",
            "  }",
            "}"
        );
    }

    @Test
    void sets_method_as_public_when_no_access_modifier_is_present() {
        var code = TestCompiler.SourceCode.of(
            "package ristretto.test;",
            "",
            "public class TestSample {",
            "",
            "  TestSample() {",
            "  }",
            "",
            "  String msg(String msg) {",
            "    return msg;",
            "  }",
            "}"
        );

        String result = compile(List.of(code, anotherClass)).invoke("ristretto.test.client.AnotherClass", "test", "hello world");

        assertThat(result, is("hello world"));
    }

    @Test
    void skips_protected_method() {
        var code = TestCompiler.SourceCode.of(
            "package ristretto.test;",
            "",
            "public class TestSample {",
            "  protected static String msg(String msg) {",
            "    return msg;",
            "  }",
            "}"
        );

        TestCompiler.Result result = compile(List.of(code, anotherClass));

        assertThat(result.diagnostics(), containsString("AnotherClass.java:7: error: msg(java.lang.String) has protected access in ristretto.test.TestSample"));
    }

    @Test
    void skips_private_method() {
        var code = TestCompiler.SourceCode.of(
            "package ristretto.test;",
            "",
            "public class TestSample {",
            "  private static String msg(String msg) {",
            "    return msg;",
            "  }",
            "}"
        );

        TestCompiler.Result result = compile(List.of(code, anotherClass));

        assertThat(result.diagnostics(), containsString("AnotherClass.java:7: error: msg(java.lang.String) has private access in ristretto.test.TestSample"));
    }

    @Test
    void skips_annotated_method() {
        var code = TestCompiler.SourceCode.of(
            "package ristretto.test;",
            "",
            "import ristretto.PackagePrivate;",
            "",
            "public class TestSample {",
            "  @PackagePrivate static String msg(String msg) {",
            "    return msg;",
            "  }",
            "}"
        );

        TestCompiler.Result result = compile(List.of(code, anotherClass));

        assertThat(result.diagnostics(), containsString("AnotherClass.java:7: error: msg(java.lang.String) is not public in ristretto.test.TestSample"));
        assertThat(result, hasOutput("DefaultMethodAccessRule /test/TestSample.java:6 MODIFIER_NOT_ADDED"));
    }

    @Test
    void skips_enum_constructor() {
        var code = TestCompiler.SourceCode.of(
            "package ristretto.test;",
            "",
            "public enum TestSample {",
            "",
            "  INSTANCE;",
            "",
            "  TestSample() {",
            "  }",
            "",
            "  static String test(String value) {",
            "    return TestSample.INSTANCE.name() + \"-\" + value;",
            "  }",
            "",
            "}"
        );

        String result = compile(code).invoke("ristretto.test.TestSample", "test", "hello");

        assertThat(result, is("INSTANCE-hello"));
    }

    @Test
    void notifies_about_modifier_already_present() {
        var code = TestCompiler.SourceCode.of(
            "package ristretto.test;",
            "",
            "public class TestSample {",
            "  public static String msg(String msg) {",
            "    return msg;",
            "  }",
            "}"
        );

        var result = compile(code);

        assertThat(result, hasOutput("DefaultMethodAccessRule /test/TestSample.java:4 MODIFIER_ALREADY_PRESENT"));
    }
}
