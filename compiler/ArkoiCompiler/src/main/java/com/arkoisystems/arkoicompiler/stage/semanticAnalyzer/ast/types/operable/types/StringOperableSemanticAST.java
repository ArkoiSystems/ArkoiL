/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.StringToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.StringOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;

public class StringOperableSemanticAST extends AbstractOperableSemanticAST<StringOperableSyntaxAST, StringToken>
{
    
    public StringOperableSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final StringOperableSyntaxAST stringOperableSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, stringOperableSyntaxAST, ASTType.STRING_OPERABLE);
    }
    
    
    @Override
    public StringToken getOperableObject() {
        if(this.getSyntaxAST().getOperableObject() == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(), this.getSyntaxAST(), SemanticErrorType.STRING_NO_OPERABLE
            );
            return null;
        }
        return this.getSyntaxAST().getOperableObject();
    }
    
}
