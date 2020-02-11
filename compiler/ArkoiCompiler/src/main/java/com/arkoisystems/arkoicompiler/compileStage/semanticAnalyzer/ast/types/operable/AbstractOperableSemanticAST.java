package com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.types.operable;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.DoubleError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.SemanticASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

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
@Setter
@Getter
public class AbstractOperableSemanticAST<T1 extends AbstractSyntaxAST, T2> extends AbstractSemanticAST<T1>
{
    
    @Expose
    private T2 operableObject;
    
    public AbstractOperableSemanticAST(final AbstractSemanticAST<?> lastContainerAST, final T1 syntaxAST, final ASTType astType) {
        super(lastContainerAST, syntaxAST, astType);
    }
    
    @Override
    public AbstractSemanticAST<?> analyse(final SemanticAnalyzer semanticAnalyzer) {
        return null;
    }
    
    public TypeSyntaxAST.TypeKind binAdd(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "Addition isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind binSub(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "Subtraction isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind binMul(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "Multiplication isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind binDiv(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "Division isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind binMod(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "Modulo isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind assign(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "Assignment isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind addAssign(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "Addition assignment isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind subAssign(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "Subtraction assignment isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind mulAssign(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "Multiplication assignment isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind divAssign(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "Division assignment isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind modAssign(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "Modulo assignment isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind equal(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "The \"equal\" operation isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind notEqual(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "The \"not equal\" operation isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind logicalOr(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "The \"logical or\" operation isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind logicalAnd(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "The \"logical and\" operation isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind postfixAdd(final SemanticAnalyzer semanticAnalyzer) {
        semanticAnalyzer.errorHandler().addError(new SemanticASTError<>(this, "The \"post addition\" operator isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind postfixSub(final SemanticAnalyzer semanticAnalyzer) {
        semanticAnalyzer.errorHandler().addError(new SemanticASTError<>(this, "The \"post subtraction\" operator isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind prefixAdd(final SemanticAnalyzer semanticAnalyzer) {
        semanticAnalyzer.errorHandler().addError(new SemanticASTError<>(this, "The \"pre addition\" operator isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind prefixSub(final SemanticAnalyzer semanticAnalyzer) {
        semanticAnalyzer.errorHandler().addError(new SemanticASTError<>(this, "The \"pre subtraction\" operator isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind prefixNegate(final SemanticAnalyzer semanticAnalyzer) {
        semanticAnalyzer.errorHandler().addError(new SemanticASTError<>(this, "The \"negate\" operator isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind prefixAffirm(final SemanticAnalyzer semanticAnalyzer) {
        semanticAnalyzer.errorHandler().addError(new SemanticASTError<>(this, "The \"affirm\" operator isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind relationalLessThan(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new SemanticASTError<>(this, "The \"less than\" operator isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind relationalGreaterThan(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "The \"greater than\" operator isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind relationalLessEqualThan(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "The \"less equal than\" operator isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind relationalGreaterEqualThan(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "The \"greater equal than\" operator isn't supported by this operable."));
        return null;
    }
    
    public TypeSyntaxAST.TypeKind relationalIs(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new DoubleError<>(this, rightSideOperable, this.getStart(), rightSideOperable.getEnd(), "The \"is\" keyword isn't supported by this operable."));
        return null;
    }
    
}
