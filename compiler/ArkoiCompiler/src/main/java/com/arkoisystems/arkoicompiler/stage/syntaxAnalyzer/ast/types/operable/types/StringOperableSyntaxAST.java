/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.StringToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;

public class StringOperableSyntaxAST extends AbstractOperableSyntaxAST<StringToken>
{
    
    public StringOperableSyntaxAST() {
        super(ASTType.STRING_OPERABLE);
    }
    
    @Override
    public StringOperableSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.STRING_LITERAL) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the string operable because the parsing doesn't start with a string."));
            return null;
        } else {
            this.setOperableObject((StringToken) syntaxAnalyzer.currentToken());
            this.setStart(this.getOperableObject().getStart());
            this.setEnd(this.getOperableObject().getEnd());
        }
        return this;
    }
    
    //    @Override
    //    public TypeKind binMod(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
    //        if (rightSideOperable instanceof CollectionOperableSyntaxAST)
    //            return TypeKind.combineKinds(this, rightSideOperable);
    //        return super.binMod(leftSideOperable, rightSideOperable);
    //    }
    
}
