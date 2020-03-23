// This is a generated file. Not intended for manual editing.
package com.arkoisystems.lang.arkoi.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ReturnStatementSyntaxAST;

public interface ArkoiReturnDeclaration extends PsiElement {

  @Nullable
  ArkoiExpression getExpression();

  @NotNull
  ReturnStatementSyntaxAST getReturnDeclaration(@NotNull SyntaxAnalyzer syntaxAnalyzer);

}
