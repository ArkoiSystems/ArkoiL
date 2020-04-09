/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.api.ISyntaxParser;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.AnnotationSyntaxAST;
import org.jetbrains.annotations.NotNull;

public class AnnotationParser implements ISyntaxParser
{
    
    @NotNull
    @Override
    public ICompilerSyntaxAST parse(@NotNull final ICompilerSyntaxAST parentAST, @NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return AnnotationSyntaxAST
                .builder(syntaxAnalyzer)
                .build()
                .parseAST(parentAST);
    }
    
    
    @Override
    public boolean canParse(@NotNull final ICompilerSyntaxAST parentAST, @NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return syntaxAnalyzer.matchesCurrentToken(SymbolType.AT_SIGN) != null;
    }
    
}