package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.AbstractSemantic;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.operable.IOperableSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.TypeAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.IdentifierCallAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.IdentifierInvokeAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.function.FunctionInvokeAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types.OperableParser;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka Timo
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
@Getter
@Setter
public class AbstractOperableAST<OT1, S extends AbstractSemantic<?>> extends AbstractAST<S> implements IOperableSemantic
{
    
    public static OperableParser OPERABLE_PARSER = new OperableParser();
    
    @Expose
    private OT1 operableObject;
    
    public AbstractOperableAST(final ASTType astType) {
        super(astType);
    }
    
    @Override
    public AbstractOperableAST<?, ?> parseAST(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractToken currentToken = syntaxAnalyzer.currentToken();
        switch (currentToken.getTokenType()) {
            case STRING_LITERAL:
                return new StringOperableAST().parseAST(parentAST, syntaxAnalyzer);
            case NUMBER_LITERAL:
                return new NumberOperableAST().parseAST(parentAST, syntaxAnalyzer);
            case SYMBOL:
                if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.OPENING_BRACKET) != null)
                    return new CollectionOperableAST().parseAST(parentAST, syntaxAnalyzer);
                else {
                    syntaxAnalyzer.errorHandler().addError(new TokenError(currentToken, "Couldn't parse the operable because the SymbolType isn't supported."));
                    return null;
                }
            case IDENTIFIER:
                if (!AbstractStatementAST.STATEMENT_PARSER.canParse(parentAST, syntaxAnalyzer)) {
                    syntaxAnalyzer.errorHandler().addError(new ParserError<>(AbstractStatementAST.STATEMENT_PARSER, parentAST.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the operable statement because it isn't parsable."));
                    return null;
                }
    
                final AbstractStatementAST<?> abstractStatementAST = AbstractStatementAST.STATEMENT_PARSER.parse(parentAST, syntaxAnalyzer);
                if (abstractStatementAST instanceof FunctionInvokeAST)
                    return new FunctionInvokeOperableAST((FunctionInvokeAST) abstractStatementAST).parseAST(parentAST, syntaxAnalyzer);
                else if(abstractStatementAST instanceof IdentifierCallAST)
                    return new IdentifierCallOperableAST((IdentifierCallAST) abstractStatementAST).parseAST(parentAST, syntaxAnalyzer);
                else if(abstractStatementAST instanceof IdentifierInvokeAST)
                    return new IdentifierInvokeOperableAST((IdentifierInvokeAST) abstractStatementAST).parseAST(parentAST, syntaxAnalyzer);
                else if (abstractStatementAST != null) {
                    syntaxAnalyzer.errorHandler().addError(new ASTError<>(abstractStatementAST, "Couldn't parse the operable because it isn't a supported statement."));
                    return null;
                }
        }
        return null;
    }
    
    @Override
    public <T extends AbstractAST<?>> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
    @Override
    public TypeAST.TypeKind binAdd(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "Addition isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind binSub(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "Subtraction isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind binMul(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "Multiplication isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind binDiv(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "Division isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind binMod(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "Modulo isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind assign(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "Assignment isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind addAssign(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "Addition assignment isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind subAssign(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "Subtraction assignment isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind mulAssign(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "Multiplication assignment isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind divAssign(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "Division assignment isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind modAssign(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "Modulo assignment isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind equal(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "The \"equal\" operation isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind notEqual(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "The \"not equal\" operation isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind logicalOr(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "The \"logical or\" operation isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind logicalAnd(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "The \"logical and\" operation isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind postfixAdd(final SemanticAnalyzer semanticAnalyzer) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "The \"post addition\" operator isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind postfixSub(final SemanticAnalyzer semanticAnalyzer) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "The \"post subtraction\" operator isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind prefixAdd(final SemanticAnalyzer semanticAnalyzer) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "The \"pre addition\" operator isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind prefixSub(final SemanticAnalyzer semanticAnalyzer) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "The \"pre subtraction\" operator isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind prefixNegate(final SemanticAnalyzer semanticAnalyzer) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "The \"negate\" operator isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind prefixAffirm(final SemanticAnalyzer semanticAnalyzer) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "The \"affirm\" operator isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind relationalLessThan(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "The \"less than\" operator isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind relationalGreaterThan(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "The \"greater than\" operator isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind relationalLessEqualThan(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "The \"less equal than\" operator isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind relationalGreaterEqualThan(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "The \"greater equal than\" operator isn't supported by this operable."));
        return null;
    }
    
    @Override
    public TypeAST.TypeKind relationalIs(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this, "The \"is\" keyword isn't supported by this operable."));
        return null;
    }
    
}
