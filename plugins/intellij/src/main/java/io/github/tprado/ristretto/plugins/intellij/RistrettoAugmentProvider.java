package io.github.tprado.ristretto.plugins.intellij;

import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.augment.PsiAugmentProvider;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public final class RistrettoAugmentProvider extends PsiAugmentProvider {

  private static final Logger LOG = Logger.getLogger(RistrettoAugmentProvider.class.getName());

  public RistrettoAugmentProvider() {
    LOG.info("ristretto augment provider loaded");
  }

  @Override
  protected @NotNull Set<String> transformModifiers(
    @NotNull PsiModifierList modifierList,
    @NotNull Set<String> modifiers
  ) {
    if (modifierList.getParent() instanceof PsiLocalVariable) {
      Set<String> result = new HashSet<>(modifiers);
      result.add(PsiModifier.FINAL);
      return result;
    }

    return modifiers;
  }
}
