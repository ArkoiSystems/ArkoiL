package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementAST;

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
public class FunctionStatementAST extends AbstractStatementAST
{
    
    /**
     * This constructor is used to get the AST-Type of classes which extends this class.
     * This will help to debug problems or check the AST for correct syntax.
     *
     * @param astType
     *         The AST-Type which should get used by the class.
     */
    public FunctionStatementAST(final ASTType astType) {
        super(astType);
    }
    
    /**
     * This method is just a boilerplate code which should get used if it didn't got
     * overwritten. It will just return null because you should overwrite it.
     *
     * @param parentAST
     *         The parent of the AST. With it you can check for correct usage of the
     *         statement.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It just returns null because you need to overwrite it.
     */
    @Override
    public FunctionStatementAST parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return null;
    }
    
    /**
     * This method is just a boilerplate code which should get used if a other class wants
     * to add an AST to this class. It will just return null because you should overwrite
     * it.
     *
     * @param toAddAST
     *         The AST which should get add to this class.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is needed for checking and modification of the
     *         current Token list/order.
     * @param <T>
     *         The declared AST-Type which should get added to this class.
     *
     * @return It just returns null because you need to overwrite it.
     */
    @Override
    public <T extends AbstractAST> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return null;
    }
    
}
