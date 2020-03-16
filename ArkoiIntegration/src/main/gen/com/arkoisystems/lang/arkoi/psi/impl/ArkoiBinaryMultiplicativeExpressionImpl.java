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

public class ArkoiBinaryMultiplicativeExpressionImpl extends ASTWrapperPsiElement implements ArkoiBinaryMultiplicativeExpression {

  public ArkoiBinaryMultiplicativeExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ArkoiVisitor visitor) {
    visitor.visitBinaryMultiplicativeExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ArkoiVisitor) accept((ArkoiVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ArkoiBinaryMultiplicativeExpression getBinaryMultiplicativeExpression() {
    return findChildByClass(ArkoiBinaryMultiplicativeExpression.class);
  }

  @Override
  @NotNull
  public ArkoiCollectionExpression getCollectionExpression() {
    return findNotNullChildByClass(ArkoiCollectionExpression.class);
  }

}
