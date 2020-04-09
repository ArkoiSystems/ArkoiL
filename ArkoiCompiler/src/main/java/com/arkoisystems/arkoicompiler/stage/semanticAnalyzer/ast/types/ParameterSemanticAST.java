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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.ParameterSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;

public class ParameterSemanticAST extends ArkoiSemanticAST<ParameterSyntaxAST>
{
    
    @Getter
    @NotNull
    private final TypeSemanticAST parameterType = this.checkParameterType();
    
    
    @Getter
    @NotNull
    private final IdentifierToken parameterName = this.checkParameterName();
    
    
    public ParameterSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @NotNull final ICompilerSemanticAST<?> lastContainerAST, @NotNull final ParameterSyntaxAST parameterSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, parameterSyntaxAST, ASTType.PARAMETER);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.printf("%s├── name: %s%n", indents, this.getParameterName().getTokenContent());
        printStream.printf("%s└── type:%n", indents);
        this.getParameterType().printSemanticAST(printStream, indents + "    ");
    }
    
    
    @NotNull
    public IdentifierToken checkParameterName() {
        Objects.requireNonNull(this.getSyntaxAST().getParameterName(), this.getFailedSupplier("syntaxAST.parameterName must not be null."));
        return this.getSyntaxAST().getParameterName();
    }
    
    
    @NotNull
    public TypeSemanticAST checkParameterType() {
        Objects.requireNonNull(this.getSyntaxAST().getParameterType(), this.getFailedSupplier("syntaxAST.parameterType must not be null."));
        
        final TypeSemanticAST typeSemanticAST = new TypeSemanticAST(
                this.getSemanticAnalyzer(),
                this.getLastContainerAST(),
                this.getSyntaxAST().getParameterType()
        );
        
        if (typeSemanticAST.isFailed())
            this.failed();
        return typeSemanticAST;
    }
    
}
