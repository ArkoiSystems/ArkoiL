/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTAccess;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.io.PrintStream;

public class IdentifierCallOperableSyntaxAST extends AbstractOperableSyntaxAST<TypeKind>
{
    
    @Getter
    private ASTAccess identifierAccess;
    
    
    @Getter
    private IdentifierToken calledIdentifier;
    
    
    public IdentifierCallOperableSyntaxAST() {
        super(ASTType.IDENTIFIER_CALL_OPERABLE);
        
        this.identifierAccess = ASTAccess.GLOBAL_ACCESS;
    }
    
    
    /**
     * This method will parse the "identifier call" statement and checks it for correct
     * syntax. This statement can be used everywhere it is needed but especially in
     * AbstractExpressionAST.
     *
     * @param parentAST
     *         The parent of the AST. With it you can check for correct usage of the
     *         statement.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It will return null if an error occurred or an IdentifierCallStatementAST
     *         if it parsed until to the end.
     */
    @Override
    public IdentifierCallOperableSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"identifier call\" statement because the parsing doesn't start with an Identifier."));
            return null;
        } else {
            this.calledIdentifier = (IdentifierToken) syntaxAnalyzer.currentToken();
            
            this.setStart(this.getCalledIdentifier().getStart());
            this.setEnd(this.getCalledIdentifier().getEnd());
        }
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "├── access: " + this.getIdentifierAccess());
        printStream.println(indents + "└── identifier: " + this.getCalledIdentifier().getTokenContent());
    }
    
}
