package ristretto.compiler.plugin;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class JavacPluginTest extends JavacPluginBaseTest {

    @Test
    void ignores_specified_package() {
        var code = TestCompiler.SourceCode.of("ristretto.test", "TestSample", "",
            "package ristretto.test;",
            "",
            "public class TestSample {",
            "",
            "  public static String test(String parameter) {",
            "    parameter += \":value\";",
            "    return parameter;",
            "  }",
            "",
            "}"
        );

        var result = compile(code, "--ignore-packages=ristretto.test").invoke("ristretto.test.TestSample", "test", "value");

        assertThat(result, is("value:value"));
    }
}
