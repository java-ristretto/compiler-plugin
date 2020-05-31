package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static ristretto.compiler.plugin.TestCompilerMatchers.hasOutput;

class DiagnosticsReportTest extends JavacPluginBaseTest {

    TestCompiler.Result compilerResult;

    @BeforeEach
    void beforeEach() {
        var code = TestCompiler.SourceCode.of(
            "package ristretto.test;",
            "",
            "import ristretto.PackagePrivate;",
            "import ristretto.Mutable;",
            "",
            "public class TestSample {",
            "",
            "  String field1;",
            "  final String field2;",
            "  @Mutable String field3;",
            "",
            "  void someMethod() {",
            "  }",
            "",
            "}"
        );

        compilerResult = compile(code);
    }

    @Test
    void prints_when_the_plugin_is_loaded() {
        assertThat(compilerResult, hasOutput("ristretto plugin loaded"));
    }

    @Test
    void prints_message_when_modifier_is_added() {
        assertThat(compilerResult, hasOutput("DefaultFieldImmutabilityRule /test/TestSample.java:8 MODIFIER_ADDED"));
    }

    @Test
    void prints_message_when_modifier_is_already_present() {
        assertThat(compilerResult, hasOutput("DefaultFieldImmutabilityRule /test/TestSample.java:9 MODIFIER_ALREADY_PRESENT"));
    }

    @Test
    void prints_message_when_modifier_is_not_added() {
        assertThat(compilerResult, hasOutput("DefaultFieldImmutabilityRule /test/TestSample.java:10 MODIFIER_NOT_ADDED"));
    }

    @Test
    void prints_summary() {
        assertThat(compilerResult, hasOutput(
            "summary:",
            "| rule                                     | inspected   | added   | present | not added |",
            "|------------------------------------------|-------------|---------|---------|-----------|",
            "| DefaultFieldImmutabilityRule             |           3 |  33.33% |  33.33% |    33.33% |",
            "| DefaultParameterImmutabilityRule         |           0 |       - |       - |         - |",
            "| DefaultLocalVariableImmutabilityRule     |           0 |       - |       - |         - |",
            "| DefaultFieldAccessRule                   |           3 | 100.00% |   0.00% |     0.00% |",
            "| DefaultMethodAccessRule                  |           1 | 100.00% |   0.00% |     0.00% |"
        ));
    }
}
