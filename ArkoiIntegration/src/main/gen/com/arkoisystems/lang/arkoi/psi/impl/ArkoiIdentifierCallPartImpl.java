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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.FunctionCallPartSyntaxAST;

public class ArkoiIdentifierCallPartImpl extends ASTWrapperPsiElement implements ArkoiIdentifierCallPart {

  public ArkoiIdentifierCallPartImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ArkoiVisitor visitor) {
    visitor.visitIdentifierCallPart(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ArkoiVisitor) accept((ArkoiVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ArkoiFunctionCallPart getFunctionCallPart() {
    return findChildByClass(ArkoiFunctionCallPart.class);
  }

  @Override
  @NotNull
  public PsiElement getIdentifier() {
    return findNotNullChildByType(IDENTIFIER);
  }

  @Override
  @Nullable
  public FunctionCallPartSyntaxAST getCalledFunctionPart(@NotNull SyntaxAnalyzer syntaxAnalyzer) {
    return ArkoiPsiImplUtil.getCalledFunctionPart(this, syntaxAnalyzer);
  }

}
