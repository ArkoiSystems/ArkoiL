// This is a generated file. Not intended for manual editing.
package com.arkoisystems.lang.arkoi.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;

public interface ArkoiPrimitives extends PsiElement {

  @NotNull
  TypeSyntaxAST getPrimitiveReturnType(@NotNull SyntaxAnalyzer syntaxAnalyzer);

}
