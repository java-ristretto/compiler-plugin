package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

class PrivateInnerClassTest extends JavacPluginBaseTest {

    TestCompiler.SourceCode anotherClass;

    @BeforeEach
    void beforeEach() {
        anotherClass = TestCompiler.SourceCode.of("ristretto.test", "AnotherClass", "",
            "package ristretto.test;",
            "",
            "public class AnotherClass {",
            "  ",
            "  public static String test(String value) {",
            "    TestSample.InnerClass sample = TestSample.newInnerClass(value);",
            "    return sample.msg;",
            "  }",
            "  ",
            "}"
        );
    }

    @Test
    void sets_inner_class_as_private_when_no_access_modifier_is_present() {
        var classWithField = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "public class TestSample {",
            "  ",
            "  static class InnerClass {",
            "    ",
            "    public String msg;",
            "    ",
            "    InnerClass(String msg) {",
            "      this.msg = msg;",
            "    }",
            "  }",
            "  ",
            "  public static InnerClass newInnerClass(String msg) {",
            "    return new InnerClass(msg);",
            "  }",
            "  ",
            "}"
        );

        TestCompiler.Result result = compile(List.of(classWithField, anotherClass));

        assertThat(result.diagnostics(), containsString("AnotherClass.java:7: error: ristretto.test.TestSample.InnerClass has private access in ristretto.test.TestSample"));
    }

    @Test
    void skips_public_inner_classes() {
        var classWithField = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "public class TestSample {",
            "  ",
            "  public static class InnerClass {",
            "    ",
            "    public String msg;",
            "    ",
            "    InnerClass(String msg) {",
            "      this.msg = msg;",
            "    }",
            "  }",
            "  ",
            "  public static InnerClass newInnerClass(String msg) {",
            "    return new InnerClass(msg);",
            "  }",
            "  ",
            "}"
        );

        String result = compile(List.of(classWithField, anotherClass)).invoke("ristretto.test.AnotherClass", "test", "hello world");

        assertThat(result, is("hello world"));
    }

    @Test
    void skips_protected_inner_classes() {
        var classWithField = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "public class TestSample {",
            "  ",
            "  protected static class InnerClass {",
            "    ",
            "    public String msg;",
            "    ",
            "    InnerClass(String msg) {",
            "      this.msg = msg;",
            "    }",
            "  }",
            "  ",
            "  public static InnerClass newInnerClass(String msg) {",
            "    return new InnerClass(msg);",
            "  }",
            "  ",
            "}"
        );

        String result = compile(List.of(classWithField, anotherClass)).invoke("ristretto.test.AnotherClass", "test", "hello world");

        assertThat(result, is("hello world"));
    }

    @Test
    void skips_annotated_inner_classes() {
        var classWithField = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "import ristretto.PackagePrivate;",
            "",
            "public class TestSample {",
            "  ",
            "  @PackagePrivate static class InnerClass {",
            "    ",
            "    public String msg;",
            "    ",
            "    InnerClass(String msg) {",
            "      this.msg = msg;",
            "    }",
            "  }",
            "  ",
            "  public static InnerClass newInnerClass(String msg) {",
            "    return new InnerClass(msg);",
            "  }",
            "  ",
            "}"
        );

        String result = compile(List.of(classWithField, anotherClass)).invoke("ristretto.test.AnotherClass", "test", "hello world");

        assertThat(result, is("hello world"));
    }

}
