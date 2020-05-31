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

import com.arkoisystems.arkoicompiler.phases.parser.ast.ParserNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.BlockNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.RootNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.IdentifierNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.NumberNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.StringNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.expression.ExpressionListNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.parameter.ParameterNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.parameter.ParameterListNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.types.FunctionNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.types.ImportNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.types.ReturnNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.types.VariableNode;
import org.jetbrains.annotations.NotNull;

public interface IVisitor<T>
{
    
    default T visit(final @NotNull ParserNode astNode) {
        if (astNode instanceof IdentifierNode)
            return this.visit((IdentifierNode) astNode);
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
        else if (astNode instanceof ExpressionListNode)
            return this.visit((ExpressionListNode) astNode);
        else if (astNode instanceof AssignmentNode)
            return this.visit((AssignmentNode) astNode);
        else if (astNode instanceof BinaryNode)
            return this.visit((BinaryNode) astNode);
        else if (astNode instanceof ParenthesizedNode)
            return this.visit((ParenthesizedNode) astNode);
        else if (astNode instanceof PostfixNode)
            return this.visit((PostfixNode) astNode);
        else if (astNode instanceof PrefixNode)
            return this.visit((PrefixNode) astNode);
        
        throw new NullPointerException(astNode.getClass().getSimpleName() + ", " + astNode.getClass().getSimpleName());
    }
    
    T visit(final @NotNull TypeNode typeNode);
    
    T visit(final @NotNull RootNode rootNode);
    
    T visit(final @NotNull ParameterListNode parameterListNode);
    
    T visit(final @NotNull ParameterNode parameter);
    
    T visit(final @NotNull BlockNode blockNode);
    
    T visit(final @NotNull FunctionNode functionNode);
    
    T visit(final @NotNull ImportNode importNode);
    
    T visit(final @NotNull ReturnNode returnNode);
    
    T visit(final @NotNull VariableNode variableNode);
    
    T visit(final @NotNull StringNode stringNode);
    
    T visit(final @NotNull NumberNode numberNode);
    
    T visit(final @NotNull IdentifierNode identifierNode);
    
    T visit(final @NotNull ExpressionListNode expressionListNode);
    
    T visit(final @NotNull AssignmentNode assignmentNode);
    
    T visit(final @NotNull BinaryNode binaryNode);
    
    T visit(final @NotNull ParenthesizedNode parenthesizedNode);
    
    T visit(final @NotNull PostfixNode postfixNode);
    
    T visit(final @NotNull PrefixNode prefixNode);
    
}
