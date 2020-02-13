package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.NumberOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.BinaryExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ParenthesizedExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.PrefixExpressionSyntaxAST;
import com.google.gson.annotations.Expose;
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
public class BinaryExpressionSemanticAST extends AbstractExpressionSemanticAST<BinaryExpressionSyntaxAST>
{
    
    @Expose
    private AbstractOperableSemanticAST<?, ?> leftSideOperable, rightSideOperable;
    
    private TypeSyntaxAST.TypeKind operableObject;
    
    public BinaryExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final BinaryExpressionSyntaxAST binaryExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, binaryExpressionSyntaxAST, ASTType.BINARY_EXPRESSION);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind getOperableObject() {
        if (this.operableObject == null) {
            if (this.getLeftSideOperable() == null)
                return null;
            if (this.getRightSideOperable() == null)
                return null;
            
            final TypeSyntaxAST.TypeKind typeKind = TypeSyntaxAST.TypeKind.combineKinds(this.getLeftSideOperable(), this.getRightSideOperable());
            if (typeKind == null) {
                this.getSemanticAnalyzer().errorHandler().addError(new SyntaxASTError<>(this.getSyntaxAST(), "Couldn't analyze this binary expression because the return type is not resolvable."));
                return null;
            }
            return (this.operableObject = typeKind);
        }
        return this.operableObject;
    }
    
    public AbstractOperableSemanticAST<?, ?> getLeftSideOperable() {
        if (this.leftSideOperable == null)
            return (this.leftSideOperable = this.analyzeOperable(this.getSyntaxAST().getLeftSideOperable()));
        return this.leftSideOperable;
    }
    
    public AbstractOperableSemanticAST<?, ?> getRightSideOperable() {
        if (this.rightSideOperable == null)
            return (this.rightSideOperable = this.analyzeOperable(this.getSyntaxAST().getRightSideOperable()));
        return this.rightSideOperable;
    }
    
    private AbstractOperableSemanticAST<?, ?> analyzeOperable(final AbstractOperableSyntaxAST<?> abstractOperableSyntaxAST) {
        if (abstractOperableSyntaxAST instanceof ParenthesizedExpressionSyntaxAST) {
            final ParenthesizedExpressionSyntaxAST parenthesizedExpressionSyntaxAST = (ParenthesizedExpressionSyntaxAST) abstractOperableSyntaxAST;
            final ParenthesizedExpressionSemanticAST parenthesizedExpressionSemanticAST
                    = new ParenthesizedExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), parenthesizedExpressionSyntaxAST);
            
            if (parenthesizedExpressionSemanticAST.getOperableObject() == null)
                return null;
            return parenthesizedExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof NumberOperableSyntaxAST) {
            final NumberOperableSyntaxAST numberOperableSyntaxAST = (NumberOperableSyntaxAST) abstractOperableSyntaxAST;
            final NumberOperableSemanticAST numberOperableSemanticAST
                    = new NumberOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), numberOperableSyntaxAST);
            
            if (numberOperableSemanticAST.getOperableObject() == null)
                return null;
            return numberOperableSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof BinaryExpressionSyntaxAST) {
            final BinaryExpressionSyntaxAST binaryExpressionSyntaxAST = (BinaryExpressionSyntaxAST) abstractOperableSyntaxAST;
            final BinaryExpressionSemanticAST binaryExpressionSemanticAST
                    = new BinaryExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), binaryExpressionSyntaxAST);
            
            if (binaryExpressionSemanticAST.getOperableObject() == null)
                return null;
            return binaryExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof PrefixExpressionSyntaxAST) {
            final PrefixExpressionSyntaxAST prefixExpressionSyntaxAST = (PrefixExpressionSyntaxAST) abstractOperableSyntaxAST;
            final PrefixExpressionSemanticAST prefixExpressionSemanticAST
                    = new PrefixExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), prefixExpressionSyntaxAST);
            
            if (prefixExpressionSemanticAST.getOperableObject() == null)
                return null;
            return prefixExpressionSemanticAST;
        } else {
            this.getSemanticAnalyzer().errorHandler().addError(new SyntaxASTError<>(abstractOperableSyntaxAST, "Couldn't analyze this operable because it isn't supported by the binary expression."));
            return null;
        }
    }
    
    @Override
    public TypeSyntaxAST.TypeKind binAdd(SemanticAnalyzer semanticAnalyzer, AbstractOperableSyntaxAST<?> rightSideOperable) {
        return super.binAdd(semanticAnalyzer, rightSideOperable);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind binSub(SemanticAnalyzer semanticAnalyzer, AbstractOperableSyntaxAST<?> rightSideOperable) {
        return super.binSub(semanticAnalyzer, rightSideOperable);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind binMul(SemanticAnalyzer semanticAnalyzer, AbstractOperableSyntaxAST<?> rightSideOperable) {
        return super.binMul(semanticAnalyzer, rightSideOperable);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind binDiv(SemanticAnalyzer semanticAnalyzer, AbstractOperableSyntaxAST<?> rightSideOperable) {
        return super.binDiv(semanticAnalyzer, rightSideOperable);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind binMod(SemanticAnalyzer semanticAnalyzer, AbstractOperableSyntaxAST<?> rightSideOperable) {
        return super.binMod(semanticAnalyzer, rightSideOperable);
    }
    
}
