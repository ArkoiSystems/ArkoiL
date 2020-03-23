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

public class ArkoiVariableDeclarationImpl extends ASTWrapperPsiElement implements ArkoiVariableDeclaration {

  public ArkoiVariableDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ArkoiVisitor visitor) {
    visitor.visitVariableDeclaration(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ArkoiVisitor) accept((ArkoiVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ArkoiAnnotationCall> getAnnotationCallList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ArkoiAnnotationCall.class);
  }

  @Override
  @NotNull
  public List<ArkoiCommentDeclaration> getCommentDeclarationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ArkoiCommentDeclaration.class);
  }

  @Override
  @Nullable
  public ArkoiInlinedBlock getInlinedBlock() {
    return findChildByClass(ArkoiInlinedBlock.class);
  }

  @Override
  @Nullable
  public PsiElement getIdentifier() {
    return findChildByType(IDENTIFIER);
  }

}
