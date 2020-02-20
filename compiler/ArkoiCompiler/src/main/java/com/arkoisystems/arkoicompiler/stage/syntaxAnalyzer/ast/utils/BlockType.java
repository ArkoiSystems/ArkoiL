package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils;

import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;

/**
 * This enum is used to define the {@link BlockType} for an {@link BlockSyntaxAST}. The
 * three entry have different meanings, so a native {@link BlockType} is not treated like
 * an inlined one.
 */
public enum BlockType
{
    
    BLOCK,
    INLINE,
    NATIVE
    
}