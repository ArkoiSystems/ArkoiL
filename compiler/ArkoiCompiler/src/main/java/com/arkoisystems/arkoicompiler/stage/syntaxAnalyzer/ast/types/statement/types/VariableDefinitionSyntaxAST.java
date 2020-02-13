package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.AnnotationSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.RootSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
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
public class VariableDefinitionSyntaxAST extends AbstractStatementSyntaxAST
{
    
    @Expose
    private final List<AnnotationSyntaxAST> variableAnnotations;
    
    @Expose
    private IdentifierToken variableName;
    
    @Expose
    private ExpressionSyntaxAST variableExpression;
    
    /**
     * This constructor will initialize the statement with the AST-Type
     * "VARIABLE_DEFINITION". This will help to debug problems or check the AST for
     * correct usage. Also it will pass previous parsed annotations through the
     * constructor.
     *
     * @param annotationSyntaxAST
     *         The AnnotationAST which should get used.
     */
    public VariableDefinitionSyntaxAST(final AnnotationSyntaxAST annotationSyntaxAST) {
        super(ASTType.VARIABLE_DEFINITION);
        
        this.variableAnnotations = annotationSyntaxAST.getAnnotationStorage();
    }
    
    /**
     * This constructor will initialize the statement with the AST-Type
     * "VARIABLE_DEFINITION". This will help to debug problems or check the AST for
     * correct syntax.
     */
    public VariableDefinitionSyntaxAST() {
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
    public VariableDefinitionSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof RootSyntaxAST) && !(parentAST instanceof BlockSyntaxAST)) {
            syntaxAnalyzer.errorHandler().addError(new SyntaxASTError<>(parentAST, "Couldn't parse the \"variable definition\" statement because it isn't declared inside the root file or in a block."));
            return null;
        }
    
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null || !syntaxAnalyzer.currentToken().getTokenContent().equals("var")) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"variable definition\" statement because the parsing doesn't start with the \"var\" keyword."));
            return null;
        } else this.setStart(syntaxAnalyzer.currentToken().getStart());
    
        if (syntaxAnalyzer.matchesNextToken(TokenType.IDENTIFIER) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"variable definition\" statement because the \"var\" keyword isn't followed by an variable name."));
            return null;
        } else this.variableName = (IdentifierToken) syntaxAnalyzer.currentToken();
        
        if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.EQUAL) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"variable definition\" statement because the variable name isn't followed by an equal sign for deceleration of the following expression."));
            return null;
        } else syntaxAnalyzer.nextToken();
        
        if (!AbstractExpressionSyntaxAST.EXPRESSION_PARSER.canParse(this, syntaxAnalyzer)) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"variable definition\" statement because the equal sign is followed by an invalid expression."));
            return null;
        }
    
        final ExpressionSyntaxAST expressionAST = AbstractExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, syntaxAnalyzer);
        if (expressionAST == null) {
            syntaxAnalyzer.errorHandler().addError(new ParserError<>(AbstractExpressionSyntaxAST.EXPRESSION_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the \"variable definition\" statement because an error occurred during the parsing of the expression."));
            return null;
        } else this.variableExpression = expressionAST;
    
        if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.SEMICOLON) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"variable definition\" statement because it doesn't end with an semicolon."));
            return null;
        } else this.setEnd(syntaxAnalyzer.currentToken().getEnd());
        return this;
    }
    
}
