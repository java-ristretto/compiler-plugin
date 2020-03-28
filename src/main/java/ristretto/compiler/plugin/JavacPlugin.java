package ristretto.compiler.plugin;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Names;

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
            compilationUnit.accept(MethodParameterFinalModifier.INSTANCE, null);
            compilationUnit.accept(new NullCheckForPublicMethodParameter(context), null);
        }
    }

    private static final class MethodParameterFinalModifier extends TreeScanner<Void, Void> {

        static final TreeScanner<Void, Void> INSTANCE = new MethodParameterFinalModifier();

        @Override
        public Void visitMethod(MethodTree method, Void data) {
            for (var parameter : method.getParameters()) {
                JCVariableDecl declaration = (JCVariableDecl) parameter;
                declaration.mods.flags |= Flags.FINAL;
            }
            return super.visitMethod(method, data);
        }
    }

    private static final class NullCheckForPublicMethodParameter extends TreeScanner<Void, Void> {

        final Context context;

        NullCheckForPublicMethodParameter(Context context) {
            this.context = context;
        }

        @Override
        public Void visitMethod(MethodTree method, Void data) {
            TreeMaker maker = TreeMaker.instance(context);
            Names symbols = Names.instance(context);

            for (var parameter : method.getParameters()) {
                JCVariableDecl declaration = (JCVariableDecl) parameter;

                JCTree.JCIf nullCheck = maker.If(
                    maker.Binary(JCTree.Tag.EQ, maker.Ident(declaration.getName()), maker.Literal(TypeTag.BOT, null)),
                    maker.Throw(
                        maker.NewClass(
                            null,
                            List.nil(),
                            maker.Ident(symbols.fromString(NullPointerException.class.getSimpleName())),
                            List.of(maker.Literal(String.format("%s is null", declaration.getName()))),
                            null
                        )
                    ),
                    null
                );

                JCTree.JCBlock body = (JCTree.JCBlock) method.getBody();
                body.stats = body.stats.prepend(nullCheck);
            }
            return super.visitMethod(method, data);
        }
    }
}
