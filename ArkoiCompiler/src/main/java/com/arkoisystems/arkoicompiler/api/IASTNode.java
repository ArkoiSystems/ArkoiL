/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on April 06, 2020
 * Author єхcsє#5543 aka timo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arkoisystems.arkoicompiler.api;

import com.arkoisystems.arkoicompiler.ArkoiError;
import com.arkoisystems.arkoicompiler.api.utils.IFailed;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.MarkerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IASTNode extends IFailed
{
    
    @NotNull
    MarkerFactory<? extends IASTNode, IToken, IToken> getMarkerFactory();
    
    
    @Nullable
    SyntaxAnalyzer getSyntaxAnalyzer();
    
    
    @Nullable
    ArkoiError.ErrorPosition.LineRange getLineRange();
    
    
    @Nullable
    IToken getStartToken();
    
    
    @Nullable
    IToken getEndToken();
    
    
    @NotNull
    ASTType getAstType();
    
    
    @NotNull
    TypeKind getTypeKind();
    
    
    @NotNull
    IASTNode parseAST(@NotNull final IASTNode parentAST);
    
    
    void accept(@NotNull final IVisitor<?> visitor);
    
    
    <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final IASTNode[] astNodes, @NotNull final String message, @NotNull final Object... arguments);
    
    
    <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final IASTNode astNode, @NotNull final String message, @NotNull final Object... arguments);
    
    
    <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final ArkoiToken arkoiToken, @NotNull final String message, @NotNull final Object... arguments);
    
}
