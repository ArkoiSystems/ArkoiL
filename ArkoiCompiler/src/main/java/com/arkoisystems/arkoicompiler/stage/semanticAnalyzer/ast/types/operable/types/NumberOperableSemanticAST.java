/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.NumberToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class NumberOperableSemanticAST extends AbstractOperableSemanticAST<NumberOperableSyntaxAST>
{
    
    public NumberOperableSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final AbstractSemanticAST<?> lastContainerAST, @NotNull final NumberOperableSyntaxAST numberOperableSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, numberOperableSyntaxAST, ASTType.NUMBER_OPERABLE);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── operable: " + this.getNumber().getTokenContent());
        printStream.println(indents + "└── type: " + this.getTypeKind().getName());
    }
    
    @NotNull
    public NumberToken getNumber() {
        return this.getSyntaxAST().getNumberToken();
    }
    
    @NotNull
    @Override
    public TypeKind getTypeKind() {
        final NumberToken numberToken = this.getSyntaxAST().getNumberToken();
        if (numberToken.getTokenContent().contains("."))
            return TypeKind.FLOAT;
        return TypeKind.INTEGER;
    }
    
}
