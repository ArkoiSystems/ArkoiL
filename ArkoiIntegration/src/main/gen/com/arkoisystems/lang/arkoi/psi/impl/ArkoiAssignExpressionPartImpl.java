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

public class ArkoiAssignExpressionPartImpl extends ASTWrapperPsiElement implements ArkoiAssignExpressionPart {

  public ArkoiAssignExpressionPartImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ArkoiVisitor visitor) {
    visitor.visitAssignExpressionPart(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ArkoiVisitor) accept((ArkoiVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ArkoiAddAssignExpression getAddAssignExpression() {
    return findChildByClass(ArkoiAddAssignExpression.class);
  }

  @Override
  @Nullable
  public ArkoiAssignExpression getAssignExpression() {
    return findChildByClass(ArkoiAssignExpression.class);
  }

  @Override
  @Nullable
  public ArkoiDivAssignExpression getDivAssignExpression() {
    return findChildByClass(ArkoiDivAssignExpression.class);
  }

  @Override
  @Nullable
  public ArkoiExpAssignExpression getExpAssignExpression() {
    return findChildByClass(ArkoiExpAssignExpression.class);
  }

  @Override
  @Nullable
  public ArkoiModAssignExpression getModAssignExpression() {
    return findChildByClass(ArkoiModAssignExpression.class);
  }

  @Override
  @Nullable
  public ArkoiMulAssignExpression getMulAssignExpression() {
    return findChildByClass(ArkoiMulAssignExpression.class);
  }

  @Override
  @Nullable
  public ArkoiSubAssignExpression getSubAssignExpression() {
    return findChildByClass(ArkoiSubAssignExpression.class);
  }

}
