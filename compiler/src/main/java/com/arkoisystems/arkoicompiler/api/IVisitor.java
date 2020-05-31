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

import com.arkoisystems.arkoicompiler.stages.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.*;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.argument.Argument;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.argument.ArgumentList;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.NumberOperable;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.StringOperable;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.expression.ExpressionList;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.parameter.Parameter;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.parameter.ParameterList;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.statement.types.FunctionStatement;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.statement.types.ImportStatement;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.statement.types.ReturnStatement;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.statement.types.VariableStatement;
import org.jetbrains.annotations.NotNull;

public interface IVisitor<T>
{
    
    default T visit(final @NotNull ArkoiNode astNode) {
        if (astNode instanceof IdentifierOperable)
            return this.visit((IdentifierOperable) astNode);
        else if (astNode instanceof Type)
            return this.visit((Type) astNode);
        else if (astNode instanceof Root)
            return this.visit((Root) astNode);
        else if (astNode instanceof ParameterList)
            return this.visit((ParameterList) astNode);
        else if (astNode instanceof Parameter)
            return this.visit((Parameter) astNode);
        else if (astNode instanceof Block)
            return this.visit((Block) astNode);
        else if (astNode instanceof ArgumentList)
            return this.visit((ArgumentList) astNode);
        else if (astNode instanceof Argument)
            return this.visit((Argument) astNode);
        else if (astNode instanceof FunctionStatement)
            return this.visit((FunctionStatement) astNode);
        else if (astNode instanceof ImportStatement)
            return this.visit((ImportStatement) astNode);
        else if (astNode instanceof ReturnStatement)
            return this.visit((ReturnStatement) astNode);
        else if (astNode instanceof VariableStatement)
            return this.visit((VariableStatement) astNode);
        else if (astNode instanceof StringOperable)
            return this.visit((StringOperable) astNode);
        else if (astNode instanceof NumberOperable)
            return this.visit((NumberOperable) astNode);
        else if (astNode instanceof CollectionOperable)
            return this.visit((CollectionOperable) astNode);
        else if (astNode instanceof ExpressionList)
            return this.visit((ExpressionList) astNode);
        else if (astNode instanceof AssignmentExpression)
            return this.visit((AssignmentExpression) astNode);
        else if (astNode instanceof EqualityExpression)
            return this.visit((EqualityExpression) astNode);
        else if (astNode instanceof LogicalExpression)
            return this.visit((LogicalExpression) astNode);
        else if (astNode instanceof BinaryExpression)
            return this.visit((BinaryExpression) astNode);
        else if (astNode instanceof ParenthesizedExpression)
            return this.visit((ParenthesizedExpression) astNode);
        else if (astNode instanceof PostfixExpression)
            return this.visit((PostfixExpression) astNode);
        else if (astNode instanceof PrefixExpression)
            return this.visit((PrefixExpression) astNode);
        else if (astNode instanceof RelationalExpression)
            return this.visit((RelationalExpression) astNode);
        else
            throw new NullPointerException(astNode.getClass().getSimpleName() + ", " + astNode.getAstType().name());
    }
    
    T visit(final @NotNull Type type);
    
    T visit(final @NotNull Root root);
    
    T visit(final @NotNull ParameterList parameterList);
    
    T visit(final @NotNull Parameter parameter);
    
    T visit(final @NotNull Block block);
    
    T visit(final @NotNull ArgumentList argumentList);
    
    T visit(final @NotNull Argument argument);
    
    T visit(final @NotNull FunctionStatement functionStatement);
    
    T visit(final @NotNull ImportStatement importStatement);
    
    T visit(final @NotNull ReturnStatement returnStatement);
    
    T visit(final @NotNull VariableStatement variableStatement);
    
    T visit(final @NotNull StringOperable stringOperable);
    
    T visit(final @NotNull NumberOperable numberOperable);
    
    T visit(final @NotNull IdentifierOperable identifierOperable);
    
    T visit(final @NotNull CollectionOperable collectionOperable);
    
    T visit(final @NotNull ExpressionList expressionList);
    
    T visit(final @NotNull AssignmentExpression assignmentExpression);
    
    T visit(final @NotNull BinaryExpression binaryExpression);
    
    T visit(final @NotNull EqualityExpression equalityExpression);
    
    T visit(final @NotNull LogicalExpression logicalExpression);
    
    T visit(final @NotNull ParenthesizedExpression parenthesizedExpression);
    
    T visit(final @NotNull PostfixExpression postfixExpression);
    
    T visit(final @NotNull PrefixExpression prefixExpression);
    
    T visit(final @NotNull RelationalExpression relationalExpression);
    
}
