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

import com.arkoisystems.lang.arkoi.lexer.ArkoiTokenTypes
import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

class ArkoiBraceMatcher : PairedBraceMatcher {

    private val bracePairs = arrayOf(
        BracePair(ArkoiTokenTypes.openingBrace, ArkoiTokenTypes.closingBrace, true),
        BracePair(ArkoiTokenTypes.openingBracket, ArkoiTokenTypes.closingBracket, false),
        BracePair(ArkoiTokenTypes.openingParenthesis, ArkoiTokenTypes.closingParenthesis, false),
        BracePair(ArkoiTokenTypes.openingArrow, ArkoiTokenTypes.closingArrow, false)
    )

    override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int) = openingBraceOffset

    override fun getPairs() = bracePairs

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true

}