/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.CollectionOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.IdentifierCallOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.NumberOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.StringOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import lombok.Getter;

/**
 * This enum is used to define a TypeKind which is used in {@link TypeSyntaxAST}. It is
 * important for later usage in the {@link SemanticAnalyzer} because it will check if they
 * can be combined etc.
 */
@Getter
public enum TypeKind
{
    
    STRING("string", false, 0),
    INTEGER("int", true, 1),
    FLOAT("float", true, 2),
    BYTE("byte", true, 0.5),
    COLLECTION("[]", false, 0),
    DOUBLE("double", true, 2),
    SHORT("short", true, 0.5),
    BOOLEAN("boolean", false, 0),
    VOID("void", false, 0);
    
    
    /**
     * The name of the enum entry e.g. "double" or "short"
     */
    private final String name;
    
    
    private final boolean isNumeric;
    
    
    private final double precision;
    
    
    TypeKind(final String name, final boolean isNumeric, final double precision) {
        this.isNumeric = isNumeric;
        this.precision = precision;
        this.name = name;
    }
    
    /**
     * Gets the {@link TypeKind} of an {@link AbstractToken} through the token content. It
     * will skip the collection type because you can't parse a collection outside the
     * {@link SemanticAnalyzer}.
     *
     * @param abstractToken
     *         the {@link AbstractToken} which is used for searching the specific {@link
     *         TypeKind}.
     *
     * @return {@code null} if the method didn't found anything or the {@link TypeKind} if
     *         it did.
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
    
    
    /**
     * Gets the {@link TypeKind} based on the {@link AbstractOperableSemanticAST}. It can
     * happen, that a {@link AbstractOperableSemanticAST} needs to compute the {@link
     * TypeKind} at first like with the {@link AbstractExpressionSemanticAST} and {@link
     * IdentifierCallOperableSemanticAST}.
     *
     * @param abstractOperableSemanticAST
     *         the {@link AbstractOperableSemanticAST} which is used to find the correct
     *         {@link TypeKind}.
     *
     * @return {@code null} if the {@link AbstractOperableSemanticAST} isn't supported or
     *         the found {@link TypeKind}.
     */
    public static TypeKind getTypeKind(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        if (abstractOperableSemanticAST instanceof AbstractExpressionSemanticAST) {
            final AbstractExpressionSemanticAST<?> abstractExpressionSemanticAST = (AbstractExpressionSemanticAST<?>) abstractOperableSemanticAST;
            return abstractExpressionSemanticAST.getExpressionType();
        } else if (abstractOperableSemanticAST instanceof NumberOperableSemanticAST) {
            //            final NumberOperableSemanticAST numberExpression = (NumberOperableSemanticAST) abstractOperableSemanticAST;
            //            return getTypeKind(numberExpression.getNumberType());
            return null;
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
    
    
    /**
     * Combines two {@link AbstractOperableSemanticAST}'s and returns just one {@link
     * TypeKind}. It is just a sub-method for the real method ({@link
     * TypeKind#combineKinds(TypeKind, TypeKind)}) which is used if the {@link TypeKind}'s
     * of the two {@link AbstractOperableSemanticAST}'s was found.
     *
     * @param leftSideOperable
     *         the left {@link AbstractOperableSemanticAST} which is used to resolve it's
     *         {@link TypeKind}.
     * @param rightSideOperable
     *         the right {@link AbstractOperableSemanticAST} which is used to resolve it's
     *         {@link TypeKind}.
     *
     * @return {@code null} if the combination isn't supported or the result of the called
     *         method ({@link TypeKind#combineKinds(TypeKind, TypeKind)}).
     */
    public static TypeKind combineKinds(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        return combineKinds(getTypeKind(leftSideOperable), getTypeKind(rightSideOperable));
    }
    
    
    /**
     * The main method to combine two {@link TypeKind}'s. If the combination isn't
     * supported, it will throw an error.
     *
     * @param leftSideKind
     *         the left {@link TypeKind} which should be combined with the
     *         "rightSideKind".
     * @param rightSideKind
     *         the right {@link TypeKind} which should be combined with the
     *         "leftSideKind".
     *
     * @return the resolved {@link TypeKind} or an error if the combination isn't
     *         supported.
     */
    private static TypeKind combineKinds(final TypeKind leftSideKind, final TypeKind rightSideKind) {
        System.out.print(leftSideKind + ", " + rightSideKind + ": ");
        if (leftSideKind == STRING && rightSideKind == COLLECTION) {
            System.out.print(STRING + "\n");
            return STRING;
        } else if (leftSideKind.isNumeric() && rightSideKind.isNumeric()) {
            if (rightSideKind.getPrecision() > leftSideKind.getPrecision()) {
                System.out.print(rightSideKind + "\n");
                return rightSideKind;
            } else {
                System.out.print(leftSideKind + "\n");
                return leftSideKind;
            }
        } else
            throw new NullPointerException("Combination isn't supported: " + leftSideKind + ", " + rightSideKind);
    }
    
}