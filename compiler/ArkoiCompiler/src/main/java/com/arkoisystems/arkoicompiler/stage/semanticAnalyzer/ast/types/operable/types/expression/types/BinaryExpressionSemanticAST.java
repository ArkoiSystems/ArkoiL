package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SemanticASTError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.BinaryExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ParenthesizedExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.PostfixExpressionSyntaxAST;
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
    
    private TypeSyntaxAST.TypeKind expressionType;
    
    public BinaryExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final BinaryExpressionSyntaxAST binaryExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, binaryExpressionSyntaxAST, ASTType.BINARY_EXPRESSION);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind getExpressionType() {
        if (this.expressionType == null) {
            if (this.getBinaryOperator() == null)
                return null;
            if (this.getLeftSideOperable() == null)
                return null;
            if (this.getRightSideOperable() == null)
                return null;
    
            final TypeSyntaxAST.TypeKind typeKind;
            switch (this.getBinaryOperator()) {
                case ADDITION:
                    typeKind = this.binAdd(this.getLeftSideOperable(), this.getRightSideOperable());
                    break;
                case SUBTRACTION:
                    typeKind = this.binSub(this.getLeftSideOperable(), this.getRightSideOperable());
                    break;
                case MULTIPLICATION:
                    typeKind = this.binMul(this.getLeftSideOperable(), this.getRightSideOperable());
                    break;
                case DIVISION:
                    typeKind = this.binDiv(this.getLeftSideOperable(), this.getRightSideOperable());
                    break;
                case MODULO:
                    typeKind = this.binMod(this.getLeftSideOperable(), this.getRightSideOperable());
                    break;
                default:
                    typeKind = null;
                    break;
            }
            return (this.expressionType = typeKind);
        }
        return this.expressionType;
    }
    
    public AbstractOperableSemanticAST<?, ?> getLeftSideOperable() {
        if (this.leftSideOperable == null)
            return (this.leftSideOperable = this.analyzeOperable(this.getSyntaxAST().getLeftSideOperable()));
        return this.leftSideOperable;
    }
    
    public BinaryExpressionSyntaxAST.BinaryOperator getBinaryOperator() {
        return this.getSyntaxAST().getBinaryOperator();
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
            
            if (parenthesizedExpressionSemanticAST.getExpressionType() == null)
                return null;
            return parenthesizedExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof NumberOperableSyntaxAST) {
            final NumberOperableSyntaxAST numberOperableSyntaxAST = (NumberOperableSyntaxAST) abstractOperableSyntaxAST;
            final NumberOperableSemanticAST numberOperableSemanticAST
                    = new NumberOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), numberOperableSyntaxAST);
            
            if (numberOperableSemanticAST.getExpressionType() == null)
                return null;
            return numberOperableSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof StringOperableSyntaxAST) {
            final StringOperableSyntaxAST stringOperableSyntaxAST = (StringOperableSyntaxAST) abstractOperableSyntaxAST;
            final StringOperableSemanticAST stringOperableSemanticAST
                    = new StringOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), stringOperableSyntaxAST);
            
            if (stringOperableSemanticAST.getExpressionType() == null)
                return null;
            return stringOperableSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof CollectionOperableSyntaxAST) {
            final CollectionOperableSyntaxAST collectionOperableSyntaxAST = (CollectionOperableSyntaxAST) abstractOperableSyntaxAST;
            final CollectionOperableSemanticAST collectionOperableSemanticAST
                    = new CollectionOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), collectionOperableSyntaxAST);
            
            if (collectionOperableSemanticAST.getExpressionType() == null)
                return null;
            return collectionOperableSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof BinaryExpressionSyntaxAST) {
            final BinaryExpressionSyntaxAST binaryExpressionSyntaxAST = (BinaryExpressionSyntaxAST) abstractOperableSyntaxAST;
            final BinaryExpressionSemanticAST binaryExpressionSemanticAST
                    = new BinaryExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), binaryExpressionSyntaxAST);
            
            if (binaryExpressionSemanticAST.getExpressionType() == null)
                return null;
            return binaryExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof PrefixExpressionSyntaxAST) {
            final PrefixExpressionSyntaxAST prefixExpressionSyntaxAST = (PrefixExpressionSyntaxAST) abstractOperableSyntaxAST;
            final PrefixExpressionSemanticAST prefixExpressionSemanticAST
                    = new PrefixExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), prefixExpressionSyntaxAST);
            
            if (prefixExpressionSemanticAST.getExpressionType() == null)
                return null;
            return prefixExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof PostfixExpressionSyntaxAST) {
            final PostfixExpressionSyntaxAST postfixExpressionSyntaxAST = (PostfixExpressionSyntaxAST) abstractOperableSyntaxAST;
            final PostfixExpressionSemanticAST postfixExpressionSemanticAST
                    = new PostfixExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), postfixExpressionSyntaxAST);
    
            if (postfixExpressionSemanticAST.getExpressionType() == null)
                return null;
            return postfixExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof IdentifierInvokeOperableSyntaxAST) {
            final IdentifierInvokeOperableSyntaxAST identifierInvokeOperableSyntaxAST = (IdentifierInvokeOperableSyntaxAST) abstractOperableSyntaxAST;
            final IdentifierInvokeOperableSemanticAST identifierInvokeOperableSemanticAST
                    = new IdentifierInvokeOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), identifierInvokeOperableSyntaxAST);
    
            if (identifierInvokeOperableSemanticAST.getExpressionType() == null)
                return null;
            return identifierInvokeOperableSemanticAST;
        }  else if (abstractOperableSyntaxAST instanceof IdentifierCallOperableSyntaxAST) {
            final IdentifierCallOperableSyntaxAST identifierCallOperableSyntaxAST = (IdentifierCallOperableSyntaxAST) abstractOperableSyntaxAST;
            final IdentifierCallOperableSemanticAST identifierCallOperableSemanticAST
                    = new IdentifierCallOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), identifierCallOperableSyntaxAST);
    
            if (identifierCallOperableSemanticAST.getExpressionType() == null)
                return null;
            return identifierCallOperableSemanticAST;
        }  else if (abstractOperableSyntaxAST instanceof FunctionInvokeOperableSyntaxAST) {
            final FunctionInvokeOperableSyntaxAST functionInvokeOperableSyntaxAST = (FunctionInvokeOperableSyntaxAST) abstractOperableSyntaxAST;
            final FunctionInvokeOperableSemanticAST functionInvokeOperableSemanticAST
                    = new FunctionInvokeOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), functionInvokeOperableSyntaxAST);
    
            if (functionInvokeOperableSemanticAST.getExpressionType() == null)
                return null;
            return functionInvokeOperableSemanticAST;
        } else {
            this.getSemanticAnalyzer().errorHandler().addError(new SyntaxASTError<>(abstractOperableSyntaxAST, "Couldn't analyze this operable because it isn't supported by the binary expression."));
            return null;
        }
    }
    
    @Override
    public TypeSyntaxAST.TypeKind binAdd(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        final AbstractOperableSemanticAST<?, ?>
                leftExpressionOperable = this.analyzeNumericOperable(leftSideOperable),
                rightExpressionOperable = this.analyzeNumericOperable(rightSideOperable);
        if (leftExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(leftSideOperable, "Couldn't analyze this binary expression because the left side operable isn't supported by an addition."));
            return null;
        } else if (rightExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(rightSideOperable, "Couldn't analyze this binary expression because the right side operable isn't supported by an addition."));
            return null;
        }
        return TypeSyntaxAST.TypeKind.combineKinds(leftExpressionOperable, rightExpressionOperable);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind binSub(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        final AbstractOperableSemanticAST<?, ?>
                leftExpressionOperable = this.analyzeNumericOperable(leftSideOperable),
                rightExpressionOperable = this.analyzeNumericOperable(rightSideOperable);
        if (leftExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(leftSideOperable, "Couldn't analyze this binary expression because the left side operable isn't supported by a subtraction."));
            return null;
        } else if (rightExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(rightSideOperable, "Couldn't analyze this binary expression because the right side operable isn't supported by a subtraction."));
            return null;
        }
        return TypeSyntaxAST.TypeKind.combineKinds(leftExpressionOperable, rightExpressionOperable);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind binMul(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        final AbstractOperableSemanticAST<?, ?>
                leftExpressionOperable = this.analyzeNumericOperable(leftSideOperable),
                rightExpressionOperable = this.analyzeNumericOperable(rightSideOperable);
        if (leftExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(leftSideOperable, "Couldn't analyze this binary expression because the left side operable isn't supported by a multiplication."));
            return null;
        } else if (rightExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(rightSideOperable, "Couldn't analyze this binary expression because the right side operable isn't supported by a multiplication."));
            return null;
        }
        return TypeSyntaxAST.TypeKind.combineKinds(leftExpressionOperable, rightExpressionOperable);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind binDiv(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        final AbstractOperableSemanticAST<?, ?>
                leftExpressionOperable = this.analyzeNumericOperable(leftSideOperable),
                rightExpressionOperable = this.analyzeNumericOperable(rightSideOperable);
        if (leftExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(leftSideOperable, "Couldn't analyze this binary expression because the left side operable isn't supported by a division."));
            return null;
        } else if (rightExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(rightSideOperable, "Couldn't analyze this binary expression because the right side operable isn't supported by a division."));
            return null;
        }
        return TypeSyntaxAST.TypeKind.combineKinds(leftExpressionOperable, rightExpressionOperable);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind binMod(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        final AbstractOperableSemanticAST<?, ?> leftExpressionOperable;
        if(leftSideOperable instanceof StringOperableSemanticAST)
            leftExpressionOperable = leftSideOperable;
        else leftExpressionOperable = this.analyzeNumericOperable(leftSideOperable);
        
        final AbstractOperableSemanticAST<?, ?> rightExpressionOperable;
        if(rightSideOperable instanceof CollectionOperableSemanticAST)
            rightExpressionOperable = rightSideOperable;
        else rightExpressionOperable = this.analyzeNumericOperable(rightSideOperable);
        
        if (leftExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(leftSideOperable, "Couldn't analyze this binary expression because the left side operable isn't supported by the modulo operator."));
            return null;
        } else if (rightExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(rightSideOperable, "Couldn't analyze this binary expression because the right side operable isn't supported by the modulo operator."));
            return null;
        }
        return TypeSyntaxAST.TypeKind.combineKinds(leftExpressionOperable, rightExpressionOperable);
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
        } else if (abstractOperableSemanticAST instanceof BinaryExpressionSemanticAST) {
            final BinaryExpressionSemanticAST binaryExpressionSemanticAST = (BinaryExpressionSemanticAST) abstractOperableSemanticAST;
            if (binaryExpressionSemanticAST.getExpressionType() == null)
                return null;
            
            switch (binaryExpressionSemanticAST.getExpressionType()) {
                case BYTE:
                case FLOAT:
                case DOUBLE:
                case INTEGER:
                case SHORT:
                    return binaryExpressionSemanticAST;
                default:
                    return null;
            }
        } else if (abstractOperableSemanticAST instanceof PostfixExpressionSemanticAST) {
            final PostfixExpressionSemanticAST postfixExpressionSemanticAST = (PostfixExpressionSemanticAST) abstractOperableSemanticAST;
            if (postfixExpressionSemanticAST.getExpressionType() == null)
                return null;
            
            switch (postfixExpressionSemanticAST.getExpressionType()) {
                case BYTE:
                case FLOAT:
                case DOUBLE:
                case INTEGER:
                case SHORT:
                    return postfixExpressionSemanticAST;
                default:
                    return null;
            }
        } else if (abstractOperableSemanticAST instanceof PrefixExpressionSemanticAST) {
            final PrefixExpressionSemanticAST prefixExpressionSemanticAST = (PrefixExpressionSemanticAST) abstractOperableSemanticAST;
            if (prefixExpressionSemanticAST.getExpressionType() == null)
                return null;
            
            switch (prefixExpressionSemanticAST.getExpressionType()) {
                case BYTE:
                case FLOAT:
                case DOUBLE:
                case INTEGER:
                case SHORT:
                    return prefixExpressionSemanticAST;
                default:
                    return null;
            }
        } else if (abstractOperableSemanticAST instanceof NumberOperableSemanticAST)
            return abstractOperableSemanticAST;
        else if (abstractOperableSemanticAST instanceof FunctionInvokeOperableSemanticAST) {
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
        }
        return null;
    }
    
}
