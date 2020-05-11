/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on April 10, 2020
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

import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ReturnAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableAST;
import org.jetbrains.annotations.NotNull;

public interface IVisitor<T>
{
    
    default T visit(@NotNull final IASTNode astNode) {
        if (astNode instanceof IdentifierCallAST)
            return this.visit((IdentifierCallAST) astNode);
        else if (astNode instanceof TypeAST)
            return this.visit((TypeAST) astNode);
        else if (astNode instanceof RootAST)
            return this.visit((RootAST) astNode);
        else if (astNode instanceof ParameterListAST)
            return this.visit((ParameterListAST) astNode);
        else if (astNode instanceof ParameterAST)
            return this.visit((ParameterAST) astNode);
        else if (astNode instanceof BlockAST)
            return this.visit((BlockAST) astNode);
        else if (astNode instanceof ArgumentListAST)
            return this.visit((ArgumentListAST) astNode);
        else if (astNode instanceof ArgumentAST)
            return this.visit((ArgumentAST) astNode);
        else if (astNode instanceof AnnotationAST)
            return this.visit((AnnotationAST) astNode);
        else if (astNode instanceof FunctionAST)
            return this.visit((FunctionAST) astNode);
        else if (astNode instanceof ImportAST)
            return this.visit((ImportAST) astNode);
        else if (astNode instanceof ReturnAST)
            return this.visit((ReturnAST) astNode);
        else if (astNode instanceof VariableAST)
            return this.visit((VariableAST) astNode);
        else if (astNode instanceof StringAST)
            return this.visit((StringAST) astNode);
        else if (astNode instanceof NumberAST)
            return this.visit((NumberAST) astNode);
        else if (astNode instanceof FunctionCallPartAST)
            return this.visit((FunctionCallPartAST) astNode);
        else if (astNode instanceof CollectionAST)
            return this.visit((CollectionAST) astNode);
        else if (astNode instanceof AssignmentExpressionAST)
            return this.visit((AssignmentExpressionAST) astNode);
        else if (astNode instanceof CastExpressionAST)
            return this.visit((CastExpressionAST) astNode);
        else if (astNode instanceof EqualityExpressionAST)
            return this.visit((EqualityExpressionAST) astNode);
        else if (astNode instanceof LogicalExpressionAST)
            return this.visit((LogicalExpressionAST) astNode);
        else if (astNode instanceof BinaryExpressionAST)
            return this.visit((BinaryExpressionAST) astNode);
        else if (astNode instanceof ParenthesizedExpressionAST)
            return this.visit((ParenthesizedExpressionAST) astNode);
        else if (astNode instanceof PostfixExpressionAST)
            return this.visit((PostfixExpressionAST) astNode);
        else if (astNode instanceof PrefixExpressionAST)
            return this.visit((PrefixExpressionAST) astNode);
        else if (astNode instanceof RelationalExpressionAST)
            return this.visit((RelationalExpressionAST) astNode);
        else
            throw new NullPointerException(astNode.getClass().getSimpleName() + ", " + astNode.getAstType().name());
    }
    
    T visit(@NotNull final TypeAST typeAST);
    
    T visit(@NotNull final RootAST rootAST);
    
    T visit(@NotNull final ParameterListAST parameterListAST);
    
    T visit(@NotNull final ParameterAST parameterAST);
    
    T visit(@NotNull final BlockAST blockAST);
    
    T visit(@NotNull final ArgumentListAST argumentListAST);
    
    T visit(@NotNull final ArgumentAST argumentAST);
    
    T visit(@NotNull final AnnotationAST annotationAST);
    
    T visit(@NotNull final FunctionAST functionAST);
    
    T visit(@NotNull final ImportAST importAST);
    
    T visit(@NotNull final ReturnAST returnAST);
    
    T visit(@NotNull final VariableAST variableAST);
    
    T visit(@NotNull final StringAST stringAST);
    
    T visit(@NotNull final NumberAST numberAST);
    
    T visit(@NotNull final IdentifierCallAST identifierCallAST);
    
    T visit(@NotNull final FunctionCallPartAST functionCallPartAST);
    
    T visit(@NotNull final CollectionAST collectionAST);
    
    T visit(@NotNull final AssignmentExpressionAST assignmentExpressionAST);
    
    T visit(@NotNull final BinaryExpressionAST binaryExpressionAST);
    
    T visit(@NotNull final CastExpressionAST castExpressionAST);
    
    T visit(@NotNull final EqualityExpressionAST equalityExpressionAST);
    
    T visit(@NotNull final LogicalExpressionAST logicalExpressionAST);
    
    T visit(@NotNull final ParenthesizedExpressionAST parenthesizedExpressionAST);
    
    T visit(@NotNull final PostfixExpressionAST postfixExpressionAST);
    
    T visit(@NotNull final PrefixExpressionAST prefixExpressionAST);
    
    T visit(@NotNull final RelationalExpressionAST relationalExpressionAST);
    
}
