// This is a generated file. Not intended for manual editing.
package com.arkoisystems.lang.arkoi.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.ArgumentSyntaxAST;

public interface ArkoiAnnotationCall extends PsiElement {

  @Nullable
  ArkoiArgumentList getArgumentList();

  @Nullable
  ArkoiIdentifierCall getIdentifierCall();

  @Nullable
  IdentifierCallOperableSyntaxAST getAnnotationCall(@NotNull SyntaxAnalyzer syntaxAnalyzer);

  @NotNull
  List<ArgumentSyntaxAST> getAnnotationArguments(@NotNull SyntaxAnalyzer syntaxAnalyzer);

}
