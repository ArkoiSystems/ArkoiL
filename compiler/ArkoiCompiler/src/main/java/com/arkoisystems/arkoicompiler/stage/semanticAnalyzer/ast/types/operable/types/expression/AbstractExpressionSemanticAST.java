/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SemanticASTError;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;

public class AbstractExpressionSemanticAST<T extends AbstractSyntaxAST> extends AbstractOperableSemanticAST<T, TypeKind>
{
    
    public AbstractExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final T syntaxAST, final ASTType astType) {
        super(semanticAnalyzer, lastContainerAST, syntaxAST, astType);
    }
    
    
    public TypeKind binAdd(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "Addition isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind binSub(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "Subtraction isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind binMul(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "Multiplication isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind binDiv(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "Division isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind binMod(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "Modulo isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind assign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "Assignment isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind addAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "Addition assignment isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind subAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "Subtraction assignment isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind mulAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "Multiplication assignment isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind divAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "Division assignment isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind modAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "Modulo assignment isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind equal(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "The \"equal\" operation isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind notEqual(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "The \"not equal\" operation isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind logicalOr(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "The \"logical or\" operation isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind logicalAnd(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "The \"logical and\" operation isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind postfixAdd(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { abstractOperableSemanticAST },
                "The \"post addition\" operator isn't supported by this operable."
        ));
        return null;
    }
    
    
    public TypeKind postfixSub(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { abstractOperableSemanticAST },
                "The \"post subtraction\" operator isn't supported by this operable."
        ));
        return null;
    }
    
    
    public TypeKind prefixAdd(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { abstractOperableSemanticAST },
                "The \"pre addition\" operator isn't supported by this operable."
        ));
        return null;
    }
    
    
    public TypeKind prefixSub(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { abstractOperableSemanticAST },
                "The \"pre subtraction\" operator isn't supported by this operable."
        ));
        return null;
    }
    
    
    public TypeKind prefixNegate(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { abstractOperableSemanticAST },
                "The \"negate\" operator isn't supported by this operable."
        ));
        return null;
    }
    
    
    public TypeKind prefixAffirm(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { abstractOperableSemanticAST },
                "The \"affirm\" operator isn't supported by this operable."
        ));
        return null;
    }
    
    
    public TypeKind relationalLessThan(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "The \"less than\" operator isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind relationalGreaterThan(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "The \"greater than\" operator isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind relationalLessEqualThan(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "The \"less equal than\" operator isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind relationalGreaterEqualThan(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(), "The \"greater equal than\" operator isn't supported by these operable."
        ));
        return null;
    }
    
    
    public TypeKind relationalIs(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] { this },
                leftSideOperable.getStart(),
                rightSideOperable.getEnd(),
                "The \"is\" keyword isn't supported by these operable."
        ));
        return null;
    }
    
}
