// This is a generated file. Not intended for manual editing.
package com.arkoisystems.lang.arkoi.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ArkoiFunctionInvoke extends PsiElement {

  @NotNull
  List<ArkoiExpression> getExpressionList();

  @Nullable
  ArkoiFunctionInvoke getFunctionInvoke();

  @NotNull
  PsiElement getIdentifier();

}
