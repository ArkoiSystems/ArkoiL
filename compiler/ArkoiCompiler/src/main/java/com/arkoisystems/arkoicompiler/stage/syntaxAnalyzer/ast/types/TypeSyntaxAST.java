package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.numbers.AbstractNumberToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.CollectionOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.IdentifierCallOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.NumberOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.StringOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.TypeParser;
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
public class TypeSyntaxAST extends AbstractSyntaxAST
{
    
    public static TypeParser TYPE_PARSER = new TypeParser();
    
    
    @Expose
    private TypeKind typeKind;
    
    @Expose
    private boolean isArray;
    
    /**
     * This constructor will initialize the AST with the AST-Type "TYPE". This will help
     * to debug problems or check the AST for correct syntax. Also it will pass the
     * TypeKind to this class which is used for comparing two types. Besides that it will
     * pass through if the type is an array.
     *
     * @param typeKind
     *         The kind of the Type which is used for comparing two types if they are the
     *         same.
     * @param isArray
     *         This boolean will say if the Type is an Array or not.
     */
    public TypeSyntaxAST(final TypeKind typeKind, final boolean isArray) {
        super(ASTType.TYPE);
        
        this.typeKind = typeKind;
        this.isArray = isArray;
    }
    
    /**
     * This constructor will initialize the AST with the AST-Type "TYPE". This will help
     * to debug problems or check the AST for correct syntax. Besides that it will passes
     * no other values.
     */
    public TypeSyntaxAST() {
        super(ASTType.TYPE);
    }
    
    /**
     * This method will parse the TypeAST and checks it for the correct syntax. This AST
     * can be used by everything so it doesn't matter what the parent AST is. A TypeAST is
     * used to specify an IdentifierToken as a Type. So "int" can be an Integer or
     * "string" a String.
     *
     * @param parentAST
     *         The parent of the AST. With it you can check for correct usage of the
     *         statement.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It will return null if an error occurred or an TypeAST if it parsed until
     *         to the end.
     */
    @Override
    public TypeSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the Type because the parsing doesn't start with an IdentifierToken."));
            return null;
        } else this.typeKind = TypeKind.getTypeKind(syntaxAnalyzer.currentToken());
    
        if (this.typeKind == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the Type because it isn't a valid type keyword."));
            return null;
        }
    
        this.setStart(syntaxAnalyzer.currentToken().getStart());
        this.setEnd(syntaxAnalyzer.currentToken().getEnd());
    
        // This will check if the next two Tokens are an opening and closing bracket aka. "[]". If it is, then skip these two Tokens and set the "isArray" boolean to true.
        if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.OPENING_BRACKET) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.CLOSING_BRACKET) != null) {
            syntaxAnalyzer.nextToken(2);
            this.isArray = true;
        }
        return this;
    }
    
    @Getter
    public enum TypeKind
    {
        
        STRING("string"),
        INTEGER("int"),
        FLOAT("float"),
        BYTE("byte"),
        COLLECTION("[]"),
        DOUBLE("double"),
        SHORT("short"),
        BOOLEAN("boolean"),
        VOID("void");
        
        private final String name;
        
        /**
         * This constructor will initialize the name of the TypeKind for later
         * development.
         *
         * @param name
         *         The name of the TypeKind e.g. "string" or "int"
         */
        TypeKind(final String name) {
            this.name = name;
        }
        
        /**
         * This method will return the TypeKind of the input Token. If it doesn't find
         * something equal to a TypeKind it will just return the TypeKind "OTHER".
         *
         * @param abstractToken
         *         The input Token which get used to search the TypeKind.
         *
         * @return It will return by default "OTHER" or the found TypeKind with help of
         *         the AbstractToken.
         */
        public static TypeKind getTypeKind(final AbstractToken abstractToken) {
            for (final TypeKind typeKind : TypeKind.values()) {
                if (typeKind == COLLECTION)
                    continue;
                if (typeKind.getName() == null)
                    continue;
                if (typeKind.getName().equals(abstractToken.getTokenContent()))
                    return typeKind;
            }
            return null;
        }
    
        public static TypeKind getTypeKind(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
            if (abstractOperableSemanticAST instanceof AbstractExpressionSemanticAST) {
                final AbstractExpressionSemanticAST<?> abstractExpressionSemanticAST = (AbstractExpressionSemanticAST<?>) abstractOperableSemanticAST;
                return abstractExpressionSemanticAST.getExpressionType();
            } else if (abstractOperableSemanticAST instanceof NumberOperableSemanticAST) {
                final NumberOperableSemanticAST numberExpression = (NumberOperableSemanticAST) abstractOperableSemanticAST;
                return getTypeKind(numberExpression.getNumberType());
            } else if (abstractOperableSemanticAST instanceof StringOperableSemanticAST) {
                return STRING;
            } else if (abstractOperableSemanticAST instanceof CollectionOperableSemanticAST) {
                return COLLECTION;
            } else if (abstractOperableSemanticAST instanceof IdentifierCallOperableSemanticAST) {
                final IdentifierCallOperableSemanticAST identifierCallOperableSemanticAST = (IdentifierCallOperableSemanticAST) abstractOperableSemanticAST;
                return identifierCallOperableSemanticAST.getExpressionType();
            } else {
                System.out.println("TypeKind: Not supported yet #1: " + abstractOperableSemanticAST);
                return null;
            }
        }
    
        public static TypeKind combineKinds(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
            return combineKinds(getTypeKind(leftSideOperable), getTypeKind(rightSideOperable));
        }
    
        private static TypeKind combineKinds(final TypeKind leftSideKind, final TypeKind rightSideKind) {
            System.out.println("TypeKind: " + leftSideKind + ", " + rightSideKind);
            if(leftSideKind == STRING && rightSideKind == COLLECTION)
                return STRING;
            return leftSideKind;
        }
    
        public static TypeKind getTypeKind(final AbstractNumberToken.NumberType numberType) {
            switch (numberType) {
                case BYTE:
                    return BYTE;
                case DOUBLE:
                    return DOUBLE;
                case SHORT:
                    return SHORT;
                case INTEGER:
                case HEXADECIMAL:
                    return INTEGER;
                case FLOAT:
                    return FLOAT;
                default:
                    return null;
            }
        }
    
    }
    
}
