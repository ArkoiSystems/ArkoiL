// This is a generated file. Not intended for manual editing.
package com.arkoisystems.lang.arkoi.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ArkoiBraceBlock extends PsiElement {

  @NotNull
  List<ArkoiFunctionInvoke> getFunctionInvokeList();

  @NotNull
  List<ArkoiReturnDeclaration> getReturnDeclarationList();

  @NotNull
  List<ArkoiVariableDeclaration> getVariableDeclarationList();

  @NotNull
  List<ArkoiVariableInvoke> getVariableInvokeList();

}
