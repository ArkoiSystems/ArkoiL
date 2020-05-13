/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 31, 2020
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
package com.arkoisystems.arkoicompiler.api;

import com.arkoisystems.alt.ArkoiLT;
import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.ArkoiCompiler;
import com.arkoisystems.arkoicompiler.ArkoiError;
import com.arkoisystems.arkoicompiler.stage.codegen.CodeGen;
import com.arkoisystems.arkoicompiler.stage.lexer.ArkoiLexer;
import com.arkoisystems.arkoicompiler.stage.semantic.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.parser.SyntaxAnalyzer;
import org.jetbrains.annotations.NotNull;

public interface ICompilerClass
{
    
    /**
     * This method is used to get the {@link SyntaxAnalyzer} <b>everywhere</b> where the
     * {@link ICompilerClass} is used.
     *
     * @return the {@link SyntaxAnalyzer} of this {@link ICompilerClass}.
     */
    @NotNull
    SyntaxAnalyzer getSyntaxAnalyzer();
    
    @NotNull
    ArkoiLT<ArkoiLexer> getLanguageTools();
    
    /**
     * This method is used to get the {@link SemanticAnalyzer} <b>everywhere</b> where the
     * {@link ICompilerClass} is used.
     *
     * @return the {@link SyntaxAnalyzer} of this {@link ICompilerClass}.
     */
    @NotNull
    SemanticAnalyzer getSemanticAnalyzer();
    
    /**
     * This method is used to get the {@link CodeGen} <b>everywhere</b> where the
     * {@link ICompilerClass} is used.
     *
     * @return the {@link CodeGen} of this {@link ICompilerClass}.
     */
    @NotNull
    CodeGen getCodeGen();
    
    /**
     * This method is used to get the content of the file as a {@code char[]}, which is
     * necessary for later usage in the {@link ArkoiLexer} or even in the official
     * <b>Arkoi-Plugin</b>.
     *
     * @return the content of the file as a {@code char[]}.
     */
    @NotNull
    String getContent();
    
    /**
     * This method is used to get the file path to the object which contained the {@link
     * #getContent()}. In the official <b>Arkoi-Plugin</b> it also can be a Virtual-File.
     * It just needs to be a <b>existing</b> path with other content which is needed.
     *
     * @return the file path of the object which contained the {@link #getContent()}.
     */
    @NotNull
    String getFilePath();
    
    /**
     * This method is used to get the {@link ArkoiCompiler} of this {@link ICompilerClass}
     * which is used to handle all {@link ArkoiClass}es which need to be processed.
     *
     * @return the {@link ArkoiCompiler} of this {@link ICompilerClass}.
     */
    @NotNull
    ArkoiCompiler getArkoiCompiler();
    
    /**
     * This method is used to check if the {@link ICompilerClass} is a native class or
     * not. This is relevant for finding default methods which you can use without
     * importing any other {@link ICompilerClass}.
     *
     * @return the flag if it's a native {@link ICompilerClass} or not.
     */
    boolean isNative();
    
    /**
     * This method is used to check if the {@link ICompilerClass} should print detailed
     * {@link ArkoiError} or not. Detailed errors can make the life easier but sometimes
     * harder too. It will print everything what the compiler complies about.
     *
     * @return the flag if it should print detailed {@link ArkoiError} or not.
     */
    boolean isDetailed();
    
}
