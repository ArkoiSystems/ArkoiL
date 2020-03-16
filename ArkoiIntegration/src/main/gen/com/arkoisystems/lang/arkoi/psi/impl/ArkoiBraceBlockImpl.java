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

public class ArkoiBraceBlockImpl extends ASTWrapperPsiElement implements ArkoiBraceBlock {

  public ArkoiBraceBlockImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ArkoiVisitor visitor) {
    visitor.visitBraceBlock(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ArkoiVisitor) accept((ArkoiVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ArkoiFunctionInvoke> getFunctionInvokeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ArkoiFunctionInvoke.class);
  }

  @Override
  @NotNull
  public List<ArkoiReturnDeclaration> getReturnDeclarationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ArkoiReturnDeclaration.class);
  }

  @Override
  @NotNull
  public List<ArkoiVariableDeclaration> getVariableDeclarationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ArkoiVariableDeclaration.class);
  }

  @Override
  @NotNull
  public List<ArkoiVariableInvoke> getVariableInvokeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ArkoiVariableInvoke.class);
  }

}
