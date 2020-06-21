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
package com.arkoisystems.compiler.visitor;

import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.BlockNode;
import com.arkoisystems.compiler.phases.parser.ast.types.RootNode;
import com.arkoisystems.compiler.phases.parser.ast.types.StructNode;
import com.arkoisystems.compiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.compiler.phases.parser.ast.types.argument.ArgumentListNode;
import com.arkoisystems.compiler.phases.parser.ast.types.argument.ArgumentNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.NumberNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.StringNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.ExpressionListNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.AssignmentNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.BinaryNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.ParenthesizedNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.PrefixNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.IdentifierNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types.AssignNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types.FunctionCallNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types.StructCreateNode;
import com.arkoisystems.compiler.phases.parser.ast.types.parameter.ParameterListNode;
import com.arkoisystems.compiler.phases.parser.ast.types.parameter.ParameterNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.FunctionNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.ImportNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.ReturnNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.VariableNode;
import org.jetbrains.annotations.NotNull;

public interface IVisitor<T>
{
    
    default T visit(@NotNull final ParserNode astNode) {
        if (astNode instanceof ArgumentNode)
            return this.visit((ArgumentNode) astNode);
        else if (astNode instanceof ArgumentListNode)
            return this.visit((ArgumentListNode) astNode);
        else if (astNode instanceof FunctionCallNode)
            return this.visit((FunctionCallNode) astNode);
        else if (astNode instanceof AssignNode)
            return this.visit((AssignNode) astNode);
        else if (astNode instanceof StructCreateNode)
            return this.visit((StructCreateNode) astNode);
        else if (astNode instanceof IdentifierNode)
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
        else if (astNode instanceof PrefixNode)
            return this.visit((PrefixNode) astNode);
        else if (astNode instanceof StructNode)
            return this.visit((StructNode) astNode);
    
        throw new NullPointerException(astNode.getClass().getSimpleName());
    }
    
    T visit(@NotNull final StructNode structNode);
    
    T visit(@NotNull final TypeNode typeNode);
    
    T visit(@NotNull final RootNode rootNode);
    
    T visit(@NotNull final ParameterListNode parameterListNode);
    
    T visit(@NotNull final ParameterNode parameter);
    
    T visit(@NotNull final ArgumentListNode argumentListNode);
    
    T visit(@NotNull final ArgumentNode argumentNode);
    
    T visit(@NotNull final BlockNode blockNode);
    
    T visit(@NotNull final FunctionNode functionNode);
    
    T visit(@NotNull final ImportNode importNode);
    
    T visit(@NotNull final ReturnNode returnNode);
    
    T visit(@NotNull final VariableNode variableNode);
    
    T visit(@NotNull final StringNode stringNode);
    
    T visit(@NotNull final NumberNode numberNode);
    
    T visit(@NotNull final IdentifierNode identifierNode);
    
    T visit(@NotNull final FunctionCallNode functionCallNode);
    
    T visit(@NotNull final AssignNode assignNode);
    
    T visit(@NotNull final StructCreateNode structCreateNode);
    
    T visit(@NotNull final ExpressionListNode expressionListNode);
    
    T visit(@NotNull final AssignmentNode assignmentNode);
    
    T visit(@NotNull final BinaryNode binaryNode);
    
    T visit(@NotNull final ParenthesizedNode parenthesizedNode);
    
    T visit(@NotNull final PrefixNode prefixNode);
    
}
