/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class AbstractOperableSemanticAST<T1 extends AbstractSyntaxAST> extends AbstractSemanticAST<T1>
{
    
    public AbstractOperableSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final AbstractSemanticAST<?> lastContainerAST, @NotNull final T1 syntaxAST, @NotNull final ASTType astType) {
        super(semanticAnalyzer, lastContainerAST, syntaxAST, astType);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull PrintStream printStream, @NotNull String indents) { }
    
    
    @Nullable
    public TypeKind getTypeKind() {
        return null;
    }
    
}
