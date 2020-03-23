// This is a generated file. Not intended for manual editing.
package com.arkoisystems.lang.arkoi.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ArkoiOperableExpression extends PsiElement {

  @Nullable
  ArkoiCastExpression getCastExpression();

  @Nullable
  ArkoiOperable getOperable();

  @Nullable
  ArkoiParenthesizedExpression getParenthesizedExpression();

  @Nullable
  ArkoiPostfixExpression getPostfixExpression();

  @Nullable
  ArkoiPrefixExpression getPrefixExpression();

}
