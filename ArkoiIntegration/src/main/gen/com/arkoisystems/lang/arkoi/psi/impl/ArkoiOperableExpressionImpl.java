// This is a generated file. Not intended for manual editing.
package com.arkoisystems.lang.arkoi.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.arkoisystems.lang.arkoi.ArkoiTokenTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.arkoisystems.lang.arkoi.psi.*;

public class ArkoiOperableExpressionImpl extends ASTWrapperPsiElement implements ArkoiOperableExpression {

  public ArkoiOperableExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ArkoiVisitor visitor) {
    visitor.visitOperableExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ArkoiVisitor) accept((ArkoiVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ArkoiCastExpression getCastExpression() {
    return findChildByClass(ArkoiCastExpression.class);
  }

  @Override
  @Nullable
  public ArkoiOperable getOperable() {
    return findChildByClass(ArkoiOperable.class);
  }

  @Override
  @Nullable
  public ArkoiParenthesizedExpression getParenthesizedExpression() {
    return findChildByClass(ArkoiParenthesizedExpression.class);
  }

  @Override
  @Nullable
  public ArkoiPostfixExpression getPostfixExpression() {
    return findChildByClass(ArkoiPostfixExpression.class);
  }

  @Override
  @Nullable
  public ArkoiPrefixExpression getPrefixExpression() {
    return findChildByClass(ArkoiPrefixExpression.class);
  }

}
