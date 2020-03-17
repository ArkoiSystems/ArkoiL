/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on March 17, 2020
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
package com.arkoisystems.lang.arkoi.ide

import com.arkoisystems.lang.arkoi.ArkoiTokenTypes
import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

class ArkoiBraceMatcher : PairedBraceMatcher {

    private val bracePairs = arrayOf(
        BracePair(ArkoiTokenTypes.L_BRACE, ArkoiTokenTypes.R_BRACE, true),
        BracePair(ArkoiTokenTypes.L_BRACKET, ArkoiTokenTypes.R_BRACKET, false),
        BracePair(ArkoiTokenTypes.L_PARENTHESIS, ArkoiTokenTypes.R_PARENTHESIS, false),
        BracePair(ArkoiTokenTypes.LESS_THAN, ArkoiTokenTypes.GREATER_THAN, false)
    )

    override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int) = openingBraceOffset

    override fun getPairs() = bracePairs

    // TODO: Check if the pair is inside a string
    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true

}