/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils;

import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.FunctionInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierInvokeOperableSyntaxAST;

/**
 * This enum defines the different access levels for various ASTs like {@link
 * FunctionInvokeOperableSyntaxAST}, {@link IdentifierInvokeOperableSyntaxAST} etc.
 */
public enum ASTAccess
{
    
    THIS_ACCESS,
    GLOBAL_ACCESS,
    
}