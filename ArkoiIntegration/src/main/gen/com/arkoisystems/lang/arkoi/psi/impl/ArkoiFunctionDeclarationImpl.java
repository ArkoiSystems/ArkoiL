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

public class ArkoiFunctionDeclarationImpl extends ASTWrapperPsiElement implements ArkoiFunctionDeclaration {

  public ArkoiFunctionDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ArkoiVisitor visitor) {
    visitor.visitFunctionDeclaration(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ArkoiVisitor) accept((ArkoiVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ArkoiArgumentList getArgumentList() {
    return findChildByClass(ArkoiArgumentList.class);
  }

  @Override
  @Nullable
  public ArkoiBlockDeclaration getBlockDeclaration() {
    return findChildByClass(ArkoiBlockDeclaration.class);
  }

  @Override
  @Nullable
  public ArkoiPrimitives getPrimitives() {
    return findChildByClass(ArkoiPrimitives.class);
  }

  @Override
  @Nullable
  public PsiElement getIdentifier() {
    return findChildByType(IDENTIFIER);
  }

}
