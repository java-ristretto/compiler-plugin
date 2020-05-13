package ristretto.compiler.plugin;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;

public final class JavacPlugin implements Plugin {

    public static final String NAME = "ristretto";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void init(JavacTask task, String... args) {
        DiagnosticsReport diagnosticsReport;

        if (args.length > 0 && "--output=stderr".equals(args[0])) {
            Context context = ((BasicJavacTask) task).getContext();
            diagnosticsReport = new DiagnosticsReport(RistrettoLogger.stderr(Log.instance(context)));
        } else {
            diagnosticsReport = new DiagnosticsReport(RistrettoLogger.javaUtilLogging());
        }

        task.addTaskListener(new OnParseFinished(diagnosticsReport));
        task.addTaskListener(new OnCompilationFinished(diagnosticsReport));

        diagnosticsReport.pluginLoaded();
    }

    private static final class OnParseFinished implements TaskListener {

        final DiagnosticsReport report;

        OnParseFinished(DiagnosticsReport report) {
            this.report = report;
        }

        @Override
        public void finished(TaskEvent event) {
            if (!TaskEvent.Kind.PARSE.equals(event.getKind())) {
                return;
            }
            CompilationUnitTree compilationUnit = event.getCompilationUnit();
            compilationUnit.accept(new VariableFinalModifier(report.withJavaFile(compilationUnit.getSourceFile())), null);
        }
    }

    private static final class OnCompilationFinished implements TaskListener {

        final DiagnosticsReport report;

        OnCompilationFinished(DiagnosticsReport report) {
            this.report = report;
        }

        @Override
        public void finished(TaskEvent event) {
            if (!TaskEvent.Kind.COMPILATION.equals(event.getKind())) {
                return;
            }
            report.pluginFinished();
        }
    }
}
