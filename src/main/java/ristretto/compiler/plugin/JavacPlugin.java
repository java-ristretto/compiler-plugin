package ristretto.compiler.plugin;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;

public final class JavacPlugin implements Plugin {

    @Override
    public String getName() {
        return "ristretto";
    }

    @Override
    public void init(final JavacTask task, final String... args) {

    }
}
