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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallOperableSyntaxAST;

public class ArkoiIdentifierCallImpl extends ASTWrapperPsiElement implements ArkoiIdentifierCall {

  public ArkoiIdentifierCallImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ArkoiVisitor visitor) {
    visitor.visitIdentifierCall(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ArkoiVisitor) accept((ArkoiVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ArkoiIdentifierCallPart> getIdentifierCallPartList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ArkoiIdentifierCallPart.class);
  }

  @Override
  @Nullable
  public IdentifierCallOperableSyntaxAST getIdentifierCall(@NotNull SyntaxAnalyzer syntaxAnalyzer) {
    return ArkoiPsiImplUtil.getIdentifierCall(this, syntaxAnalyzer);
  }

}
