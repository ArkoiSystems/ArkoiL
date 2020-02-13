package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SemanticASTError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.FunctionInvokeOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.IdentifierInvokeOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.NumberOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.StringOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.FunctionInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.StringOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.BinaryExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
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
public class ExpressionSemanticAST extends AbstractExpressionSemanticAST<ExpressionSyntaxAST>
{
    
    private AbstractOperableSemanticAST<?, ?> expressionOperable;
    
    private TypeSyntaxAST.TypeKind operableObject;
    
    public ExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final ExpressionSyntaxAST expressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, expressionSyntaxAST, ASTType.BASIC_EXPRESSION);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind getOperableObject() {
        if (this.operableObject == null) {
            final AbstractOperableSemanticAST<?, ?> expressionOperable = this.getExpressionOperable();
            if (expressionOperable instanceof NumberOperableSemanticAST) {
                final NumberOperableSemanticAST numberOperableSemanticAST = (NumberOperableSemanticAST) expressionOperable;
                return (this.operableObject = TypeSyntaxAST.TypeKind.getTypeKind(numberOperableSemanticAST.getOperableObject().getNumberType()));
            } else if (expressionOperable instanceof StringOperableSemanticAST)
                return (this.operableObject = TypeSyntaxAST.TypeKind.STRING);
            else if (expressionOperable instanceof AbstractExpressionSemanticAST) {
                final AbstractExpressionSemanticAST<?> abstractExpressionSemanticAST = (AbstractExpressionSemanticAST<?>) expressionOperable;
                return (this.operableObject = abstractExpressionSemanticAST.getOperableObject());
            } else {
                this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(expressionOperable, "Couldn't analyze the expression because the operable isn't supported."));
                return null;
            }
        }
        return this.operableObject;
    }
    
    public AbstractOperableSemanticAST<?, ?> getExpressionOperable() {
        if (this.expressionOperable == null) {
            if (this.getSyntaxAST().getExpressionOperable() instanceof StringOperableSyntaxAST) {
                final StringOperableSyntaxAST stringOperableSyntaxAST = (StringOperableSyntaxAST) this.getSyntaxAST().getExpressionOperable();
                final StringOperableSemanticAST stringOperableSemanticAST
                        = new StringOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), stringOperableSyntaxAST);
                
                if (stringOperableSemanticAST.getOperableObject() == null)
                    return null;
                return stringOperableSemanticAST;
            } else if (this.getSyntaxAST().getExpressionOperable() instanceof NumberOperableSyntaxAST) {
                final NumberOperableSyntaxAST numberOperableSyntaxAST = (NumberOperableSyntaxAST) this.getSyntaxAST().getExpressionOperable();
                final NumberOperableSemanticAST numberOperableSemanticAST
                        = new NumberOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), numberOperableSyntaxAST);
                
                if (numberOperableSemanticAST.getOperableObject() == null)
                    return null;
                return numberOperableSemanticAST;
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
                return identifierInvokeOperableSemanticAST;
            } else if (this.getSyntaxAST().getExpressionOperable() instanceof FunctionInvokeOperableSyntaxAST) {
                final FunctionInvokeOperableSyntaxAST functionInvokeOperableSyntaxAST = (FunctionInvokeOperableSyntaxAST) this.getSyntaxAST().getExpressionOperable();
                final FunctionInvokeOperableSemanticAST functionInvokeOperableSemanticAST
                        = new FunctionInvokeOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), functionInvokeOperableSyntaxAST);
                
                if (functionInvokeOperableSemanticAST.getInvokedFunction() == null)
                    return null;
                if (functionInvokeOperableSyntaxAST.getInvokedExpressions() == null)
                    return null;
                return functionInvokeOperableSemanticAST;
            } else if (this.getSyntaxAST().getExpressionOperable() instanceof BinaryExpressionSyntaxAST) {
                final BinaryExpressionSyntaxAST binaryExpressionSyntaxAST = (BinaryExpressionSyntaxAST) this.getSyntaxAST().getExpressionOperable();
                final BinaryExpressionSemanticAST binaryExpressionSemanticAST
                        = new BinaryExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), binaryExpressionSyntaxAST);
                
                if (binaryExpressionSemanticAST.getOperableObject() == null)
                    return null;
                return binaryExpressionSemanticAST;
            } else {
                this.getSemanticAnalyzer().errorHandler().addError(new SyntaxASTError<>(this.getSyntaxAST().getExpressionOperable(), "Couldn't analyze this expression because the operable isn't supported."));
                return null;
            }
        }
        return this.expressionOperable;
    }
    
    //    @Override
    //    public ExpressionSemanticAST analyseAST(final SemanticAnalyzer semanticAnalyzer) {
    //        if (this.getSyntaxAST().getAbstractOperableSyntaxAST() instanceof StringOperableSyntaxAST) {
    //            final StringOperableSyntaxAST stringOperableSyntaxAST = (StringOperableSyntaxAST) this.getSyntaxAST().getAbstractOperableSyntaxAST();
    //            final StringOperableSemanticAST stringOperableSemanticAST
    //                    = new StringOperableSemanticAST(this.getLastContainerAST(), stringOperableSyntaxAST).analyseAST(semanticAnalyzer);
    //            this.abstractOperableSemanticAST = stringOperableSemanticAST;
    //            this.setOperableObject(TypeSyntaxAST.TypeKind.STRING);
    //            return stringOperableSemanticAST == null ? null : this;
    //        } else if (this.getSyntaxAST().getAbstractOperableSyntaxAST() instanceof NumberOperableSyntaxAST) {
    //            final NumberOperableSyntaxAST numberOperableSyntaxAST = (NumberOperableSyntaxAST) this.getSyntaxAST().getAbstractOperableSyntaxAST();
    //            final NumberOperableSemanticAST numberOperableSemanticAST
    //                    = new NumberOperableSemanticAST(this.getLastContainerAST(), numberOperableSyntaxAST).analyseAST(semanticAnalyzer);
    //
    //            if (numberOperableSemanticAST == null)
    //                return null;
    //
    //            this.setOperableObject(TypeSyntaxAST.TypeKind.getTypeKind(numberOperableSemanticAST.getOperableObject().getNumberType()));
    //            this.abstractOperableSemanticAST = numberOperableSemanticAST;
    //            return this;
    //        } else if (this.getSyntaxAST().getAbstractOperableSyntaxAST() instanceof IdentifierInvokeOperableSyntaxAST) {
    //            final IdentifierInvokeOperableSyntaxAST identifierInvokeOperableSyntaxAST = (IdentifierInvokeOperableSyntaxAST) this.getSyntaxAST().getAbstractOperableSyntaxAST();
    //            final IdentifierInvokeOperableSemanticAST identifierInvokeOperableSemanticAST
    //                    = new IdentifierInvokeOperableSemanticAST(this.getLastContainerAST(), identifierInvokeOperableSyntaxAST).analyseAST(semanticAnalyzer);
    //            this.abstractOperableSemanticAST = identifierInvokeOperableSemanticAST;
    //            return identifierInvokeOperableSemanticAST == null ? null : this;
    //        } else if (this.getSyntaxAST().getAbstractOperableSyntaxAST() instanceof FunctionInvokeOperableSyntaxAST) {
    //            final FunctionInvokeOperableSyntaxAST functionInvokeOperableSyntaxAST = (FunctionInvokeOperableSyntaxAST) this.getSyntaxAST().getAbstractOperableSyntaxAST();
    //            final FunctionInvokeOperableSemanticAST functionInvokeOperableSemanticAST
    //                    = new FunctionInvokeOperableSemanticAST(this.getLastContainerAST(), functionInvokeOperableSyntaxAST).analyseAST(semanticAnalyzer);
    //            this.abstractOperableSemanticAST = functionInvokeOperableSemanticAST;
    //            return functionInvokeOperableSemanticAST == null ? null : this;
    //        } else if (this.getSyntaxAST().getAbstractOperableSyntaxAST() instanceof IdentifierCallOperableSyntaxAST) {
    //            final IdentifierCallOperableSyntaxAST identifierCallOperableSyntaxAST = (IdentifierCallOperableSyntaxAST) this.getSyntaxAST().getAbstractOperableSyntaxAST();
    //            final IdentifierCallOperableSemanticAST identifierCallOperableSemanticAST
    //                    = new IdentifierCallOperableSemanticAST(this.getLastContainerAST(), identifierCallOperableSyntaxAST).analyseAST(semanticAnalyzer);
    //            this.abstractOperableSemanticAST = identifierCallOperableSemanticAST;
    //            return identifierCallOperableSemanticAST == null ? null : this;
    //        } else {
    //            semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(this.getSyntaxAST().getAbstractOperableSyntaxAST(), "Couldn't analyze this expression because the operable isn't supported."));
    //            return null;
    //        }
    //    }
    
}
