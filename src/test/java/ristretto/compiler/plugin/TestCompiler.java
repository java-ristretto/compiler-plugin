package ristretto.compiler.plugin;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

final class TestCompiler {

    private final JavaCompiler compiler;

    private TestCompiler() {
        compiler = ToolProvider.getSystemJavaCompiler();
    }

    static TestCompiler newInstance() {
        return new TestCompiler();
    }

    Result compile(String sourceCode) {
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
        Writer additionalOutput = new StringWriter();

        JavaCompiler.CompilationTask task = compiler.getTask(
            additionalOutput,
            new FileManager(compiler.getStandardFileManager(diagnosticCollector, null, StandardCharsets.UTF_8)),
            diagnosticCollector,
            List.of("-classpath", System.getProperty("java.class.path"), "-Xplugin:" + JavacPlugin.NAME),
            null,
            List.of(new SourceFile(sourceCode))
        );

        task.call();

        return new Result(diagnosticCollector.getDiagnostics(), additionalOutput.toString());
    }

    static final class Result {

        final List<Diagnostic<? extends JavaFileObject>> diagnostics;
        final String additionalOutput;

        Result(List<Diagnostic<? extends JavaFileObject>> diagnostics, String additionalOutput) {
            this.diagnostics = diagnostics;
            this.additionalOutput = additionalOutput;
        }

        String diagnostics() {
            return diagnostics.stream().map(Diagnostic::toString).collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private static final class SourceFile extends SimpleJavaFileObject {

        final String content;

        SourceFile(String content) {
            super(URI.create("file://ristretto/test/TestSample.java"), Kind.SOURCE);
            this.content = content;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }
    }

    private static final class ClassFile extends SimpleJavaFileObject {

        ByteArrayOutputStream outputStream;

        ClassFile(String className) {
            super(URI.create("string://" + className), Kind.CLASS);
        }

        @Override
        public OutputStream openOutputStream() {
            outputStream = new ByteArrayOutputStream();
            return outputStream;
        }
    }

    private static final class FileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

        FileManager(StandardJavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(
            Location location,
            String className,
            JavaFileObject.Kind kind,
            FileObject sibling
        ) {
            return new ClassFile(className);
        }
    }
}
