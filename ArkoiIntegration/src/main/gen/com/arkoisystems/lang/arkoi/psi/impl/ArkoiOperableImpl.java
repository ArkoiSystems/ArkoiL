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

public class ArkoiOperableImpl extends ASTWrapperPsiElement implements ArkoiOperable {

  public ArkoiOperableImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ArkoiVisitor visitor) {
    visitor.visitOperable(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ArkoiVisitor) accept((ArkoiVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ArkoiFunctionCall getFunctionCall() {
    return findChildByClass(ArkoiFunctionCall.class);
  }

  @Override
  @Nullable
  public ArkoiLiterals getLiterals() {
    return findChildByClass(ArkoiLiterals.class);
  }

  @Override
  @Nullable
  public ArkoiVariableCall getVariableCall() {
    return findChildByClass(ArkoiVariableCall.class);
  }

}
