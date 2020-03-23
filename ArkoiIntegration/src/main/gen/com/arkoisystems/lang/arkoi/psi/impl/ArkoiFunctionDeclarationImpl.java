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
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.AnnotationSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.ParameterSyntaxAST;

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
  @NotNull
  public List<ArkoiAnnotationCall> getAnnotationCallList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ArkoiAnnotationCall.class);
  }

  @Override
  @Nullable
  public ArkoiBlockDeclaration getBlockDeclaration() {
    return findChildByClass(ArkoiBlockDeclaration.class);
  }

  @Override
  @NotNull
  public List<ArkoiCommentDeclaration> getCommentDeclarationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ArkoiCommentDeclaration.class);
  }

  @Override
  @Nullable
  public ArkoiParameterList getParameterList() {
    return findChildByClass(ArkoiParameterList.class);
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

  @Override
  @Nullable
  public IdentifierToken getFunctionName() {
    return ArkoiPsiImplUtil.getFunctionName(this);
  }

  @Override
  @Nullable
  public TypeSyntaxAST getFunctionReturnType(@NotNull SyntaxAnalyzer syntaxAnalyzer) {
    return ArkoiPsiImplUtil.getFunctionReturnType(this, syntaxAnalyzer);
  }

  @Override
  @Nullable
  public BlockSyntaxAST getFunctionBlock(@NotNull SyntaxAnalyzer syntaxAnalyzer) {
    return ArkoiPsiImplUtil.getFunctionBlock(this, syntaxAnalyzer);
  }

  @Override
  @NotNull
  public List<ParameterSyntaxAST> getFunctionParameters(@NotNull SyntaxAnalyzer syntaxAnalyzer) {
    return ArkoiPsiImplUtil.getFunctionParameters(this, syntaxAnalyzer);
  }

  @Override
  @NotNull
  public List<AnnotationSyntaxAST> getFunctionAnnotations(@NotNull SyntaxAnalyzer syntaxAnalyzer) {
    return ArkoiPsiImplUtil.getFunctionAnnotations(this, syntaxAnalyzer);
  }

}
