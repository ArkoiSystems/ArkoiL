/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.ISyntaxParser;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiASTNode;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.StatementAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RootAST extends ArkoiASTNode
{
    
    public static ISyntaxParser[] ROOT_PARSERS = new ISyntaxParser[] {
            AnnotationAST.ANNOTATION_PARSER,
            StatementAST.STATEMENT_PARSER,
    };
    
    
    @Getter
    @NotNull
    private final List<IASTNode> astNodes = new ArrayList<>();
    
    
    public RootAST(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ROOT);
    }
    
    
    @NotNull
    @Override
    public RootAST parseAST(@NotNull IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        Objects.requireNonNull(this.getMarkerFactory());
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken(false));
        this.getMarkerFactory().mark(this.getStartToken());
        
        main_loop:
        while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
            for (final ISyntaxParser parser : ROOT_PARSERS) {
                if (!parser.canParse(this, this.getSyntaxAnalyzer()))
                    continue;
                
                final IASTNode astNode = parser.parse(this, this.getSyntaxAnalyzer());
                this.getMarkerFactory().addFactory(astNode.getMarkerFactory());
                
                if (astNode.isFailed()) {
                    this.skipToNextValidToken();
                    continue main_loop;
                }
                
                this.getAstNodes().add(astNode);
                this.getSyntaxAnalyzer().nextToken();
                continue main_loop;
            }
            
            this.addError(
                    null,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ROOT_NO_PARSER_FOUND
            );
            this.skipToNextValidToken();
        }
    
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void accept(@NotNull final IVisitor visitor) {
        visitor.visit(this);
    }
    
}
