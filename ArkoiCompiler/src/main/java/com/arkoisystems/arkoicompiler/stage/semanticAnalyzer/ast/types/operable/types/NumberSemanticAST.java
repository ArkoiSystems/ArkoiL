/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSemanticAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.NumberToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.ArkoiSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.OperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;

public class NumberSemanticAST extends OperableSemanticAST<NumberSyntaxAST>
{
    
    @Getter
    @NotNull
    private final NumberToken number = this.checkNumberToken();
    
    
    @Getter
    @NotNull
    private final TypeKind typeKind = this.checkTypeKind();
    
    
    public NumberSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NotNull final NumberSyntaxAST numberSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, numberSyntaxAST, ASTType.NUMBER);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.printf("%s├── operable: %s%n", indents, this.getNumber().getTokenContent());
        printStream.printf("%s└── type: %s%n", indents, this.getTypeKind().getName());
    }
    
    
    @NotNull
    public NumberToken checkNumberToken() {
        Objects.requireNonNull(this.getSyntaxAST().getNumberToken(), this.getFailedSupplier("syntaxAST.numberToken must not be null."));
        return this.getSyntaxAST().getNumberToken();
    }
    
    
    // TODO: Make better TypeKind semantic (and move it into the syntax analyzer)
    @NotNull
    public TypeKind checkTypeKind() {
        Objects.requireNonNull(this.getSyntaxAST().getNumberToken(), this.getFailedSupplier("syntaxAST.numberToken must not be null."));
        if (this.getSyntaxAST().getNumberToken().getTokenContent().contains("."))
            return TypeKind.FLOAT;
        return TypeKind.INTEGER;
    }
    
}
