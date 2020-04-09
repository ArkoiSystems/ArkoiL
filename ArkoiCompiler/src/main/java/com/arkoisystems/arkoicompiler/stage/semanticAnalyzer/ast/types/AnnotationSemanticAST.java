/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSemanticAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.ArkoiSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.AnnotationSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.ArgumentListSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;

public class AnnotationSemanticAST extends ArkoiSemanticAST<AnnotationSyntaxAST>
{
    
    @Getter
    @NotNull
    private final IdentifierToken annotationName = this.checkAnnotationName();
    
    
    @Getter
    @NotNull
    private final ArgumentListSyntaxAST annotationArguments = this.checkAnnotationArguments();
    
    
    public AnnotationSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NotNull final AnnotationSyntaxAST annotationSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, annotationSyntaxAST, ASTType.ANNOTATION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.printf("%s├── name: %s%n", indents, this.getAnnotationName().getTokenContent());
        printStream.printf("%s└── arguments: %s%n", indents, this.getAnnotationArguments().getArguments().isEmpty() ? "N/A" : "");
        for (int index = 0; index < this.getAnnotationArguments().getArguments().size(); index++) {
            final ArkoiSyntaxAST arkoiSyntaxAST = this.getAnnotationArguments().getArguments().get(index);
            if (index == this.getAnnotationArguments().getArguments().size() - 1) {
                printStream.printf("%s    └── %s%n", indents, arkoiSyntaxAST.getClass().getSimpleName());
                arkoiSyntaxAST.printSyntaxAST(printStream, indents + "        ");
            } else {
                printStream.printf("%s    ├── %s%n", indents, arkoiSyntaxAST.getClass().getSimpleName());
                arkoiSyntaxAST.printSyntaxAST(printStream, indents + "    │   ");
                printStream.printf("%s    │%n", indents);
            }
        }
    }
    
    
    @NotNull
    public ArgumentListSyntaxAST checkAnnotationArguments() {
        Objects.requireNonNull(this.getSyntaxAST().getAnnotationArguments(), this.getFailedSupplier("syntaxAST.annotationArguments must not be null."));
        return this.getSyntaxAST().getAnnotationArguments();
    }
    
    
    @NotNull
    public IdentifierToken checkAnnotationName() {
        Objects.requireNonNull(this.getSyntaxAST().getAnnotationName(), this.getFailedSupplier("syntaxAST.annotationName must not be null."));
        return this.getSyntaxAST().getAnnotationName();
    }
    
}
