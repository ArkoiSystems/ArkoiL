/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.ArkoiSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.OperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.CastExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ParenthesizedExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Objects;

public class CastExpressionSemanticAST extends ExpressionSemanticAST<CastExpressionSyntaxAST>
{
    
    @Getter
    @Nullable
    private final OperableSemanticAST<?> leftSideOperable = this.checkLeftSideOperable();
    
    
    @Getter
    @NotNull
    private final TypeKind typeKind = this.checkTypeKind();
    
    
    public CastExpressionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NotNull final CastExpressionSyntaxAST castExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, castExpressionSyntaxAST, ASTType.CAST_EXPRESSION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.printf("%s├── left:%n", indents);
        printStream.printf("%s│   └── %s%n", indents, this.getLeftSideOperable() != null ? this.getLeftSideOperable().getClass().getSimpleName() : null);
        if (this.getLeftSideOperable() != null)
            this.getLeftSideOperable().printSemanticAST(printStream, indents + "│       ");
        printStream.printf("%s└── operator: %s%n", indents, this.getTypeKind());
    }
    
    
    @NotNull
    private TypeKind checkTypeKind() {
        Objects.requireNonNull(this.getSyntaxAST().getTypeKind(), this.getFailedSupplier("syntaxAST.typeKind must not be null."));
        return this.getSyntaxAST().getTypeKind();
    }
    
    
    @Nullable
    private OperableSemanticAST<?> checkLeftSideOperable() {
        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null."));
        Objects.requireNonNull(this.getSyntaxAST().getLeftSideOperable(), this.getFailedSupplier("syntaxAST.leftSideOperable must not be null."));
        
        final OperableSemanticAST<?> operableSemanticAST = this.getSpecifiedOperable(this.getSyntaxAST().getLeftSideOperable(), Arrays.asList(
                ParenthesizedExpressionSyntaxAST.class,
                NumberSyntaxAST.class
        ));
        
        if (operableSemanticAST != null)
            return operableSemanticAST;
        
        return this.addError(
                null,
                this.getSemanticAnalyzer().getArkoiClass(),
                this.getSyntaxAST().getLeftSideOperable(),
                SemanticErrorType.CAST_OPERABLE_NOT_SUPPORTED
        );
    }
    
}
