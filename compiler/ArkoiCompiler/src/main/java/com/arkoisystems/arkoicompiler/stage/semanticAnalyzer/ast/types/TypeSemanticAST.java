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

import java.io.PrintStream;

public class TypeSemanticAST extends AbstractSemanticAST<TypeSyntaxAST>
{
    
    public TypeSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final TypeSyntaxAST typeSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, typeSyntaxAST, ASTType.TYPE);
    }
    
    
    @Override
    public void printSemanticAST(final PrintStream printStream, final String indents) { }
    
    
    public boolean isArray() {
        return this.getSyntaxAST().isArray();
    }
    
    
    public TypeKind getTypeKind() {
        return this.getSyntaxAST().getTypeKind();
    }
    
}
