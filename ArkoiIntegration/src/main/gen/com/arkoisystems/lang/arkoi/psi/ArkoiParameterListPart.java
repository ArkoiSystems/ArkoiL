// This is a generated file. Not intended for manual editing.
package com.arkoisystems.lang.arkoi.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;

public interface ArkoiParameterListPart extends PsiElement {

  @Nullable
  ArkoiPrimitives getPrimitives();

  @NotNull
  PsiElement getIdentifier();

  @NotNull
  IdentifierToken getParameterName();

}
