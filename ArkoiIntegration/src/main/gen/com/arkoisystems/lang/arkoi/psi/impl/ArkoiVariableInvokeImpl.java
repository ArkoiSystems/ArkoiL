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

public class ArkoiVariableInvokeImpl extends ASTWrapperPsiElement implements ArkoiVariableInvoke {

  public ArkoiVariableInvokeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ArkoiVisitor visitor) {
    visitor.visitVariableInvoke(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ArkoiVisitor) accept((ArkoiVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ArkoiVariableInvoke getVariableInvoke() {
    return findChildByClass(ArkoiVariableInvoke.class);
  }

  @Override
  @NotNull
  public PsiElement getIdentifier() {
    return findNotNullChildByType(IDENTIFIER);
  }

}
