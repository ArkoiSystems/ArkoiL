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

public class ArkoiBinaryAdditiveExpressionImpl extends ASTWrapperPsiElement implements ArkoiBinaryAdditiveExpression {

  public ArkoiBinaryAdditiveExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ArkoiVisitor visitor) {
    visitor.visitBinaryAdditiveExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ArkoiVisitor) accept((ArkoiVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ArkoiBinaryAdditiveExpressionPart> getBinaryAdditiveExpressionPartList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ArkoiBinaryAdditiveExpressionPart.class);
  }

  @Override
  @NotNull
  public ArkoiBinaryMultiplicativeExpression getBinaryMultiplicativeExpression() {
    return findNotNullChildByClass(ArkoiBinaryMultiplicativeExpression.class);
  }

}
