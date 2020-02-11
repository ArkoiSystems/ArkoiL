package com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.StringToken;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.StringOperableSyntaxAST;
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
public class StringOperableSemanticAST extends AbstractOperableSemanticAST<StringOperableSyntaxAST, StringToken>
{
    
    public StringOperableSemanticAST(final AbstractSemanticAST<?> lastContainerAST, final StringOperableSyntaxAST stringOperableSyntaxAST) {
        super(lastContainerAST, stringOperableSyntaxAST, ASTType.STRING_OPERABLE);
    }
    
    @Override
    public StringOperableSemanticAST analyse(final SemanticAnalyzer semanticAnalyzer) {
        this.setOperableObject(this.getSyntaxAST().getOperableObject());
        return this;
    }
    
    @Override
    public TypeSyntaxAST.TypeKind binMod(SemanticAnalyzer semanticAnalyzer, AbstractOperableSyntaxAST<?> rightSideOperable) {
        return super.binMod(semanticAnalyzer, rightSideOperable);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind equal(SemanticAnalyzer semanticAnalyzer, AbstractOperableSyntaxAST<?> rightSideOperable) {
        return super.equal(semanticAnalyzer, rightSideOperable);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind notEqual(SemanticAnalyzer semanticAnalyzer, AbstractOperableSyntaxAST<?> rightSideOperable) {
        return super.notEqual(semanticAnalyzer, rightSideOperable);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind relationalIs(SemanticAnalyzer semanticAnalyzer, AbstractOperableSyntaxAST<?> rightSideOperable) {
        return super.relationalIs(semanticAnalyzer, rightSideOperable);
    }
    
}
