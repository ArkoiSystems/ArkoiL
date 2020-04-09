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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ParenthesizedExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.PrefixExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.PrefixOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Objects;

public class PrefixExpressionSemanticAST extends ExpressionSemanticAST<PrefixExpressionSyntaxAST>
{
    
    @Getter
    @NotNull
    private final PrefixOperatorType prefixOperatorType = this.checkPrefixOperatorType();
    @Getter
    @Nullable
    private final OperableSemanticAST<?> rightSideOperable = this.checkRightSideOperable();
    @Getter
    @NotNull
    private final TypeKind typeKind = this.checkTypeKind();
    
    
    public PrefixExpressionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NotNull final PrefixExpressionSyntaxAST prefixExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, prefixExpressionSyntaxAST, ASTType.PREFIX_EXPRESSION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.printf("%s├── operator: %s%n", indents, this.getPrefixOperatorType());
        printStream.printf("%s└── right:%n", indents);
        printStream.printf("%s    └── %s%n", indents, this.getRightSideOperable() != null ? this.getRightSideOperable().getClass().getSimpleName() : null);
        if (this.getRightSideOperable() != null)
            this.getRightSideOperable().printSemanticAST(printStream, indents + "        ");
    }
    
    
    @NotNull
    private TypeKind checkTypeKind() {
        if (this.getRightSideOperable() == null)
            return TypeKind.UNDEFINED;
        return this.getRightSideOperable().getTypeKind();
    }
    
    
    @Nullable
    private OperableSemanticAST<?> checkRightSideOperable() {
        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null."));
        Objects.requireNonNull(this.getSyntaxAST().getRightSideOperable(), this.getFailedSupplier("syntaxAST.rightSideOperable must not be null."));
        
        final OperableSemanticAST<?> operableSemanticAST;
        switch (this.getPrefixOperatorType()) {
            case AFFIRM:
            case NEGATE:
            case PREFIX_ADD:
            case PREFIX_SUB:
                operableSemanticAST = this.getSpecifiedOperable(this.getSyntaxAST().getRightSideOperable(), Arrays.asList(
                        ParenthesizedExpressionSyntaxAST.class,
                        IdentifierCallSyntaxAST.class,
                        NumberSyntaxAST.class
                ));
                break;
            default:
                return this.addError(
                        null,
                        this.getSemanticAnalyzer().getArkoiClass(),
                        this.getSyntaxAST().getRightSideOperable(),
                        "15"
                );
        }
        
        if (operableSemanticAST == null)
            return this.addError(
                    null,
                    this.getSemanticAnalyzer().getArkoiClass(),
                    this.getSyntaxAST().getRightSideOperable(),
                    SemanticErrorType.PREFIX_OPERABLE_NOT_SUPPORTED
            );
        
        if (!operableSemanticAST.getTypeKind().isNumeric())
            return this.addError(
                    null,
                    this.getSemanticAnalyzer().getArkoiClass(),
                    operableSemanticAST,
                    "16"
            );
        
        return operableSemanticAST;
    }
    
    
    @NotNull
    private PrefixOperatorType checkPrefixOperatorType() {
        Objects.requireNonNull(this.getSyntaxAST().getPrefixOperatorType(), this.getFailedSupplier("syntaxAST.prefixOperatorType must not be null."));
        return this.getSyntaxAST().getPrefixOperatorType();
    }
    
}
