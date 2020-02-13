package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.FunctionDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTAccess;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallOperableSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka timo
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
public class IdentifierCallOperableSemanticAST extends AbstractOperableSemanticAST<IdentifierCallOperableSyntaxAST, TypeSyntaxAST.TypeKind>
{
    
    @Expose
    private ASTAccess identifierAccess;
    
    private AbstractSemanticAST<?> foundIdentifier;
    
    public IdentifierCallOperableSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final IdentifierCallOperableSyntaxAST identifierCallOperableSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, identifierCallOperableSyntaxAST, ASTType.IDENTIFIER_CALL_OPERABLE);
    }
    
//    @Override
//    public IdentifierCallOperableSemanticAST analyseAST(final SemanticAnalyzer semanticAnalyzer) {
//        this.identifierAccess = this.getSyntaxAST().getIdentifierAccess();
//
//        AbstractSemanticAST<?> abstractSemanticAST = null;
//        if(this.identifierAccess != ASTAccess.THIS_ACCESS && this.getLastContainerAST() instanceof FunctionDefinitionSemanticAST) {
//            final FunctionDefinitionSemanticAST functionDefinitionSemanticAST = (FunctionDefinitionSemanticAST) this.getLastContainerAST();
//            abstractSemanticAST = functionDefinitionSemanticAST.findSemanticAST(this.getSyntaxAST().getCalledIdentifierToken());
//        }
//
//        if (abstractSemanticAST == null)
//            abstractSemanticAST = semanticAnalyzer.getRootSemanticAST().findSemanticAST(this.getSyntaxAST().getCalledIdentifierToken());
//
//        if(abstractSemanticAST == null) {
//            semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(this.getSyntaxAST(), "Couldn't analyze this AST because no identifier was found with this name."));
//            return null;
//        } else this.foundIdentifier = abstractSemanticAST;
//        return this;
//    }
    
}
