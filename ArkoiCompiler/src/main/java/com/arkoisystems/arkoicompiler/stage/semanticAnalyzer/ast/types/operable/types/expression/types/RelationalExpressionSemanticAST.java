/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.RelationalExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class RelationalExpressionSemanticAST extends ExpressionSemanticAST<RelationalExpressionSyntaxAST>
{
    
    public RelationalExpressionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NotNull final RelationalExpressionSyntaxAST relationalExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, relationalExpressionSyntaxAST, ASTType.RELATIONAL_EXPRESSION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) { }
    
    
    @NotNull
    @Override
    public TypeKind getTypeKind() {
        return TypeKind.UNDEFINED;
    }
    
}
