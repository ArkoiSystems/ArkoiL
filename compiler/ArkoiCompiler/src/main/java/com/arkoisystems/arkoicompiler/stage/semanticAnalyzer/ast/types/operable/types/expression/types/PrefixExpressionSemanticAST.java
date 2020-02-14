package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SemanticASTError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.FunctionInvokeOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.IdentifierCallOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.IdentifierInvokeOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.NumberOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberOperableSyntaxAST;
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
public class PrefixExpressionSemanticAST extends AbstractExpressionSemanticAST<PrefixExpressionSyntaxAST>
{
    
    @Expose
    private AbstractOperableSemanticAST<?, ?> rightSideOperable;
    
    private TypeSyntaxAST.TypeKind expressionType;
    
    public PrefixExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final PrefixExpressionSyntaxAST prefixExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, prefixExpressionSyntaxAST, ASTType.PREFIX_EXPRESSION);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind getExpressionType() {
        if (this.expressionType == null) {
            if (this.getRightSideOperable() == null)
                return null;
            if (this.getPrefixUnaryOperator() == null)
                return null;
            
            final TypeSyntaxAST.TypeKind typeKind;
            switch (this.getPrefixUnaryOperator()) {
                case AFFIRM:
                    typeKind = this.prefixAffirm(this.getRightSideOperable());
                    break;
                case NEGATE:
                    typeKind = this.prefixNegate(this.getRightSideOperable());
                    break;
                case PREFIX_ADD:
                    typeKind = this.prefixAdd(this.getRightSideOperable());
                    break;
                case PREFIX_SUB:
                    typeKind = this.prefixSub(this.getRightSideOperable());
                    break;
                default:
                    typeKind = null;
                    break;
            }
            return (this.expressionType = typeKind);
        }
        return this.expressionType;
    }
    
    public PrefixExpressionSyntaxAST.PrefixUnaryOperator getPrefixUnaryOperator() {
        return this.getSyntaxAST().getPrefixUnaryOperator();
    }
    
    public AbstractOperableSemanticAST<?, ?> getRightSideOperable() {
        if (this.rightSideOperable == null)
            return (this.rightSideOperable = this.analyzeOperable(this.getSyntaxAST().getRightSideOperable()));
        return this.rightSideOperable;
    }
    
    private AbstractOperableSemanticAST<?, ?> analyzeOperable(final AbstractOperableSyntaxAST<?> abstractOperableSyntaxAST) {
        if (abstractOperableSyntaxAST instanceof NumberOperableSyntaxAST) {
            final NumberOperableSyntaxAST numberOperableSyntaxAST = (NumberOperableSyntaxAST) abstractOperableSyntaxAST;
            final NumberOperableSemanticAST numberOperableSemanticAST
                    = new NumberOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), numberOperableSyntaxAST);
            
            if (numberOperableSemanticAST.getExpressionType() == null)
                return null;
            return numberOperableSemanticAST;
        } else {
            this.getSemanticAnalyzer().errorHandler().addError(new SyntaxASTError<>(abstractOperableSyntaxAST, "Couldn't analyze this operable because it isn't supported by the binary expression."));
            return null;
        }
    }
    
    @Override
    public TypeSyntaxAST.TypeKind prefixAdd(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        return super.prefixAdd(abstractOperableSemanticAST);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind prefixSub(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        return super.prefixSub(abstractOperableSemanticAST);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind prefixNegate(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        final AbstractOperableSemanticAST<?, ?> rightExpressionOperable = this.analyzeNumericOperable(abstractOperableSemanticAST);
        if (rightExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(abstractOperableSemanticAST, "Couldn't analyze the prefix expression because the prefix negate operation doesn't support this operable."));
            return null;
        }
        return TypeSyntaxAST.TypeKind.getTypeKind(rightExpressionOperable);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind prefixAffirm(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        final AbstractOperableSemanticAST<?, ?> rightExpressionOperable = this.analyzeNumericOperable(abstractOperableSemanticAST);
        if (rightExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(abstractOperableSemanticAST, "Couldn't analyze the prefix expression because the prefix affirm operation doesn't support this operable."));
            return null;
        }
        return TypeSyntaxAST.TypeKind.getTypeKind(rightExpressionOperable);
    }
    
    private AbstractOperableSemanticAST<?, ?> analyzeNumericOperable(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        if (abstractOperableSemanticAST instanceof ParenthesizedExpressionSemanticAST) {
            final ParenthesizedExpressionSemanticAST parenthesizedExpressionSemanticAST = (ParenthesizedExpressionSemanticAST) abstractOperableSemanticAST;
            if (parenthesizedExpressionSemanticAST.getExpressionType() == null)
                return null;
            
            switch (parenthesizedExpressionSemanticAST.getExpressionType()) {
                case BYTE:
                case FLOAT:
                case DOUBLE:
                case INTEGER:
                case SHORT:
                    return parenthesizedExpressionSemanticAST;
                default:
                    return null;
            }
        } else if (abstractOperableSemanticAST instanceof FunctionInvokeOperableSemanticAST) {
            final FunctionInvokeOperableSemanticAST functionInvokeOperableSemanticAST = (FunctionInvokeOperableSemanticAST) abstractOperableSemanticAST;
            if (functionInvokeOperableSemanticAST.getExpressionType() == null)
                return null;
            
            switch (functionInvokeOperableSemanticAST.getExpressionType()) {
                case BYTE:
                case FLOAT:
                case DOUBLE:
                case INTEGER:
                case SHORT:
                    return functionInvokeOperableSemanticAST;
                default:
                    return null;
            }
        } else if (abstractOperableSemanticAST instanceof IdentifierCallOperableSemanticAST) {
            final IdentifierCallOperableSemanticAST identifierCallOperableSemanticAST = (IdentifierCallOperableSemanticAST) abstractOperableSemanticAST;
            if (identifierCallOperableSemanticAST.getExpressionType() == null)
                return null;
            
            switch (identifierCallOperableSemanticAST.getExpressionType()) {
                case BYTE:
                case FLOAT:
                case DOUBLE:
                case INTEGER:
                case SHORT:
                    return identifierCallOperableSemanticAST;
                default:
                    return null;
            }
        } else if (abstractOperableSemanticAST instanceof IdentifierInvokeOperableSemanticAST) {
            final IdentifierInvokeOperableSemanticAST identifierInvokeOperableSemanticAST = (IdentifierInvokeOperableSemanticAST) abstractOperableSemanticAST;
            if (identifierInvokeOperableSemanticAST.getExpressionType() == null)
                return null;
            
            switch (identifierInvokeOperableSemanticAST.getExpressionType()) {
                case BYTE:
                case FLOAT:
                case DOUBLE:
                case INTEGER:
                case SHORT:
                    return identifierInvokeOperableSemanticAST;
                default:
                    return null;
            }
        } else if (abstractOperableSemanticAST instanceof NumberOperableSemanticAST) {
            return abstractOperableSemanticAST;
        }
        return null;
    }
    
}
