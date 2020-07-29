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
package com.arkoisystems.compiler.error;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Getter
public class CompilerError
{
    
    @EqualsAndHashCode.Include
    @Nullable
    private final List<ErrorPosition> otherPositions;
    
    @EqualsAndHashCode.Include
    @Nullable
    private final ErrorPosition causePosition;
    
    @EqualsAndHashCode.Include
    @Nullable
    private final String otherMessage;
    
    @EqualsAndHashCode.Include
    @Nullable
    private final String causeMessage;
    
    @Override
    public String toString() {
        Objects.requireNonNull(this.getCauseMessage());
        Objects.requireNonNull(this.getCausePosition());
    
        final StringBuilder messageBuilder = new StringBuilder(String.format(
                "%s:%s:%s: %s",
                this.getCausePosition().getFilePath(),
                this.getCausePosition().getLineRange().getStartLine() + 1,
                this.getCausePosition().getCharStart() + 1,
                this.getCauseMessage()
        )).append("\r\n");
        this.getCausePosition().toString(messageBuilder, " ");
    
        if (this.getOtherPositions() == null || this.getOtherPositions().isEmpty())
            return messageBuilder.toString();
    
        messageBuilder.append("\r\n").append(" ");
        if (this.getOtherMessage() != null)
            messageBuilder.append(this.getOtherMessage());
    
        for (final ErrorPosition errorPosition : this.getOtherPositions()) {
            messageBuilder.append("\r\n");
            errorPosition.toString(messageBuilder, " ");
        }
    
        return messageBuilder.toString();
    }
    
}
