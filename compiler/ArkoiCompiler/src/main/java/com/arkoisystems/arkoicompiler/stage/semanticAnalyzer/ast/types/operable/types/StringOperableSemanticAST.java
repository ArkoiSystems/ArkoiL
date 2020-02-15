/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.StringToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.StringOperableSyntaxAST;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StringOperableSemanticAST extends AbstractOperableSemanticAST<StringOperableSyntaxAST, StringToken>
{
    
    public StringOperableSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final StringOperableSyntaxAST stringOperableSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, stringOperableSyntaxAST, ASTType.STRING_OPERABLE);
    }
    
    @Override
    public StringToken getExpressionType() {
        if(this.getSyntaxAST().getOperableObject() == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SyntaxASTError<>(this.getSyntaxAST(), "Couldn't analyze this string operable because the content is null."));
            return null;
        }
        return this.getSyntaxAST().getOperableObject();
    }
    
}
