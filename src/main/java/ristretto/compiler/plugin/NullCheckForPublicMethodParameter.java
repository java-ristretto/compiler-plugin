package ristretto.compiler.plugin;

import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

final class NullCheckForPublicMethodParameter extends TreeScanner<Void, Void> {

    private final Context context;

    private NullCheckForPublicMethodParameter(Context context) {
        this.context = context;
    }

    @Override
    public Void visitMethod(MethodTree method, Void data) {
        JCTreeCatalog catalog = JCTreeCatalog.of(context);

        List<JCTree.JCStatement> nullChecks = method.getParameters()
            .stream()
            .filter(JCTreeCatalog::notPrimitiveType)
            .map(catalog::nullCheck)
            .collect(List.collector());

        JCTree.JCBlock body = (JCTree.JCBlock) method.getBody();
        body.stats = body.stats.prependList(nullChecks);

        return super.visitMethod(method, data);
    }

    static NullCheckForPublicMethodParameter of(Context context) {
        return new NullCheckForPublicMethodParameter(context);
    }

}
