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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.ArgumentSyntaxAST;

public class ArkoiAnnotationCallImpl extends ASTWrapperPsiElement implements ArkoiAnnotationCall {

  public ArkoiAnnotationCallImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ArkoiVisitor visitor) {
    visitor.visitAnnotationCall(this);
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
  public ArkoiIdentifierCall getIdentifierCall() {
    return findChildByClass(ArkoiIdentifierCall.class);
  }

  @Override
  @Nullable
  public IdentifierCallOperableSyntaxAST getAnnotationCall(@NotNull SyntaxAnalyzer syntaxAnalyzer) {
    return ArkoiPsiImplUtil.getAnnotationCall(this, syntaxAnalyzer);
  }

  @Override
  @NotNull
  public List<ArgumentSyntaxAST> getAnnotationArguments(@NotNull SyntaxAnalyzer syntaxAnalyzer) {
    return ArkoiPsiImplUtil.getAnnotationArguments(this, syntaxAnalyzer);
  }

}
