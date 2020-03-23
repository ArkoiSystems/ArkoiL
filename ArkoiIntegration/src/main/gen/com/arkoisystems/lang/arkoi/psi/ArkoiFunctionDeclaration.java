// This is a generated file. Not intended for manual editing.
package com.arkoisystems.lang.arkoi.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.AnnotationSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.ParameterSyntaxAST;

public interface ArkoiFunctionDeclaration extends PsiElement {

  @NotNull
  List<ArkoiAnnotationCall> getAnnotationCallList();

  @Nullable
  ArkoiBlockDeclaration getBlockDeclaration();

  @NotNull
  List<ArkoiCommentDeclaration> getCommentDeclarationList();

  @Nullable
  ArkoiParameterList getParameterList();

  @Nullable
  ArkoiPrimitives getPrimitives();

  @Nullable
  PsiElement getIdentifier();

  @Nullable
  IdentifierToken getFunctionName();

  @Nullable
  TypeSyntaxAST getFunctionReturnType(@NotNull SyntaxAnalyzer syntaxAnalyzer);

  @Nullable
  BlockSyntaxAST getFunctionBlock(@NotNull SyntaxAnalyzer syntaxAnalyzer);

  @NotNull
  List<ParameterSyntaxAST> getFunctionParameters(@NotNull SyntaxAnalyzer syntaxAnalyzer);

  @NotNull
  List<AnnotationSyntaxAST> getFunctionAnnotations(@NotNull SyntaxAnalyzer syntaxAnalyzer);

}
