// This is a generated file. Not intended for manual editing.
package com.arkoisystems.lang.arkoi.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.FunctionCallPartSyntaxAST;

public interface ArkoiIdentifierCallPart extends PsiElement {

  @Nullable
  ArkoiFunctionCallPart getFunctionCallPart();

  @NotNull
  PsiElement getIdentifier();

  @Nullable
  FunctionCallPartSyntaxAST getCalledFunctionPart(@NotNull SyntaxAnalyzer syntaxAnalyzer);

  //WARNING: getName(...) is skipped
  //matching getName(ArkoiIdentifierCallPart, ...)
  //methods are not found in ArkoiPsiImplUtil

}
