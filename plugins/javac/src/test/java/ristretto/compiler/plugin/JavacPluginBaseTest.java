package ristretto.compiler.plugin;

import org.junit.jupiter.api.BeforeEach;

import java.util.List;

abstract class JavacPluginBaseTest {

  private TestCompiler compiler;

  @BeforeEach
  final void newCompiler() {
    compiler = new TestCompiler();
  }

  final TestCompiler.Result compile(List<TestCompiler.SourceCode> sourceCode, String... pluginArgs) {
    return compiler.compile(sourceCode, pluginArgs);
  }

  final TestCompiler.Result compile(TestCompiler.SourceCode sourceCode, String... pluginArgs) {
    return compiler.compile(sourceCode, pluginArgs);
  }
}
