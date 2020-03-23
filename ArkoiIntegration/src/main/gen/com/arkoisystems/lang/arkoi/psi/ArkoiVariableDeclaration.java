// This is a generated file. Not intended for manual editing.
package com.arkoisystems.lang.arkoi.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ArkoiVariableDeclaration extends PsiElement {

  @NotNull
  List<ArkoiAnnotationCall> getAnnotationCallList();

  @NotNull
  List<ArkoiCommentDeclaration> getCommentDeclarationList();

  @Nullable
  ArkoiInlinedBlock getInlinedBlock();

  @Nullable
  PsiElement getIdentifier();

  //WARNING: getName(...) is skipped
  //matching getName(ArkoiVariableDeclaration, ...)
  //methods are not found in ArkoiPsiImplUtil

}
