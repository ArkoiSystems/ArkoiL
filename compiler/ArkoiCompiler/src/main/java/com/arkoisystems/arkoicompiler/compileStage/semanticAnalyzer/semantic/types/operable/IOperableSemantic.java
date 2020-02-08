package com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.operable;

import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.TypeAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.AbstractOperableAST;

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
public interface IOperableSemantic
{
    
    /*
        BinaryExpressionAST
     */
    TypeAST.TypeKind binAdd(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    TypeAST.TypeKind binSub(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    TypeAST.TypeKind binMul(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    TypeAST.TypeKind binDiv(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    TypeAST.TypeKind binMod(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    /*
        AssignmentExpressionAST
     */
    TypeAST.TypeKind assign(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    TypeAST.TypeKind addAssign(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    TypeAST.TypeKind subAssign(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    TypeAST.TypeKind mulAssign(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    TypeAST.TypeKind divAssign(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    TypeAST.TypeKind modAssign(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    /*
        EqualityExpressionAST
     */
    TypeAST.TypeKind equal(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    TypeAST.TypeKind notEqual(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    /*
        LogicalExpressionAST
     */
    TypeAST.TypeKind logicalOr(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    TypeAST.TypeKind logicalAnd(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    /*
        PostfixExpressionAST
     */
    TypeAST.TypeKind postfixAdd(final SemanticAnalyzer semanticAnalyzer);
    
    TypeAST.TypeKind postfixSub(final SemanticAnalyzer semanticAnalyzer);
    
    /*
        PrefixExpressionAST
     */
    TypeAST.TypeKind prefixAdd(final SemanticAnalyzer semanticAnalyzer);
    
    TypeAST.TypeKind prefixSub(final SemanticAnalyzer semanticAnalyzer);
    
    TypeAST.TypeKind prefixNegate(final SemanticAnalyzer semanticAnalyzer);
    
    TypeAST.TypeKind prefixAffirm(final SemanticAnalyzer semanticAnalyzer);
    
    /*
        PrefixExpressionAST
     */
    TypeAST.TypeKind relationalLessThan(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    TypeAST.TypeKind relationalGreaterThan(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    TypeAST.TypeKind relationalLessEqualThan(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    TypeAST.TypeKind relationalGreaterEqualThan(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
    TypeAST.TypeKind relationalIs(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable);
    
}
