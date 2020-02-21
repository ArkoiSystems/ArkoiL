/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SemanticASTError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;

public class BinaryExpressionSemanticAST extends AbstractExpressionSemanticAST<BinaryExpressionSyntaxAST>
{
    
    private AbstractOperableSemanticAST<?, ?> leftSideOperable, rightSideOperable;
    
    
    private TypeKind expressionType;
    
    
    public BinaryExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final BinaryExpressionSyntaxAST binaryExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, binaryExpressionSyntaxAST, ASTType.BINARY_EXPRESSION);
    }
    
    
    @Override
    public TypeKind getOperableObject() {
        if (this.expressionType == null) {
            if (this.getBinaryOperator() == null)
                return null;
            if (this.getLeftSideOperable() == null)
                return null;
            if (this.getRightSideOperable() == null)
                return null;
    
            final TypeKind typeKind;
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
        } else if (abstractOperableSyntaxAST instanceof StringOperableSyntaxAST) {
            final StringOperableSyntaxAST stringOperableSyntaxAST = (StringOperableSyntaxAST) abstractOperableSyntaxAST;
            final StringOperableSemanticAST stringOperableSemanticAST
                    = new StringOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), stringOperableSyntaxAST);
            
            if (stringOperableSemanticAST.getOperableObject() == null)
                return null;
            return stringOperableSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof CollectionOperableSyntaxAST) {
            final CollectionOperableSyntaxAST collectionOperableSyntaxAST = (CollectionOperableSyntaxAST) abstractOperableSyntaxAST;
            final CollectionOperableSemanticAST collectionOperableSemanticAST
                    = new CollectionOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), collectionOperableSyntaxAST);
            
            if (collectionOperableSemanticAST.getOperableObject() == null)
                return null;
            return collectionOperableSemanticAST;
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
        } else if (abstractOperableSyntaxAST instanceof PostfixExpressionSyntaxAST) {
            final PostfixExpressionSyntaxAST postfixExpressionSyntaxAST = (PostfixExpressionSyntaxAST) abstractOperableSyntaxAST;
            final PostfixExpressionSemanticAST postfixExpressionSemanticAST
                    = new PostfixExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), postfixExpressionSyntaxAST);
    
            if (postfixExpressionSemanticAST.getOperableObject() == null)
                return null;
            return postfixExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof IdentifierInvokeOperableSyntaxAST) {
            final IdentifierInvokeOperableSyntaxAST identifierInvokeOperableSyntaxAST = (IdentifierInvokeOperableSyntaxAST) abstractOperableSyntaxAST;
            final IdentifierInvokeOperableSemanticAST identifierInvokeOperableSemanticAST
                    = new IdentifierInvokeOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), identifierInvokeOperableSyntaxAST);
    
            if (identifierInvokeOperableSemanticAST.getOperableObject() == null)
                return null;
            return identifierInvokeOperableSemanticAST;
        }  else if (abstractOperableSyntaxAST instanceof IdentifierCallOperableSyntaxAST) {
            final IdentifierCallOperableSyntaxAST identifierCallOperableSyntaxAST = (IdentifierCallOperableSyntaxAST) abstractOperableSyntaxAST;
            final IdentifierCallOperableSemanticAST identifierCallOperableSemanticAST
                    = new IdentifierCallOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), identifierCallOperableSyntaxAST);
    
            if (identifierCallOperableSemanticAST.getOperableObject() == null)
                return null;
            return identifierCallOperableSemanticAST;
        }  else if (abstractOperableSyntaxAST instanceof FunctionInvokeOperableSyntaxAST) {
            final FunctionInvokeOperableSyntaxAST functionInvokeOperableSyntaxAST = (FunctionInvokeOperableSyntaxAST) abstractOperableSyntaxAST;
            final FunctionInvokeOperableSemanticAST functionInvokeOperableSemanticAST
                    = new FunctionInvokeOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), functionInvokeOperableSyntaxAST);
    
            if (functionInvokeOperableSemanticAST.getOperableObject() == null)
                return null;
            return functionInvokeOperableSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof CastExpressionSyntaxAST) {
            final CastExpressionSyntaxAST castExpressionSyntaxAST = (CastExpressionSyntaxAST) abstractOperableSyntaxAST;
            final CastExpressionSemanticAST castExpressionSemanticAST
                    = new CastExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), castExpressionSyntaxAST);
    
            if (castExpressionSemanticAST.getOperableObject() == null)
                return null;
            return castExpressionSemanticAST;
        }  else {
            this.getSemanticAnalyzer().errorHandler().addError(new SyntaxASTError<>(this.getSemanticAnalyzer().getArkoiClass(), abstractOperableSyntaxAST, "Couldn't analyze this operable because it isn't supported by the binary expression."));
            return null;
        }
    }
    
    
    @Override
    public TypeKind binAdd(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        final AbstractOperableSemanticAST<?, ?>
                leftExpressionOperable = this.analyzeNumericOperable(leftSideOperable),
                rightExpressionOperable = this.analyzeNumericOperable(rightSideOperable);
        if (leftExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    new AbstractSemanticAST[] { leftSideOperable },
                    "Couldn't analyze this binary expression because the left side operable isn't supported by an addition."
            ));
            return null;
        } else if (rightExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    new AbstractSemanticAST[] { rightSideOperable },
                    "Couldn't analyze this binary expression because the right side operable isn't supported by an addition."
            ));
            return null;
        }
        return TypeKind.combineKinds(leftExpressionOperable, rightExpressionOperable);
    }
    
    
    @Override
    public TypeKind binSub(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        final AbstractOperableSemanticAST<?, ?>
                leftExpressionOperable = this.analyzeNumericOperable(leftSideOperable),
                rightExpressionOperable = this.analyzeNumericOperable(rightSideOperable);
        if (leftExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    new AbstractSemanticAST[] { leftSideOperable },
                    "Couldn't analyze this binary expression because the left side operable isn't supported by a subtraction."
            ));
            return null;
        } else if (rightExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    new AbstractSemanticAST[] { rightSideOperable },
                    "Couldn't analyze this binary expression because the right side operable isn't supported by a subtraction."
            ));
            return null;
        }
        return TypeKind.combineKinds(leftExpressionOperable, rightExpressionOperable);
    }
    
    
    @Override
    public TypeKind binMul(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        final AbstractOperableSemanticAST<?, ?>
                leftExpressionOperable = this.analyzeNumericOperable(leftSideOperable),
                rightExpressionOperable = this.analyzeNumericOperable(rightSideOperable);
        if (leftExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    new AbstractSemanticAST[] { leftSideOperable },
                    "Couldn't analyze this binary expression because the left side operable isn't supported by a multiplication."
            ));
            return null;
        } else if (rightExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    new AbstractSemanticAST[] { rightSideOperable },
                    "Couldn't analyze this binary expression because the right side operable isn't supported by a multiplication."
            ));
            return null;
        }
        return TypeKind.combineKinds(leftExpressionOperable, rightExpressionOperable);
    }
    
    
    @Override
    public TypeKind binDiv(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        final AbstractOperableSemanticAST<?, ?>
                leftExpressionOperable = this.analyzeNumericOperable(leftSideOperable),
                rightExpressionOperable = this.analyzeNumericOperable(rightSideOperable);
        if (leftExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    new AbstractSemanticAST[] { leftSideOperable },
                    "Couldn't analyze this binary expression because the left side operable isn't supported by a division."
            ));
            return null;
        } else if (rightExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    new AbstractSemanticAST[] { rightSideOperable },
                    "Couldn't analyze this binary expression because the right side operable isn't supported by a division."
            ));
            return null;
        }
        return TypeKind.combineKinds(leftExpressionOperable, rightExpressionOperable);
    }
    
    
    @Override
    public TypeKind binMod(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        final AbstractOperableSemanticAST<?, ?> leftExpressionOperable;
        if(leftSideOperable instanceof StringOperableSemanticAST)
            leftExpressionOperable = leftSideOperable;
        else leftExpressionOperable = this.analyzeNumericOperable(leftSideOperable);
        
        final AbstractOperableSemanticAST<?, ?> rightExpressionOperable;
        if(rightSideOperable instanceof CollectionOperableSemanticAST)
            rightExpressionOperable = rightSideOperable;
        else rightExpressionOperable = this.analyzeNumericOperable(rightSideOperable);
        
        if (leftExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    new AbstractSemanticAST[] { leftSideOperable },
                    "Couldn't analyze this binary expression because the left side operable isn't supported by the modulo operator."
            ));
            return null;
        } else if (rightExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    new AbstractSemanticAST[] { rightSideOperable },
                    "Couldn't analyze this binary expression because the right side operable isn't supported by the modulo operator."
            ));
            return null;
        }
        return TypeKind.combineKinds(leftExpressionOperable, rightExpressionOperable);
    }
    
    
    private AbstractOperableSemanticAST<?, ?> analyzeNumericOperable(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        if (abstractOperableSemanticAST instanceof ParenthesizedExpressionSemanticAST) {
            final ParenthesizedExpressionSemanticAST parenthesizedExpressionSemanticAST = (ParenthesizedExpressionSemanticAST) abstractOperableSemanticAST;
            if (parenthesizedExpressionSemanticAST.getOperableObject() == null)
                return null;
            
            switch (parenthesizedExpressionSemanticAST.getOperableObject()) {
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
            if (binaryExpressionSemanticAST.getOperableObject() == null)
                return null;
            
            switch (binaryExpressionSemanticAST.getOperableObject()) {
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
            if (postfixExpressionSemanticAST.getOperableObject() == null)
                return null;
            
            switch (postfixExpressionSemanticAST.getOperableObject()) {
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
            if (prefixExpressionSemanticAST.getOperableObject() == null)
                return null;
            
            switch (prefixExpressionSemanticAST.getOperableObject()) {
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
            if (functionInvokeOperableSemanticAST.getOperableObject() == null)
                return null;
            
            switch (functionInvokeOperableSemanticAST.getOperableObject()) {
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
            if (identifierCallOperableSemanticAST.getOperableObject() == null)
                return null;
            
            switch (identifierCallOperableSemanticAST.getOperableObject()) {
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
            if (identifierInvokeOperableSemanticAST.getOperableObject() == null)
                return null;
            
            switch (identifierInvokeOperableSemanticAST.getOperableObject()) {
                case BYTE:
                case FLOAT:
                case DOUBLE:
                case INTEGER:
                case SHORT:
                    return identifierInvokeOperableSemanticAST;
                default:
                    return null;
            }
        } else if (abstractOperableSemanticAST instanceof CastExpressionSemanticAST) {
            final CastExpressionSemanticAST castExpressionSemanticAST = (CastExpressionSemanticAST) abstractOperableSemanticAST;
            if (castExpressionSemanticAST.getOperableObject() == null)
                return null;
    
            switch (castExpressionSemanticAST.getOperableObject()) {
                case BYTE:
                case FLOAT:
                case DOUBLE:
                case INTEGER:
                case SHORT:
                    return castExpressionSemanticAST;
                default:
                    return null;
            }
        }
        return null;
    }
    
}
