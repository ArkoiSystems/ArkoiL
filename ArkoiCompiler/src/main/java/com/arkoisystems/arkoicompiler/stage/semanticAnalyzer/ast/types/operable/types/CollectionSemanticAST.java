/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.ArkoiSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.OperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.CollectionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class CollectionSemanticAST extends OperableSemanticAST<CollectionSyntaxAST>
{
    
    @Getter
    @Nullable
    private final List<ExpressionSemanticAST<?>> collectionExpressions = this.checkCollectionExpressions();
    
    
    @Getter
    @NotNull
    private final TypeKind typeKind = this.checkTypeKind();
    
    
    public CollectionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NonNull final CollectionSyntaxAST collectionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, collectionSyntaxAST, ASTType.COLLECTION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.printf("%s└── expressions: %s%n", indents, this.getCollectionExpressions() != null ? (this.getCollectionExpressions().isEmpty() ? "N/A" : "") : null);
        if (this.getCollectionExpressions() != null) {
            for (int index = 0; index < this.getCollectionExpressions().size(); index++) {
                final ExpressionSemanticAST<?> expressionSemanticAST = this.getCollectionExpressions().get(index);
                if (index == this.getCollectionExpressions().size() - 1) {
                    printStream.printf("%s    └── %s%n", indents, expressionSemanticAST.getClass().getSimpleName());
                    expressionSemanticAST.printSemanticAST(printStream, indents + "        ");
                } else {
                    printStream.printf("%s    ├── %s%n", indents, expressionSemanticAST.getClass().getSimpleName());
                    expressionSemanticAST.printSemanticAST(printStream, indents + "    │   ");
                    printStream.printf("%s    │   %n", indents);
                }
            }
        }
    }
    
    
    @Nullable
    public List<ExpressionSemanticAST<?>> checkCollectionExpressions() {
        final List<ExpressionSemanticAST<?>> collectionExpressions = new ArrayList<>();
        
        for (final OperableSyntaxAST operableSyntaxAST : this.getSyntaxAST().getCollectionExpressions()) {
            final ExpressionSemanticAST<?> expressionSemanticAST = new ExpressionSemanticAST<>(
                    this.getSemanticAnalyzer(),
                    this.getLastContainerAST(),
                    operableSyntaxAST
            );
            
            if (expressionSemanticAST.isFailed())
                this.failed();
            collectionExpressions.add(expressionSemanticAST);
        }
        return collectionExpressions;
    }
    
    
    @NotNull
    public TypeKind checkTypeKind() {
        return TypeKind.COLLECTION;
    }
    
}
