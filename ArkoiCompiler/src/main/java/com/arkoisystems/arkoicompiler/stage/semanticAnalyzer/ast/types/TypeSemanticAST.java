/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.ArkoiSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;

public class TypeSemanticAST extends ArkoiSemanticAST<TypeSyntaxAST>
{
    
    @Getter
    private final boolean isArray = this.checkIsArray();
    
    
    @Getter
    @NotNull
    private final TypeKind typeKind = this.checkTypeKind();
    
    
    public TypeSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NotNull final TypeSyntaxAST typeSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, typeSyntaxAST, ASTType.TYPE);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.printf("%s└── keyword: %s%s%n", indents, this.getTypeKind().name(), this.isArray() ? "[]" : "");
    }
    
    
    private boolean checkIsArray() {
        return this.getSyntaxAST().isArray();
    }
    
    
    @NotNull
    public TypeKind checkTypeKind() {
        Objects.requireNonNull(this.getSyntaxAST().getTypeKeywordToken(), this.getFailedSupplier("syntaxAST.typeKeywordToken must not be null."));
        Objects.requireNonNull(this.getSyntaxAST().getTypeKeywordToken().getTypeKind(), this.getFailedSupplier("syntaxAST.typeKeywordToken.typeKind must not be null."));
        return this.getSyntaxAST().getTypeKeywordToken().getTypeKind();
    }
    
}
