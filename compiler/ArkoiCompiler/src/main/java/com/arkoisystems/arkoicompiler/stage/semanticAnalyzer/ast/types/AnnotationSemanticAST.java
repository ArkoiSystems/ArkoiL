/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.AnnotationSyntaxAST;
import lombok.Getter;
import lombok.Setter;

import java.io.PrintStream;
import java.util.List;

public class AnnotationSemanticAST extends AbstractSemanticAST<AnnotationSyntaxAST>
{
    
    public AnnotationSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final AnnotationSyntaxAST annotationSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, annotationSyntaxAST, ASTType.ANNOTATION);
    }
    
    
    public List<IdentifierToken> getAnnotationArguments() {
        return this.getSyntaxAST().getAnnotationArguments();
    }
    
    
    public IdentifierToken getAnnotationName() {
        return this.getSyntaxAST().getAnnotationName();
    }
    
}
