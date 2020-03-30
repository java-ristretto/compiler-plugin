package ristretto.compiler.plugin;

import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

final class JCTreeCatalog {

    private final TreeMaker maker;
    private final Names symbols;

    private JCTreeCatalog(TreeMaker maker, Names symbols) {
        this.maker = maker;
        this.symbols = symbols;
    }

    /*
     * Builds the equivalent to the following statement:
     *
     * if (<variable name> == null) throw new NullPointerException("<variable name> is null");
     */
    JCTree.JCIf nullCheck(VariableTree variableTree) {
        JCTree.JCVariableDecl variableDeclaration = (JCTree.JCVariableDecl) variableTree;
        Name variableName = variableDeclaration.getName();

        return maker.at(variableDeclaration.pos).If(
            maker.Binary(JCTree.Tag.EQ, maker.Ident(variableName), maker.Literal(TypeTag.BOT, null)),
            maker.Throw(
                maker.NewClass(
                    null,
                    List.nil(),
                    maker.Ident(symbols.fromString(NullPointerException.class.getSimpleName())),
                    List.of(maker.Literal(String.format("%s is null", variableName))),
                    null
                )
            ),
            null
        );
    }

    static JCTreeCatalog of(Context context) {
        return new JCTreeCatalog(TreeMaker.instance(context), Names.instance(context));
    }

    static boolean notPrimitiveType(VariableTree parameter) {
        return !JCTree.Kind.PRIMITIVE_TYPE.equals(parameter.getType().getKind());
    }
}
