package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.operable.types.IdentifierCallOperableSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.AbstractOperableAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.IdentifierCallAST;

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
public class IdentifierCallOperableAST extends AbstractOperableAST<IdentifierCallAST, IdentifierCallOperableSemantic>
{
    
    public IdentifierCallOperableAST(final IdentifierCallAST identifierCallAST) {
        super(ASTType.IDENTIFIER_CALL_OPERABLE);
        
        this.setOperableObject(identifierCallAST);
        
        this.setStart(this.getOperableObject().getStart());
        this.setEnd(this.getOperableObject().getEnd());
    }
    
    @Override
    public IdentifierCallOperableAST parseAST(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (this.getOperableObject() == null) {
            syntaxAnalyzer.errorHandler().addError(new ParserError<>(AbstractOperableAST.OPERABLE_PARSER, this, "Couldn't parse the identifier call operable because the identifier call is null."));
            return null;
        }
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
}
