/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on April 25, 2020
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
package com.arkoisystems.arkoicompiler.stage.parser.ast

import com.arkoisystems.arkoicompiler.ArkoiClass
import com.arkoisystems.arkoicompiler.ArkoiCompiler
import spock.lang.Specification

class ArkoiASTNodeSpec extends Specification {
	
	def createSyntaxAnalyzer(final String code, final boolean detailed) {
		def arkoiClass = new ArkoiClass(new ArkoiCompiler(), "", code.getBytes(), detailed)
		if (!arkoiClass.getArkoiLexer().processStage())
			return null
		arkoiClass.getSyntaxAnalyzer().reset()
		arkoiClass.getSyntaxAnalyzer().setTokens(arkoiClass.getArkoiLexer().getTokens());
		return arkoiClass.getSyntaxAnalyzer()
	}
	
}
