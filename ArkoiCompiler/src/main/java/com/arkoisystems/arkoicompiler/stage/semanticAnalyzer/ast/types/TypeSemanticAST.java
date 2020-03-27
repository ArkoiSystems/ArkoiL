/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class TypeSemanticAST extends AbstractSemanticAST<TypeSyntaxAST>
{
    
    public TypeSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final AbstractSemanticAST<?> lastContainerAST, @NotNull final TypeSyntaxAST typeSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, typeSyntaxAST, ASTType.TYPE);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) { }
    
    
    public boolean isArray() {
        return this.getSyntaxAST().isArray();
    }
    
    
    @NotNull
    public TypeKind getTypeKind() {
        return TypeKind.VOID;
//        return this.getSyntaxAST().getTypeKeywordToken();
    }
    
}
