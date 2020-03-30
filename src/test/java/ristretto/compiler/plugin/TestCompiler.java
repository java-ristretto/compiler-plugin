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
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.tools.JavaFileObject.Kind.SOURCE;

final class TestCompiler {

    private final JavaCompiler compiler;

    private TestCompiler() {
        compiler = ToolProvider.getSystemJavaCompiler();
    }

    static TestCompiler newInstance() {
        return new TestCompiler();
    }

    Result compile(SourceCode sourceCode) {
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
        Writer additionalOutput = new StringWriter();

        FileManager fileManager = new FileManager(compiler.getStandardFileManager(
            diagnosticCollector,
            null,
            StandardCharsets.UTF_8
        ));

        JavaCompiler.CompilationTask task = compiler.getTask(
            additionalOutput,
            fileManager,
            diagnosticCollector,
            List.of("-classpath", System.getProperty("java.class.path"), "-Xplugin:" + JavacPlugin.NAME),
            null,
            List.of(sourceCode)
        );

        task.call();

        return new Result(
            diagnosticCollector.getDiagnostics(),
            additionalOutput.toString(),
            new LocalClassLoader(fileManager.compiledClasses)
        );
    }

    static final class SourceCode extends SimpleJavaFileObject {

        final String content;

        private SourceCode(String packageName, String publicClass, String content) {
            super(URI.create(String.format("string://%s/%s%s", packageName.replace('.', '/'), publicClass, SOURCE.extension)), SOURCE);
            this.content = content;
        }

        static SourceCode of(String packageName, String publicClass, String... content) {
            return new SourceCode(packageName, publicClass, String.join(System.lineSeparator(), content));
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }
    }

    static final class Result {

        final List<Diagnostic<? extends JavaFileObject>> diagnostics;
        final String additionalOutput;
        final LocalClassLoader classLoader;

        Result(List<Diagnostic<? extends JavaFileObject>> diagnostics, String additionalOutput, LocalClassLoader classLoader) {
            this.diagnostics = diagnostics;
            this.additionalOutput = additionalOutput;
            this.classLoader = classLoader;
        }

        String diagnostics() {
            return diagnostics.stream().map(Diagnostic::toString).collect(Collectors.joining(System.lineSeparator()));
        }

        public ClassWrapper loadClass(String name) {
            try {
                return new ClassWrapper(classLoader.loadClass(name));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static final class ClassWrapper {

        private final Class<?> aClass;

        ClassWrapper(Class<?> aClass) {
            this.aClass = aClass;
        }

        <T> T invoke(String methodName, String value) {
            return invoke(methodName, String.class, value);
        }

        <T> T invoke(String methodName, int value) {
            return invoke(methodName, int.class, value);
        }

        @SuppressWarnings("unchecked")
        private <T> T invoke(String methodName, Class<?> type, Object value) {
            try {
                return (T) aClass.getMethod(methodName, type).invoke(null, value);
            } catch (NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof RuntimeException) {
                    throw (RuntimeException) e.getTargetException();
                }
                throw new RuntimeException(e);
            }
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

        byte[] toBytecode() {
            return outputStream.toByteArray();
        }
    }

    private static final class FileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

        final Map<String, ClassFile> compiledClasses = new HashMap<>();

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
            ClassFile classFile = new ClassFile(className);
            compiledClasses.put(className, classFile);
            return classFile;
        }
    }

    private static final class LocalClassLoader extends ClassLoader {

        final Map<String, ClassFile> classFilesByName;

        LocalClassLoader(Map<String, ClassFile> classFilesByName) {
            this.classFilesByName = classFilesByName;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (!classFilesByName.containsKey(name)) {
                throw new ClassNotFoundException(name);
            }
            byte[] bytes = classFilesByName.get(name).toBytecode();
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}
