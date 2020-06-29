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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.tools.JavaFileObject.Kind.SOURCE;

// TODO: parse events from plugin instead of matching console output string in tests assertions
// TODO: enable each rule individually for each test scenario
final class TestCompiler {

  private final JavaCompiler compiler;

  TestCompiler() {
    compiler = ToolProvider.getSystemJavaCompiler();
  }

  Result compile(SourceCode sourceCode, String... pluginArgs) {
    return compile(List.of(sourceCode), pluginArgs);
  }

  Result compile(List<SourceCode> sourceCode, String... pluginArgs) {
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
      List.of("-classpath", System.getProperty("java.class.path"), "-Xplugin:" + JavacPlugin.NAME + " --output=stderr " + String.join(" ", pluginArgs)),
      null,
      sourceCode
    );

    task.call();

    return new Result(
      diagnosticCollector.getDiagnostics(),
      additionalOutput.toString(),
      new LocalClassLoader(fileManager.compiledClasses)
    );
  }

  static final class SourceCode extends SimpleJavaFileObject {

    private static final Pattern PACKAGE_DECLARATION = Pattern.compile(
      "\\s*package\\s*(?<name>[a-z0-9.]+);\\s*"
    );

    private static final Pattern PUBLIC_TYPE_DECLARATION = Pattern.compile(
      "\\s*public\\s+((final|abstract)\\s+)?(class|enum|interface)\\s+(?<name>[A-Za-z]+)\\s+(extends\\s+[A-Za-z]+\\s+)?\\{\\s*"
    );

    final String content;

    private SourceCode(URI uri, String content) {
      super(uri, SOURCE);
      this.content = content;
    }

    static SourceCode of(String... content) {
      String packageName = extractPackageName(content);
      String publicClass = extractPublicClassName(content);

      URI uri = URI.create(String.format(
        "string://%s/%s%s",
        packageName.replace('.', '/'),
        publicClass,
        SOURCE.extension
      ));

      return new SourceCode(uri, String.join(System.lineSeparator(), content));
    }

    private static String extractPackageName(String[] content) {
      return Stream.of(content)
        .map(PACKAGE_DECLARATION::matcher)
        .filter(Matcher::matches)
        .map(matcher -> matcher.group("name"))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("no package declaration"));
    }

    private static String extractPublicClassName(String[] content) {
      return Stream.of(content)
        .map(PUBLIC_TYPE_DECLARATION::matcher)
        .filter(Matcher::matches)
        .map(matcher -> matcher.group("name"))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("no public type declaration"));
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

    <T> T invoke(String className, String methodName, String value) {
      return loadClass(className).invoke(methodName, String.class, value);
    }

    private ClassWrapper loadClass(String name) {
      try {
        return new ClassWrapper(classLoader.loadClass(name));
      } catch (ClassNotFoundException e) {
        System.out.println("plugin output:");
        System.out.println(additionalOutput);
        System.out.println();
        System.out.println("compiler output:");
        System.out.println(diagnostics());

        throw new RuntimeException(e);
      }
    }
  }

  private static final class ClassWrapper {

    private final Class<?> aClass;

    ClassWrapper(Class<?> aClass) {
      this.aClass = aClass;
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
