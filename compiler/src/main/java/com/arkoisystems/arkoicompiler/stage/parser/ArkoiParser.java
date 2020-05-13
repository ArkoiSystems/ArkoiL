/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on May 12, 2020
 * Author єхcsє#5543 aka timo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arkoisystems.arkoicompiler.stage.parser;

import com.arkoisystems.alt.api.IError;
import com.arkoisystems.alt.api.INode;
import com.arkoisystems.alt.api.IParser;
import com.arkoisystems.alt.api.annotations.EntryPoint;
import com.arkoisystems.alt.api.annotations.Rule;
import com.arkoisystems.arkoicompiler.stage.lexer.ArkoiLexer;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

@Getter
public class ArkoiParser implements IParser<ArkoiLexer>
{
    
    @NotNull
    private final String parserEBNF;
    
    @NotNull
    private final ArkoiLexer lexer;
    
    @EntryPoint(method = "onProgram")
    @SneakyThrows
    public ArkoiParser(@NotNull final ArkoiLexer lexer) {
        this.lexer = lexer;
        
        this.parserEBNF = new String(ArkoiLexer.class.getResourceAsStream(
                "/grammar/parser.ebnf"
        ).readAllBytes());
    }
    
    @Rule(name = "program")
    public void onProgram(@NotNull final INode node) { }
    
    @Rule(name = "global_statement")
    public void onGlobalStatement(@NotNull final INode node) { }
    
    @Rule(name = "function_declaration")
    public void onFunctionDeclaration(@NotNull final INode node) { }
    
    @Rule(name = "variable_declaration")
    public void onVariableDeclaration(@NotNull final INode node) { }
    
    @Rule(name = "import_declaration")
    public void onImportDeclaration(@NotNull final INode node) { }
    
    @Rule(name = "return_declaration")
    public void onReturnDeclaration(@NotNull final INode node) { }
    
    @Rule(name = "function_return")
    public void onFunctionReturn(@NotNull final INode node) { }
    
    @Rule(name = "function_arguments")
    public void onFunctionArguments(@NotNull final INode node) { }
    
    @Rule(name = "function_argument")
    public void onFunctionArgument(@NotNull final INode node) { }
    
    @Rule(name = "block")
    public void onBlock(@NotNull final INode node) { }
    
    @Rule(name = "inline_block")
    public void onInline_block(@NotNull final INode node) { }
    
    @Rule(name = "brace_block")
    public void onBraceBlock(@NotNull final INode node) { }
    
    @Rule(name = "block_statement")
    public void onBlockStatement(@NotNull final INode node) { }
    
    @Rule(name = "expression")
    public void onExpression(@NotNull final INode node) { }
    
    @Rule(name = "assignment_expression")
    public void onAssignmentExpression(@NotNull final INode node) { }
    
    @Rule(name = "additive_expression")
    public void onAdditiveExpression(@NotNull final INode node) { }
    
    @Rule(name = "multiplicative_expression")
    public void onMultiplicativeExpression(@NotNull final INode node) { }
    
    @Rule(name = "exponential_expression")
    public void onExponentialExpression(@NotNull final INode node) { }
    
    @Rule(name = "operable_expression")
    public void onOperableExpression(@NotNull final INode node) { }
    
    @Rule(name = "prefix_expression")
    public void onPrefixExpression(@NotNull final INode node) { }
    
    @Rule(name = "postfix_expression")
    public void onPostfixExpression(@NotNull final INode node) { }
    
    @Rule(name = "parenthesized_expression")
    public void onParenthesizedExpression(@NotNull final INode node) { }
    
    @Rule(name = "type")
    public void onType(@NotNull final INode node) { }
    
    @Override
    public void onError(final @NonNull IError error) {
        System.out.println("Arkoi Parser: " + error);
    }
    
}
