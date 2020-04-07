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
        Context context = ((BasicJavacTask) task).getContext();
        task.addTaskListener(new OnParseFinished(context));
        Log.instance(context).printRawLines(Log.WriterKind.NOTICE, String.format("%s plugin loaded", NAME));
    }

    private static final class OnParseFinished implements TaskListener {

        final Context context;

        private OnParseFinished(Context context) {
            this.context = context;
        }

        @Override
        public void finished(TaskEvent event) {
            if (!TaskEvent.Kind.PARSE.equals(event.getKind())) {
                return;
            }
            CompilationUnitTree compilationUnit = event.getCompilationUnit();
            compilationUnit.accept(
                MethodParameterFinalModifier.INSTANCE,
                AnnotationNameResolver.newResolver()
            );
            compilationUnit.accept(
                NullCheckForPublicMethodParameter.of(context),
                AnnotationNameResolver.newResolver()
            );
        }
    }
}
