/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.AnnotationParser;
import lombok.Getter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class AnnotationSyntaxAST extends AbstractSyntaxAST
{
    
    public static AnnotationParser ANNOTATION_PARSER = new AnnotationParser();
    
    
    @Getter
    private final List<AnnotationSyntaxAST> annotationStorage;
    
    
    @Getter
    private IdentifierToken annotationName;
    
    
    @Getter
    private List<IdentifierToken> annotationArguments;
    
    
    public AnnotationSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer, final List<AnnotationSyntaxAST> annotationStorage) {
        super(syntaxAnalyzer, ASTType.ANNOTATION);
        
        this.annotationStorage = annotationStorage;
        
        this.annotationArguments = new ArrayList<>();
        this.annotationStorage.add(this);
    }
    
    
    /**
     * This constructor will initialize the AnnotationAST with the AST-Type "ANNOTATION".
     * This will help to debug problems or check the AST for correct syntax. It wont pass
     * variables through the constructor, but initializes default variables like the
     * AnnotationStorage.
     */
    public AnnotationSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ANNOTATION);
        
        this.annotationArguments = new ArrayList<>();
        this.annotationStorage = new ArrayList<>();
        this.annotationStorage.add(this);
    }
    
    
    /**AnnotationSemantic
     * This method will parse the AnnotationAST and checks it for the correct syntax. This
     * AST can just be used in the RootAST but needs to be followed by an other
     * AnnotationAST or an FunctionDefinitionAST/VariableDefinitionAST. An Annotation has
     * a name and arguments. You can leave the arguments empty but the name must be
     * present every time.
     * <p>
     * An example for a AnnotationAST:
     * <p>
     * &#64;native[default]
     * <p>
     * fun println<>(message: string);
     *
     * @param parentAST
     *         The parent of the AST. With it you can check for correct usage of the
     *         statement.
     * @return It will return null if an error occurred or an AnnotationAST if it parsed
     *         until to the end.
     */
    @Override
    public AbstractSyntaxAST parseAST(final AbstractSyntaxAST parentAST) {
        if (!(parentAST instanceof RootSyntaxAST)) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    "Couldn't parse the Annotation because it isn't declared inside the root file."
            );
            return null;
        }
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.AT_SIGN) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    "Couldn't parse the Annotation because the parsing doesn't start with an at sign aka. \"@\"."
            );
            return null;
        } else this.setStart(this.getSyntaxAnalyzer().currentToken().getStart());
    
        if (this.getSyntaxAnalyzer().matchesNextToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    "Couldn't parse the Annotation because the at sign isn't followed by an name for the annotation."
            );
            return null;
        } else
            this.annotationName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_BRACKET) != null) {
            this.getSyntaxAnalyzer().nextToken(2);
        
            while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
                if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACKET) != null)
                    break;
            
                if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            this.getSyntaxAnalyzer().currentToken(),
                            "Couldn't parse the Annotation because you can't define a non IdentifierToken inside the arguments section beside a comma after an argument if it should get followed by an extra one."
                    );
                    return null;
                } else {
                    this.annotationArguments.add((IdentifierToken) this.getSyntaxAnalyzer().currentToken());
                    this.getSyntaxAnalyzer().nextToken();
                }
            
                if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.COMMA) != null)
                    this.getSyntaxAnalyzer().nextToken();
                else if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACKET) != null)
                    break;
                else {
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            this.getSyntaxAnalyzer().currentToken(),
                            "Couldn't parse the Annotation because you can't declare something else then a closing bracket and a comma after an argument."
                    );
                    return null;
                }
            }
        
            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACKET) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        "Couldn't parse the Annotation because the arguments section doesn't end with a closing bracket."
                );
                return null;
            }
        }
    
        this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        this.getSyntaxAnalyzer().nextToken();
    
        if (ANNOTATION_PARSER.canParse(parentAST, this.getSyntaxAnalyzer()))
            return new AnnotationSyntaxAST(this.getSyntaxAnalyzer(), this.annotationStorage).parseAST(parentAST);
    
        if (!AbstractStatementSyntaxAST.STATEMENT_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    "Couldn't parse the Annotation because an there is no parsable statement after it."
            );
            return null;
        }
        
        if (!this.getSyntaxAnalyzer().currentToken().getTokenContent().equals("fun") && !this.getSyntaxAnalyzer().currentToken().getTokenContent().equals("var")) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    "Couldn't parse the Annotation because it isn't followed by an function or variable definition."
            );
            return null;
        } else if (this.getSyntaxAnalyzer().currentToken().getTokenContent().equals("fun")) {
            return new FunctionDefinitionSyntaxAST(this.getSyntaxAnalyzer(), this.getAnnotationStorage()).parseAST(parentAST);
        } else {
            return new VariableDefinitionSyntaxAST(this.getSyntaxAnalyzer(), this.getAnnotationStorage()).parseAST(parentAST);
        }
    }
    
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "├── name: " + this.getAnnotationName().getTokenContent());
        printStream.println(indents + "└── arguments: " + (this.getAnnotationArguments().isEmpty() ? "N/A" : ""));
        for (final IdentifierToken identifierToken : this.getAnnotationArguments())
            printStream.println(indents + "    └── " + identifierToken.getTokenContent());
    }
    
}
