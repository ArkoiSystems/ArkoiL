/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTAccess;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.Getter;
import lombok.Setter;

import java.io.PrintStream;

public class IdentifierInvokeOperableSyntaxAST extends AbstractOperableSyntaxAST<TypeKind>
{
    
    @Getter
    @Setter
    private ASTAccess identifierAccess;
    
    
    @Getter
    private IdentifierToken invokedIdentifier;
    
    
    @Getter
    private AbstractSyntaxAST invokePostStatement;
    
    
    public IdentifierInvokeOperableSyntaxAST() {
        super(ASTType.IDENTIFIER_INVOKE_OPERABLE);
        
        this.identifierAccess = ASTAccess.GLOBAL_ACCESS;
    }
    
    
    @Override
    public IdentifierInvokeOperableSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"identifier invoke\" statement because the parsing doesn't start with an identifier."));
            return null;
        } else {
            this.invokedIdentifier = (IdentifierToken) syntaxAnalyzer.currentToken();
            this.setStart(this.invokedIdentifier.getStart());
        }
        
        if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.PERIOD) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"identifier invoke\" statement because the name isn't followed by an period."));
            return null;
        } else {
            this.setEnd(syntaxAnalyzer.currentToken().getEnd());
            syntaxAnalyzer.nextToken();
        }
        
        if (!AbstractStatementSyntaxAST.STATEMENT_PARSER.canParse(this, syntaxAnalyzer)) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"identifier invoke\" statement because the period isn't followed by an valid statement."));
            return null;
        }
        
        final AbstractSyntaxAST abstractSyntaxAST = AbstractStatementSyntaxAST.STATEMENT_PARSER.parse(this, syntaxAnalyzer);
        if (abstractSyntaxAST == null) {
            syntaxAnalyzer.errorHandler().addError(new ParserError<>(AbstractStatementSyntaxAST.STATEMENT_PARSER, this, "Couldn't parse the \"identifier invoke\" statement because an error occurred during the parsing of the statement."));
            return null;
        } else this.invokePostStatement = abstractSyntaxAST;
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "├── access: " + this.getIdentifierAccess());
        printStream.println(indents + "├── identifier: " + this.getInvokedIdentifier().getTokenContent());
        printStream.println(indents + "└── statement:");
        printStream.println(indents + "    └── " + this.getInvokePostStatement().getClass().getSimpleName());
        this.getInvokePostStatement().printSyntaxAST(printStream, indents + "        ");
    }
    
}
