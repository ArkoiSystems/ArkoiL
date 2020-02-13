package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.OperableParser;
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
public class AbstractOperableSyntaxAST<O> extends AbstractSyntaxAST
{
    
    public static OperableParser OPERABLE_PARSER = new OperableParser();
    
    @Expose
    private O operableObject;
    
    public AbstractOperableSyntaxAST(final ASTType astType) {
        super(astType);
    }
    
    @Override
    public AbstractOperableSyntaxAST<?> parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractToken currentToken = syntaxAnalyzer.currentToken();
        switch (currentToken.getTokenType()) {
            case STRING_LITERAL:
                return new StringOperableSyntaxAST().parseAST(parentAST, syntaxAnalyzer);
            case NUMBER_LITERAL:
                return new NumberOperableSyntaxAST().parseAST(parentAST, syntaxAnalyzer);
            case SYMBOL:
                if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.OPENING_BRACKET) != null)
                    return new CollectionOperableSyntaxAST().parseAST(parentAST, syntaxAnalyzer);
                else {
                    syntaxAnalyzer.errorHandler().addError(new TokenError(currentToken, "Couldn't parse the operable because the SymbolType isn't supported."));
                    return null;
                }
            case IDENTIFIER:
                if (!AbstractStatementSyntaxAST.STATEMENT_PARSER.canParse(parentAST, syntaxAnalyzer)) {
                    syntaxAnalyzer.errorHandler().addError(new ParserError<>(AbstractStatementSyntaxAST.STATEMENT_PARSER, parentAST.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the operable statement because it isn't parsable."));
                    return null;
                }
                
                final AbstractSyntaxAST abstractSyntaxAST = AbstractStatementSyntaxAST.STATEMENT_PARSER.parse(parentAST, syntaxAnalyzer);
                if (abstractSyntaxAST instanceof IdentifierCallOperableSyntaxAST)
                    return (IdentifierCallOperableSyntaxAST) abstractSyntaxAST;
                else if (abstractSyntaxAST instanceof FunctionInvokeOperableSyntaxAST)
                    return (FunctionInvokeOperableSyntaxAST) abstractSyntaxAST;
                else if (abstractSyntaxAST instanceof IdentifierInvokeOperableSyntaxAST)
                    return (IdentifierInvokeOperableSyntaxAST) abstractSyntaxAST;
                else if (abstractSyntaxAST != null) {
                    syntaxAnalyzer.errorHandler().addError(new SyntaxASTError<>(abstractSyntaxAST, "Couldn't parse the operable because it isn't a supported statement."));
                    return null;
                }
        }
        return null;
    }
    
}
