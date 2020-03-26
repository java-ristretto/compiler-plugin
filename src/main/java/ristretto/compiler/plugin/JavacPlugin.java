package ristretto.compiler.plugin;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;

public final class JavacPlugin implements Plugin {

    @Override
    public String getName() {
        return "ristretto";
    }

    @Override
    public void init(final JavacTask task, final String... args) {
        Context context = ((BasicJavacTask) task).getContext();
        Log.instance(context).printRawLines(Log.WriterKind.NOTICE, getName() + " plugin");
    }
}
