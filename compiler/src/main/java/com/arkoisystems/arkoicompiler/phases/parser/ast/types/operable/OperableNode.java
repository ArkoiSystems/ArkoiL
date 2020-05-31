/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
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
package com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable;

import com.arkoisystems.arkoicompiler.phases.lexer.token.LexerToken;
import com.arkoisystems.arkoicompiler.phases.parser.Parser;
import com.arkoisystems.arkoicompiler.phases.parser.ast.ParserNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.IdentifierNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.NumberNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.StringNode;
import com.arkoisystems.arkoicompiler.phases.parser.SymbolTable;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OperableNode extends ParserNode
{
    
    @Builder(builderMethodName = "operatorBuilder")
    protected OperableNode(
            final @Nullable Parser parser,
            final @Nullable SymbolTable currentScope,
            final @Nullable LexerToken startToken,
            final @Nullable LexerToken endToken
    ) {
        super(parser, currentScope, startToken, endToken);
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return IdentifierNode.GLOBAL_NODE.canParse(parser, offset) ||
                NumberNode.GLOBAL_NODE.canParse(parser, offset) ||
                StringNode.GLOBAL_NODE.canParse(parser, offset);
    }
    
    @Override
    public OperableNode clone() throws CloneNotSupportedException {
        return (OperableNode) super.clone();
    }
    
}
