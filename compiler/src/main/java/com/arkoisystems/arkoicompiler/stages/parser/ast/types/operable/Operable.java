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
package com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable;

import com.arkoisystems.arkoicompiler.stages.lexer.token.LexerToken;
import com.arkoisystems.arkoicompiler.stages.parser.Parser;
import com.arkoisystems.arkoicompiler.stages.parser.ast.ParserNode;
import com.arkoisystems.arkoicompiler.stages.parser.ast.enums.ASTType;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.CollectionOperable;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.IdentifierOperable;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.NumberOperable;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.StringOperable;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Operable extends ParserNode
{
    
    @Builder(builderMethodName = "operatorBuilder")
    protected Operable(
            final @Nullable Parser parser,
            final @NotNull ASTType astType,
            final @Nullable LexerToken startToken,
            final @Nullable LexerToken endToken
    ) {
        super(parser, astType, startToken, endToken);
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return CollectionOperable.GLOBAL_NODE.canParse(parser, offset) ||
                IdentifierOperable.GLOBAL_NODE.canParse(parser, offset) ||
                NumberOperable.GLOBAL_NODE.canParse(parser, offset) ||
                StringOperable.GLOBAL_NODE.canParse(parser, offset);
    }
    
    @Override
    public Operable clone() throws CloneNotSupportedException {
        return (Operable) super.clone();
    }
    
}
