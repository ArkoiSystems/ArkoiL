/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.CollectionOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class CollectionOperableSemanticAST extends AbstractOperableSemanticAST<CollectionOperableSyntaxAST, TypeKind>
{
    
    private List<ExpressionSemanticAST> collectionExpressions;
    
    
    public CollectionOperableSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final CollectionOperableSyntaxAST collectionOperableSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, collectionOperableSyntaxAST, ASTType.COLLECTION_OPERABLE);
    }
    
    
    // TODO: Check for null safety.
    @Override
    public void printSemanticAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "└── expressions: " + (this.getCollectionExpressions().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getCollectionExpressions().size(); index++) {
            final ExpressionSemanticAST expressionSemanticAST = this.getCollectionExpressions().get(index);
            if (index == this.getCollectionExpressions().size() - 1) {
                printStream.println(indents + "    └── " + expressionSemanticAST.getClass().getSimpleName());
                expressionSemanticAST.printSemanticAST(printStream, indents + "        ");
            } else {
                printStream.println(indents + "    ├── " + expressionSemanticAST.getClass().getSimpleName());
                expressionSemanticAST.printSemanticAST(printStream, indents + "    │   ");
                printStream.println(indents + "    │   ");
            }
        }
    }
    
    
    @Override
    public TypeKind getOperableObject() {
        if(this.getCollectionExpressions() == null)
            return null;
        return TypeKind.COLLECTION;
    }
    
    
    public List<ExpressionSemanticAST> getCollectionExpressions() {
        if (this.collectionExpressions == null) {
            this.collectionExpressions = new ArrayList<>();
            
            for (final ExpressionSyntaxAST expressionSyntaxAST : this.getSyntaxAST().getCollectionExpressions()) {
                final ExpressionSemanticAST expressionSemanticAST = new ExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), expressionSyntaxAST);
                expressionSemanticAST.getOperableObject();
    
                if (expressionSemanticAST.isFailed())
                    this.failed();
                this.collectionExpressions.add(expressionSemanticAST);
            }
            return this.isFailed() ? null : this.collectionExpressions;
        }
        return this.collectionExpressions;
    }
    
}
