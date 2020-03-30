package ristretto.compiler.plugin;

import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;

final class MethodParameterFinalModifier extends TreeScanner<Void, Void> {

    static final TreeScanner<Void, Void> INSTANCE = new MethodParameterFinalModifier();

    private MethodParameterFinalModifier() {
    }

    @Override
    public Void visitMethod(MethodTree method, Void data) {
        for (var parameter : method.getParameters()) {
            JCTree.JCVariableDecl declaration = (JCTree.JCVariableDecl) parameter;
            declaration.mods.flags |= Flags.FINAL;
        }
        return super.visitMethod(method, data);
    }
}
