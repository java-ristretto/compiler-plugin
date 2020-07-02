package io.github.tprado.ristretto.plugins.intellij;

import com.intellij.codeInspection.CleanupLocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.InspectionGadgetsFix;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class UnnecessaryFinalModifierInspection extends BaseInspection implements CleanupLocalInspectionTool {

  @Override
  public boolean isEnabledByDefault() {
    return true;
  }

  @Override
  @NotNull
  public String buildErrorString(Object... infos) {
    return RistrettoInspectionGadgetsBundle.message("unnecessary.final.modifier.problem");
  }

  @Override
  public BaseInspectionVisitor buildVisitor() {
    return new UnnecessaryFinalModifierVisitor();
  }

  @Override
  public InspectionGadgetsFix buildFix(Object... infos) {
    return new UnnecessaryFinalModifiersFix((String) infos[0]);
  }

  private static final class UnnecessaryFinalModifiersFix extends InspectionGadgetsFix {

    private final String modifiersText;

    private UnnecessaryFinalModifiersFix(String modifiersText) {
      this.modifiersText = modifiersText;
    }

    @Override
    @NotNull
    public String getName() {
      return RistrettoInspectionGadgetsBundle.message("smth.unnecessary.remove.quickfix", modifiersText);
    }

    @NotNull
    @Override
    public String getFamilyName() {
      return RistrettoInspectionGadgetsBundle.message("unnecessary.interface.modifiers.fix.family.name");
    }

    @Override
    public void doFix(Project project, ProblemDescriptor descriptor) {
      final PsiModifierList modifierList = (PsiModifierList) descriptor.getPsiElement().getParent();
      modifierList.setModifierProperty(PsiModifier.FINAL, false);
    }
  }

  private static class UnnecessaryFinalModifierVisitor extends BaseInspectionVisitor {

    @Override
    public void visitLocalVariable(PsiLocalVariable variable) {
      PsiModifierList actualModifiers = variable.getModifierList();

      if (actualModifiers == null) {
        return;
      }

      Set<String> redundantModifiers = new HashSet<>();
      redundantModifiers.add(PsiModifier.FINAL);

      final PsiElement[] modifiersAsElements = actualModifiers.getChildren();
      final StringBuilder redundantModifiersFound = new StringBuilder();
      for (PsiElement modifierElement : modifiersAsElements) {
        final String modifierText = modifierElement.getText();
        if (redundantModifiers.contains(modifierText)) {
          if (redundantModifiersFound.length() > 0) {
            redundantModifiersFound.append(' ');
          }
          redundantModifiersFound.append(modifierText);
        }
      }

      for (PsiElement modifierElement : modifiersAsElements) {
        if (redundantModifiers.contains(modifierElement.getText())) {
          registerError(modifierElement, ProblemHighlightType.LIKE_UNUSED_SYMBOL, redundantModifiersFound.toString(), actualModifiers);
        }
      }
    }

  }
}
