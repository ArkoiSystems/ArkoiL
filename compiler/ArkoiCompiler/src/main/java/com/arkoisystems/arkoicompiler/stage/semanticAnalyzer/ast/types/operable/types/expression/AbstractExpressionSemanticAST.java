package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.DoubleError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SemanticASTError;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka timo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
public class AbstractExpressionSemanticAST<T extends AbstractSyntaxAST> extends AbstractOperableSemanticAST<T, TypeSyntaxAST.TypeKind>
{
    
    public AbstractExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final T syntaxAST, final ASTType astType) {
        super(semanticAnalyzer, lastContainerAST, syntaxAST, astType);
    }
    
    public TypeSyntaxAST.TypeKind binAdd(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "Addition isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind binSub(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "Subtraction isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind binMul(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "Multiplication isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind binDiv(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "Division isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind binMod(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "Modulo isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind assign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "Assignment isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind addAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "Addition assignment isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind subAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "Subtraction assignment isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind mulAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "Multiplication assignment isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind divAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "Division assignment isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind modAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "Modulo assignment isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind equal(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "The \"equal\" operation isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind notEqual(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "The \"not equal\" operation isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind logicalOr(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "The \"logical or\" operation isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind logicalAnd(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "The \"logical and\" operation isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind postfixAdd(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(abstractOperableSemanticAST, "The \"post addition\" operator isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind postfixSub(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(abstractOperableSemanticAST, "The \"post subtraction\" operator isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind prefixAdd(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(abstractOperableSemanticAST, "The \"pre addition\" operator isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind prefixSub(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(abstractOperableSemanticAST, "The \"pre subtraction\" operator isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind prefixNegate(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(abstractOperableSemanticAST, "The \"negate\" operator isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind prefixAffirm(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(abstractOperableSemanticAST, "The \"affirm\" operator isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind relationalLessThan(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "The \"less than\" operator isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind relationalGreaterThan(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "The \"greater than\" operator isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind relationalLessEqualThan(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "The \"less equal than\" operator isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind relationalGreaterEqualThan(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "The \"greater equal than\" operator isn't supported by these operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind relationalIs(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new DoubleError<>(leftSideOperable, rightSideOperable, leftSideOperable.getStart(), rightSideOperable.getEnd(), "The \"is\" keyword isn't supported by these operable."));
        return null;
    }
    
}
