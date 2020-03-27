package ristretto.compiler.plugin;

import com.sun.source.tree.MethodTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
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
        task.addTaskListener(OnParseFinished.INSTANCE);

        Context context = ((BasicJavacTask) task).getContext();
        Log.instance(context).printRawLines(Log.WriterKind.NOTICE, String.format("%s plugin loaded", NAME));
    }

    private static final class OnParseFinished implements TaskListener {

        private static final TaskListener INSTANCE = new OnParseFinished();

        @Override
        public void finished(TaskEvent event) {
            if (!TaskEvent.Kind.PARSE.equals(event.getKind())) {
                return;
            }
            event.getCompilationUnit().accept(MethodParameterFinalModifier.INSTANCE, null);
        }
    }

    private static final class MethodParameterFinalModifier extends TreeScanner<Void, Void> {

        private static final TreeScanner<Void, Void> INSTANCE = new MethodParameterFinalModifier();

        @Override
        public Void visitMethod(MethodTree method, Void data) {
            for (var parameter : method.getParameters()) {
                JCVariableDecl declaration = (JCVariableDecl) parameter;
                declaration.mods.flags |= Flags.FINAL;
            }
            return super.visitMethod(method, data);
        }
    }
}
