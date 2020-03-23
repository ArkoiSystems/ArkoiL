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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.AnnotationParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnnotationSyntaxAST extends AbstractSyntaxAST
{
    
    public static AnnotationParser ANNOTATION_PARSER = new AnnotationParser();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private List<AnnotationSyntaxAST> annotationStorage;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private IdentifierToken annotationName;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private List<IdentifierToken> annotationArguments;
    
    
    /**
     * This constructor will initialize the AnnotationAST with the AST-Type "ANNOTATION".
     * This will help to debug problems or check the AST for correct syntax. It wont pass
     * variables through the constructor, but initializes default variables like the
     * AnnotationStorage.
     */
    protected AnnotationSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ANNOTATION);
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
    public Optional<? extends AbstractSyntaxAST> parseAST(@NonNull final AbstractSyntaxAST parentAST) {
        if (!(parentAST instanceof RootSyntaxAST)) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_WRONG_PARENT
            );
            return Optional.empty();
        }
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.AT_SIGN) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_WRONG_START
            );
            return Optional.empty();
        }
        this.setStart(this.getSyntaxAnalyzer().currentToken().getStart());
    
        if (this.getSyntaxAnalyzer().matchesNextToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_NO_NAME
            );
            return Optional.empty();
        }
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
                            SyntaxErrorType.ANNOTATION_NO_COMMA_SEPARATION
                    );
                    return Optional.empty();
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
                            SyntaxErrorType.ANNOTATION_UNSUPPORTED_TOKEN_INSIDE
                    );
                    return Optional.empty();
                }
            }
        
            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACKET) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.ANNOTATION_WRONG_ENDING
                );
                return Optional.empty();
            }
        }
    
        this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        this.getSyntaxAnalyzer().nextToken();
    
        if (ANNOTATION_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
            this.annotationStorage.add(this);
            return AnnotationSyntaxAST
                    .builder(this.getSyntaxAnalyzer())
                    .annotations(this.annotationStorage)
                    .build()
                    .parseAST(parentAST);
        }
    
        if (!AbstractStatementSyntaxAST.STATEMENT_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_NO_PARSEABLE_STATEMENT
            );
            return Optional.empty();
        }
        
        if (!this.getSyntaxAnalyzer().currentToken().getTokenContent().equals("fun") && !this.getSyntaxAnalyzer().currentToken().getTokenContent().equals("var")) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_NO_VARIABLE_OR_FUNCTION
            );
            return Optional.empty();
        } else if (this.getSyntaxAnalyzer().currentToken().getTokenContent().equals("fun")) {
            return new FunctionDefinitionSyntaxAST(this.getSyntaxAnalyzer(), this.getAnnotationStorage()).parseAST(parentAST);
        } else {
            return new VariableDefinitionSyntaxAST(this.getSyntaxAnalyzer(), this.getAnnotationStorage()).parseAST(parentAST);
        }
    }
    
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) {
        printStream.println(indents + "├── name: " + this.getAnnotationName().getTokenContent());
        printStream.println(indents + "└── arguments: " + (this.getAnnotationArguments().isEmpty() ? "N/A" : ""));
        for (final IdentifierToken identifierToken : this.getAnnotationArguments())
            printStream.println(indents + "    └── " + identifierToken.getTokenContent());
    }
    
    
    public static AnnotationSyntaxASTBuilder builder(final SyntaxAnalyzer syntaxAnalyzer) {
        return new AnnotationSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static AnnotationSyntaxASTBuilder builder() {
        return new AnnotationSyntaxASTBuilder();
    }
    
    
    public static class AnnotationSyntaxASTBuilder {
        
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        private List<AnnotationSyntaxAST> annotationStorage;
    
    
        private List<IdentifierToken> annotationArguments;
        
    
        private IdentifierToken annotationName;
        
        
        private int start, end;
        
        
        public AnnotationSyntaxASTBuilder(final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
            
            this.annotationArguments = new ArrayList<>();
            this.annotationStorage = new ArrayList<>();
        }
        
        
        public AnnotationSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public AnnotationSyntaxASTBuilder annotations(List<AnnotationSyntaxAST> annotationStorage) {
            this.annotationStorage = annotationStorage;
            return this;
        }
        
        
        public AnnotationSyntaxASTBuilder arguments(List<IdentifierToken> annotationArguments) {
            this.annotationArguments = annotationArguments;
            return this;
        }
    
    
        public AnnotationSyntaxASTBuilder name(IdentifierToken annotationName) {
            this.annotationName = annotationName;
            return this;
        }
        
        
        public AnnotationSyntaxASTBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public AnnotationSyntaxASTBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public AnnotationSyntaxAST build() {
            final AnnotationSyntaxAST annotationSyntaxAST = new AnnotationSyntaxAST(this.syntaxAnalyzer);
            annotationSyntaxAST.setAnnotationArguments(this.annotationArguments);
            annotationSyntaxAST.setAnnotationStorage(this.annotationStorage);
            annotationSyntaxAST.setAnnotationName(this.annotationName);
            annotationSyntaxAST.setStart(this.start);
            annotationSyntaxAST.setEnd(this.end);
            return annotationSyntaxAST;
        }
        
    }
    
}
