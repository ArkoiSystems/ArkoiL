package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SeparatorToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types.TypeParser;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka Timo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
@Getter
@Setter
public class TypeAST extends AbstractAST
{
    
    public static TypeParser TYPE_PARSER = new TypeParser();
    
    
    @Expose
    private TypeKind typeKind;
    
    @Expose
    private boolean isArray;
    
    public TypeAST(final TypeKind typeKind, final boolean isArray) {
        super(ASTType.TYPE);
        
        this.typeKind = typeKind;
        this.isArray = isArray;
    }
    
    public TypeAST() {
        super(ASTType.TYPE);
    }
    
    @Override
    public TypeAST parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractToken currentIdentifierToken = syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER);
        if (currentIdentifierToken == null) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(this, "Couldn't parse the TypeAST because the parsing doesn't begin with an IdentifierToken."));
            return null;
        } else this.setTypeKind(TypeKind.getTypeKind((IdentifierToken) currentIdentifierToken));
        
        if (syntaxAnalyzer.matchesPeekToken(1, SeparatorToken.SeparatorType.OPENING_BRACKET) == null || syntaxAnalyzer.matchesPeekToken(2, SeparatorToken.SeparatorType.CLOSING_BRACKET) == null)
            return parentAST.addAST(this, syntaxAnalyzer);
        
        syntaxAnalyzer.nextToken();
        syntaxAnalyzer.nextToken();
        this.setArray(true);
        
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    @Override
    public <T extends AbstractAST> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
    @Getter
    public enum TypeKind
    {
        
        STRING("string"),
        INTEGER("int"),
        FLOAT("float"),
        BOOLEAN("boolean"),
        VOID("void"),
        OTHER(null);
        
        private final String name;
        
        TypeKind(final String name) {
            this.name = name;
        }
        
        public static TypeKind getTypeKind(final IdentifierToken identifierToken) {
            for (final TypeKind typeKind : TypeKind.values()) {
                if (typeKind.getName() == null)
                    continue;
                
                if (typeKind.getName().equals(identifierToken.getTokenContent()))
                    return typeKind;
            }
            return TypeKind.OTHER;
        }
    }
    
}
