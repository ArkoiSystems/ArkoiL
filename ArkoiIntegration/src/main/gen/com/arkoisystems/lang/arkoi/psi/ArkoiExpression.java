// This is a generated file. Not intended for manual editing.
package com.arkoisystems.lang.arkoi.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;

public interface ArkoiExpression extends PsiElement {

  @NotNull
  ArkoiAssignmentExpression getAssignmentExpression();

  @NotNull
  ExpressionSyntaxAST getExpression(@NotNull SyntaxAnalyzer syntaxAnalyzer);

}
