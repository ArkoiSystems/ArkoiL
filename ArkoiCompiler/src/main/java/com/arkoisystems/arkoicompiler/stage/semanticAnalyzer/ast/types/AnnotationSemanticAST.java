/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.AnnotationSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.List;

public class AnnotationSemanticAST extends AbstractSemanticAST<AnnotationSyntaxAST>
{
    
    public AnnotationSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final AbstractSemanticAST<?> lastContainerAST, @NotNull final AnnotationSyntaxAST annotationSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, annotationSyntaxAST, ASTType.ANNOTATION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── name: " + this.getAnnotationName().getTokenContent());
        printStream.println(indents + "└── arguments: " + (this.getAnnotationArguments().isEmpty() ? "N/A" : ""));
        for (final IdentifierToken identifierToken : this.getAnnotationArguments())
            printStream.println(indents + "    └── " + identifierToken.getTokenContent());
    }
    
    
    @NotNull
    public List<IdentifierToken> getAnnotationArguments() {
        return this.getSyntaxAST().getAnnotationArguments();
    }
    
    
    @NotNull
    public IdentifierToken getAnnotationName() {
        return this.getSyntaxAST().getAnnotationName();
    }
    
}
