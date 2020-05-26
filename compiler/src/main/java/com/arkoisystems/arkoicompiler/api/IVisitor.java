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

import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.FunctionNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ImportNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ReturnNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.VariableNode;
import org.jetbrains.annotations.NotNull;

public interface IVisitor<T>
{
    
    default T visit(final @NotNull ArkoiNode astNode) {
        if (astNode instanceof IdentifierCallNode)
            return this.visit((IdentifierCallNode) astNode);
        else if (astNode instanceof TypeNode)
            return this.visit((TypeNode) astNode);
        else if (astNode instanceof RootNode)
            return this.visit((RootNode) astNode);
        else if (astNode instanceof ParameterListNode)
            return this.visit((ParameterListNode) astNode);
        else if (astNode instanceof ParameterNode)
            return this.visit((ParameterNode) astNode);
        else if (astNode instanceof BlockNode)
            return this.visit((BlockNode) astNode);
        else if (astNode instanceof ArgumentListNode)
            return this.visit((ArgumentListNode) astNode);
        else if (astNode instanceof ArgumentNode)
            return this.visit((ArgumentNode) astNode);
        else if (astNode instanceof FunctionNode)
            return this.visit((FunctionNode) astNode);
        else if (astNode instanceof ImportNode)
            return this.visit((ImportNode) astNode);
        else if (astNode instanceof ReturnNode)
            return this.visit((ReturnNode) astNode);
        else if (astNode instanceof VariableNode)
            return this.visit((VariableNode) astNode);
        else if (astNode instanceof StringNode)
            return this.visit((StringNode) astNode);
        else if (astNode instanceof NumberNode)
            return this.visit((NumberNode) astNode);
        else if (astNode instanceof FunctionCallPartNode)
            return this.visit((FunctionCallPartNode) astNode);
        else if (astNode instanceof CollectionNode)
            return this.visit((CollectionNode) astNode);
        else if (astNode instanceof AssignmentExpressionNode)
            return this.visit((AssignmentExpressionNode) astNode);
        else if (astNode instanceof EqualityExpressionNode)
            return this.visit((EqualityExpressionNode) astNode);
        else if (astNode instanceof LogicalExpressionNode)
            return this.visit((LogicalExpressionNode) astNode);
        else if (astNode instanceof BinaryExpressionNode)
            return this.visit((BinaryExpressionNode) astNode);
        else if (astNode instanceof ParenthesizedExpressionNode)
            return this.visit((ParenthesizedExpressionNode) astNode);
        else if (astNode instanceof PostfixExpressionNode)
            return this.visit((PostfixExpressionNode) astNode);
        else if (astNode instanceof PrefixExpressionNode)
            return this.visit((PrefixExpressionNode) astNode);
        else if (astNode instanceof RelationalExpressionNode)
            return this.visit((RelationalExpressionNode) astNode);
        else
            throw new NullPointerException(astNode.getClass().getSimpleName() + ", " + astNode.getAstType().name());
    }
    
    T visit(final @NotNull TypeNode typeAST);
    
    T visit(final @NotNull RootNode rootAST);
    
    T visit(final @NotNull ParameterListNode parameterListAST);
    
    T visit(final @NotNull ParameterNode parameterAST);
    
    T visit(final @NotNull BlockNode blockAST);
    
    T visit(final @NotNull ArgumentListNode argumentListAST);
    
    T visit(final @NotNull ArgumentNode argumentAST);
    
    T visit(final @NotNull FunctionNode functionAST);
    
    T visit(final @NotNull ImportNode importAST);
    
    T visit(final @NotNull ReturnNode returnAST);
    
    T visit(final @NotNull VariableNode variableAST);
    
    T visit(final @NotNull StringNode stringAST);
    
    T visit(final @NotNull NumberNode numberAST);
    
    T visit(final @NotNull IdentifierCallNode identifierCallAST);
    
    T visit(final @NotNull FunctionCallPartNode functionCallPartAST);
    
    T visit(final @NotNull CollectionNode collectionAST);
    
    T visit(final @NotNull AssignmentExpressionNode assignmentExpressionAST);
    
    T visit(final @NotNull BinaryExpressionNode binaryExpressionAST);
    
    T visit(final @NotNull EqualityExpressionNode equalityExpressionAST);
    
    T visit(final @NotNull LogicalExpressionNode logicalExpressionAST);
    
    T visit(final @NotNull ParenthesizedExpressionNode parenthesizedExpressionAST);
    
    T visit(final @NotNull PostfixExpressionNode postfixExpressionAST);
    
    T visit(final @NotNull PrefixExpressionNode prefixExpressionAST);
    
    T visit(final @NotNull RelationalExpressionNode relationalExpressionAST);
    
}
