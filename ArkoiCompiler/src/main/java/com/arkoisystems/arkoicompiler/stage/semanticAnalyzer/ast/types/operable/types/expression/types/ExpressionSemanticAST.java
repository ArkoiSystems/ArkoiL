/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.StringOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.Objects;

public class ExpressionSemanticAST extends AbstractExpressionSemanticAST<ExpressionSyntaxAST>
{
    
    private AbstractOperableSemanticAST<?, ?> expressionOperable;
    
    
    private TypeKind expressionType;
    
    
    public ExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final ExpressionSyntaxAST expressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, expressionSyntaxAST, ASTType.EXPRESSION);
    }
    
    
    // TODO: Check null safety.
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        Objects.requireNonNull(this.getExpressionOperable()).printSemanticAST(printStream, indents);
    }
    
    
    @Override
    public TypeKind getOperableObject() {
        if (this.expressionType == null) {
            final AbstractOperableSemanticAST<?, ?> expressionOperable = this.getExpressionOperable();
            if (expressionOperable instanceof NumberOperableSemanticAST) {
                final NumberOperableSemanticAST numberOperableSemanticAST = (NumberOperableSemanticAST) expressionOperable;
                this.expressionType = numberOperableSemanticAST.getOperableObject();
            } else if (expressionOperable instanceof StringOperableSemanticAST) {
                this.expressionType = TypeKind.STRING;
            } else if (expressionOperable instanceof AbstractExpressionSemanticAST) {
                final AbstractExpressionSemanticAST<?> abstractExpressionSemanticAST = (AbstractExpressionSemanticAST<?>) expressionOperable;
                this.expressionType = abstractExpressionSemanticAST.getOperableObject();
            } else if (expressionOperable instanceof IdentifierInvokeOperableSemanticAST) {
                final IdentifierInvokeOperableSemanticAST identifierInvokeOperableSemanticAST = (IdentifierInvokeOperableSemanticAST) expressionOperable;
                this.expressionType = identifierInvokeOperableSemanticAST.getOperableObject();
            } else if (expressionOperable instanceof FunctionInvokeOperableSemanticAST) {
                final FunctionInvokeOperableSemanticAST functionInvokeOperableSemanticAST = (FunctionInvokeOperableSemanticAST) expressionOperable;
                this.expressionType = functionInvokeOperableSemanticAST.getOperableObject();
            } else if (expressionOperable instanceof IdentifierCallOperableSemanticAST) {
                final IdentifierCallOperableSemanticAST identifierCallOperableSemanticAST = (IdentifierCallOperableSemanticAST) expressionOperable;
                this.expressionType = identifierCallOperableSemanticAST.getOperableObject();
            }
            
            if (this.expressionOperable == null)
                this.failed();
            return this.expressionType;
        }
        return this.expressionType;
    }
    
    
    private AbstractOperableSemanticAST<?, ?> getExpressionOperable() {
        if (this.expressionOperable == null) {
            if (this.getSyntaxAST().getExpressionOperable() instanceof StringOperableSyntaxAST) {
                final StringOperableSyntaxAST stringOperableSyntaxAST = (StringOperableSyntaxAST) this.getSyntaxAST().getExpressionOperable();
                final StringOperableSemanticAST stringOperableSemanticAST
                        = new StringOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), stringOperableSyntaxAST);
                
                if (stringOperableSemanticAST.getOperableObject() == null)
                    return null;
                return (this.expressionOperable = stringOperableSemanticAST);
            } else if (this.getSyntaxAST().getExpressionOperable() instanceof NumberOperableSyntaxAST) {
                final NumberOperableSyntaxAST numberOperableSyntaxAST = (NumberOperableSyntaxAST) this.getSyntaxAST().getExpressionOperable();
                final NumberOperableSemanticAST numberOperableSemanticAST
                        = new NumberOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), numberOperableSyntaxAST);
                
                if (numberOperableSemanticAST.getOperableObject() == null)
                    return null;
                return (this.expressionOperable = numberOperableSemanticAST);
            } else if (this.getSyntaxAST().getExpressionOperable() instanceof IdentifierInvokeOperableSyntaxAST) {
                final IdentifierInvokeOperableSyntaxAST identifierInvokeOperableSyntaxAST = (IdentifierInvokeOperableSyntaxAST) this.getSyntaxAST().getExpressionOperable();
                final IdentifierInvokeOperableSemanticAST identifierInvokeOperableSemanticAST
                        = new IdentifierInvokeOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), identifierInvokeOperableSyntaxAST);
                
                if (identifierInvokeOperableSemanticAST.getIdentifierAccess() == null)
                    return null;
                if (identifierInvokeOperableSemanticAST.getInvokedIdentifier() == null)
                    return null;
                if (identifierInvokeOperableSemanticAST.getInvokePostStatement() == null)
                    return null;
                return (this.expressionOperable = identifierInvokeOperableSemanticAST);
            } else if (this.getSyntaxAST().getExpressionOperable() instanceof FunctionInvokeOperableSyntaxAST) {
                final FunctionInvokeOperableSyntaxAST functionInvokeOperableSyntaxAST = (FunctionInvokeOperableSyntaxAST) this.getSyntaxAST().getExpressionOperable();
                final FunctionInvokeOperableSemanticAST functionInvokeOperableSemanticAST
                        = new FunctionInvokeOperableSemanticAST(this.getSemanticAnalyzer(), this, this.getLastContainerAST(), functionInvokeOperableSyntaxAST);
                
                if (functionInvokeOperableSemanticAST.getInvokedFunction() == null)
                    return null;
                if (functionInvokeOperableSyntaxAST.getInvokedExpressions() == null)
                    return null;
                return (this.expressionOperable = functionInvokeOperableSemanticAST);
            } else if (this.getSyntaxAST().getExpressionOperable() instanceof BinaryExpressionSyntaxAST) {
                final BinaryExpressionSyntaxAST binaryExpressionSyntaxAST = (BinaryExpressionSyntaxAST) this.getSyntaxAST().getExpressionOperable();
                final BinaryExpressionSemanticAST binaryExpressionSemanticAST
                        = new BinaryExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), binaryExpressionSyntaxAST);
                
                if (binaryExpressionSemanticAST.getOperableObject() == null)
                    return null;
                return (this.expressionOperable = binaryExpressionSemanticAST);
            } else if (this.getSyntaxAST().getExpressionOperable() instanceof IdentifierCallOperableSyntaxAST) {
                final IdentifierCallOperableSyntaxAST identifierCallOperableSyntaxAST = (IdentifierCallOperableSyntaxAST) this.getSyntaxAST().getExpressionOperable();
                final IdentifierCallOperableSemanticAST identifierCallOperableSemanticAST
                        = new IdentifierCallOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), identifierCallOperableSyntaxAST);
                
                if (identifierCallOperableSemanticAST.getOperableObject() == null)
                    return null;
                return (this.expressionOperable = identifierCallOperableSemanticAST);
            } else if (this.getSyntaxAST().getExpressionOperable() instanceof ParenthesizedExpressionSyntaxAST) {
                final ParenthesizedExpressionSyntaxAST parenthesizedExpressionSyntaxAST = (ParenthesizedExpressionSyntaxAST) this.getSyntaxAST().getExpressionOperable();
                final ParenthesizedExpressionSemanticAST parenthesizedExpressionSemanticAST
                        = new ParenthesizedExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), parenthesizedExpressionSyntaxAST);

                if (parenthesizedExpressionSemanticAST.getOperableObject() == null)
                    return null;
                return (this.expressionOperable = parenthesizedExpressionSemanticAST);
            } else if (this.getSyntaxAST().getExpressionOperable() instanceof AssignmentExpressionSyntaxAST) {
                final AssignmentExpressionSyntaxAST assignmentExpressionSyntaxAST = (AssignmentExpressionSyntaxAST) this.getSyntaxAST().getExpressionOperable();
                final AssignmentExpressionSemanticAST assignmentExpressionSemanticAST
                        = new AssignmentExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), assignmentExpressionSyntaxAST);

                if (assignmentExpressionSemanticAST.getOperableObject() == null)
                    return null;
                return (this.expressionOperable = assignmentExpressionSemanticAST);
            } else if (this.getSyntaxAST().getExpressionOperable() instanceof PrefixExpressionSyntaxAST) {
                final PrefixExpressionSyntaxAST prefixExpressionSyntaxAST = (PrefixExpressionSyntaxAST) this.getSyntaxAST().getExpressionOperable();
                final PrefixExpressionSemanticAST prefixExpressionSemanticAST
                        = new PrefixExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), prefixExpressionSyntaxAST);

                if (prefixExpressionSemanticAST.getOperableObject() == null)
                    return null;
                return (this.expressionOperable = prefixExpressionSemanticAST);
            } else if (this.getSyntaxAST().getExpressionOperable() instanceof PostfixExpressionSyntaxAST) {
                final PostfixExpressionSyntaxAST postfixExpressionSyntaxAST = (PostfixExpressionSyntaxAST) this.getSyntaxAST().getExpressionOperable();
                final PostfixExpressionSemanticAST postfixExpressionSemanticAST
                        = new PostfixExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), postfixExpressionSyntaxAST);

                if (postfixExpressionSemanticAST.getOperableObject() == null)
                    return null;
                return (this.expressionOperable = postfixExpressionSemanticAST);
            } else if (this.getSyntaxAST().getExpressionOperable() instanceof CastExpressionSyntaxAST) {
                final CastExpressionSyntaxAST castExpressionSyntaxAST = (CastExpressionSyntaxAST) this.getSyntaxAST().getExpressionOperable();
                final CastExpressionSemanticAST castExpressionSemanticAST
                        = new CastExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), castExpressionSyntaxAST);

                if (castExpressionSemanticAST.getOperableObject() == null)
                    return null;
                return (this.expressionOperable = castExpressionSemanticAST);
            } else {
                this.addError(
                        this.getSemanticAnalyzer().getArkoiClass(),
                        this.getSyntaxAST().getExpressionOperable(),
                        SemanticErrorType.EXPRESSION_AST_NOT_SUPPORTED
                );
                return null;
            }
        }
        return this.expressionOperable;
    }
    
}
