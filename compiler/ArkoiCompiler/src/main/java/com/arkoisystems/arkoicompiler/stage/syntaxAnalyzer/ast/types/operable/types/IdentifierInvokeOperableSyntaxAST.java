/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
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
    
    
    public IdentifierInvokeOperableSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.IDENTIFIER_INVOKE_OPERABLE);
        
        this.identifierAccess = ASTAccess.GLOBAL_ACCESS;
    }
    
    
    @Override
    public IdentifierInvokeOperableSyntaxAST parseAST(final AbstractSyntaxAST parentAST) {
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.IDENTIFIER_INVOKE_WRONG_START
            );
            return null;
        }
        
        this.invokedIdentifier = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
        this.setStart(this.invokedIdentifier.getStart());
        
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.PERIOD) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.IDENTIFIER_INVOKE_NO_SEPARATOR
            );
            return null;
        }
        
        this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        this.getSyntaxAnalyzer().nextToken();
        
        if (!AbstractStatementSyntaxAST.STATEMENT_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.IDENTIFIER_INVOKE_NO_VALID_STATEMENT
            );
            return null;
        }
        
        final AbstractSyntaxAST abstractSyntaxAST = AbstractStatementSyntaxAST.STATEMENT_PARSER.parse(this, this.getSyntaxAnalyzer());
        if (abstractSyntaxAST == null)
            return null;
        
        this.invokePostStatement = abstractSyntaxAST;
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
