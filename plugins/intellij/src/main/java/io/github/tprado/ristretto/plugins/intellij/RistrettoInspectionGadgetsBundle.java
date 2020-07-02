package io.github.tprado.ristretto.plugins.intellij;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public final class RistrettoInspectionGadgetsBundle extends DynamicBundle {

  @NonNls
  public static final String BUNDLE = "messages.RistrettoInspectionGadgetsBundle";

  private static final RistrettoInspectionGadgetsBundle INSTANCE = new RistrettoInspectionGadgetsBundle();

  private RistrettoInspectionGadgetsBundle() { super(BUNDLE); }

  @NotNull
  public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params) {
    return INSTANCE.getMessage(key, params);
  }
}