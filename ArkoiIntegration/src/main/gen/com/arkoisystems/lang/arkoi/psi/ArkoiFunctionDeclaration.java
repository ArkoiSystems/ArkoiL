// This is a generated file. Not intended for manual editing.
package com.arkoisystems.lang.arkoi.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ArkoiFunctionDeclaration extends PsiElement {

  @Nullable
  ArkoiBraceBlock getBraceBlock();

  @Nullable
  ArkoiInlinedBlock getInlinedBlock();

  @NotNull
  List<ArkoiPrimitives> getPrimitivesList();

}
