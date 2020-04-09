/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import lombok.Getter;

public enum TypeKind
{
    
    FLOAT("float", true, 2),
    DOUBLE("double", true, 2.5),
    LONG("long", true, 1.5),
    INTEGER("int", true, 1),
    SHORT("short", true, 1),
    CHAR("char", true, 1),
    BYTE("byte", true, 0.5),
    
    STRING("string", false, 0),
    BOOLEAN("boolean", false, 0),
    
    COLLECTION("[]", false, 0),
    VOID("void", false, 0),
    UNDEFINED("undefined", false, 0);
    
    
    @Getter
    private final String name;
    
    
    @Getter
    private final boolean isNumeric;
    
 
    @Getter
    private final double precision;
    
    
    TypeKind(final String name, final boolean isNumeric, final double precision) {
        this.isNumeric = isNumeric;
        this.precision = precision;
        this.name = name;
    }
    
    
    public static TypeKind getTypeKind(final AbstractToken abstractToken) {
        for (final TypeKind typeKind : TypeKind.values()) {
            if (typeKind == COLLECTION || typeKind == UNDEFINED)
                continue;
            if (typeKind.getName().equals(abstractToken.getTokenContent()))
                return typeKind;
        }
        return null;
    }
    
}