package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.AssignmentExpressionSyntaxAST;
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
public class AssignmentExpressionSemanticAST extends AbstractExpressionSemanticAST<AssignmentExpressionSyntaxAST>
{
    
    public AssignmentExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final AssignmentExpressionSyntaxAST assignmentExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, assignmentExpressionSyntaxAST, ASTType.ASSIGNMENT_EXPRESSION);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind getOperableObject() {
        System.out.println("Assignment Expression Semantic AST");
        return null;
    }
    
    @Override
    public TypeSyntaxAST.TypeKind assign(SemanticAnalyzer semanticAnalyzer, AbstractOperableSyntaxAST<?> rightSideOperable) {
        return super.assign(semanticAnalyzer, rightSideOperable);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind addAssign(SemanticAnalyzer semanticAnalyzer, AbstractOperableSyntaxAST<?> rightSideOperable) {
        return super.addAssign(semanticAnalyzer, rightSideOperable);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind subAssign(SemanticAnalyzer semanticAnalyzer, AbstractOperableSyntaxAST<?> rightSideOperable) {
        return super.subAssign(semanticAnalyzer, rightSideOperable);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind mulAssign(SemanticAnalyzer semanticAnalyzer, AbstractOperableSyntaxAST<?> rightSideOperable) {
        return super.mulAssign(semanticAnalyzer, rightSideOperable);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind divAssign(SemanticAnalyzer semanticAnalyzer, AbstractOperableSyntaxAST<?> rightSideOperable) {
        return super.divAssign(semanticAnalyzer, rightSideOperable);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind modAssign(SemanticAnalyzer semanticAnalyzer, AbstractOperableSyntaxAST<?> rightSideOperable) {
        return super.modAssign(semanticAnalyzer, rightSideOperable);
    }
    
}
