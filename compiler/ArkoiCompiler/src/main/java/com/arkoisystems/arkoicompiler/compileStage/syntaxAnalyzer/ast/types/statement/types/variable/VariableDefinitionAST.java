package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.variable;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.statements.variable.VariableDefinitionSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.AnnotationAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.BlockAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.RootAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expression.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expression.types.ExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.VariableStatementAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
public class VariableDefinitionAST extends VariableStatementAST<VariableDefinitionSemantic>
{
    
    private final List<AnnotationAST> variableAnnotations;
    
    @Expose
    private IdentifierToken variableNameToken;
    
    @Expose
    private ExpressionAST expressionAST;
    
    /**
     * This constructor will initialize the statement with the AST-Type
     * "VARIABLE_DEFINITION". This will help to debug problems or check the AST for
     * correct usage. Also it will pass previous parsed annotations through the
     * constructor.
     *
     * @param variableAnnotations
     *         The annotations list which already got parsed.
     */
    public VariableDefinitionAST(final List<AnnotationAST> variableAnnotations) {
        super(ASTType.VARIABLE_DEFINITION);
        
        this.variableAnnotations = variableAnnotations;
    }
    
    /**
     * This constructor will initialize the statement with the AST-Type
     * "VARIABLE_DEFINITION". This will help to debug problems or check the AST for
     * correct syntax.
     */
    public VariableDefinitionAST() {
        super(ASTType.VARIABLE_DEFINITION);
        
        this.variableAnnotations = new ArrayList<>();
    }
    
    /**
     * This method will parse the "variable definition" statement and checks it for the
     * correct syntax. This statement can just be used inside the RootAST or inside a
     * BlockAST.
     * <p>
     * An example for this statement:
     * <p>
     * var test_string = "Hello World"
     * <p>
     * fun main<int>(args: string[]) { println(this.test_string); return 0; }
     *
     * @param parentAST
     *         The parent of the AST. With it you can check for correct usage of the
     *         statement.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It will return null if an error occurred or an VariableDefinitionAST if it
     *         parsed until to the end.
     */
    @Override
    public VariableDefinitionAST parseAST(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof RootAST) && !(parentAST instanceof BlockAST)) {
            syntaxAnalyzer.errorHandler().addError(new ASTError<>(parentAST, "Couldn't parse the \"variable definition\" statement because it isn't declared inside the root file or in a block."));
            return null;
        }
    
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null || !syntaxAnalyzer.currentToken().getTokenContent().equals("var")) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"variable definition\" statement because the parsing doesn't start with the \"var\" keyword."));
            return null;
        } else this.setStart(syntaxAnalyzer.currentToken().getStart());
    
        if (syntaxAnalyzer.matchesNextToken(TokenType.IDENTIFIER) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"variable definition\" statement because the \"var\" keyword isn't followed by an variable name."));
            return null;
        } else this.variableNameToken = (IdentifierToken) syntaxAnalyzer.currentToken();
        
        if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.EQUAL) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"variable definition\" statement because the variable name isn't followed by an equal sign for deceleration of the following expression."));
            return null;
        } else syntaxAnalyzer.nextToken();
        
        if (!AbstractExpressionAST.EXPRESSION_PARSER.canParse(this, syntaxAnalyzer)) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"variable definition\" statement because the equal sign is followed by an invalid expression."));
            return null;
        }
    
        final ExpressionAST expressionAST = AbstractExpressionAST.EXPRESSION_PARSER.parse(this, syntaxAnalyzer);
        if (expressionAST == null) {
            syntaxAnalyzer.errorHandler().addError(new ParserError<>(AbstractExpressionAST.EXPRESSION_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the \"variable definition\" statement because an error occurred during the parsing of the expression."));
            return null;
        } else this.expressionAST = expressionAST;
    
        if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.SEMICOLON) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"variable definition\" statement because it doesn't end with an semicolon."));
            return null;
        } else this.setEnd(syntaxAnalyzer.currentToken().getEnd());
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    /**
     * This method is just overwritten to prevent default code execution. So it will just
     * return the input and doesn't check anything.
     *
     * @param toAddAST
     *         The AST which should get added to the "VariableDefinitionAST".
     * @param syntaxAnalyzer
     *         The SyntaxAnalyzer which should get used if you want to compare Tokens.
     * @param <T>
     *         The Type of the AST which should be added to the "VariableDefinitionAST".
     *
     * @return It will just return the input "toAddAST" because you can't add ASTs to a
     *         VariableDefinitionAST.
     */
    @Override
    public <T extends AbstractAST<?>> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
}
