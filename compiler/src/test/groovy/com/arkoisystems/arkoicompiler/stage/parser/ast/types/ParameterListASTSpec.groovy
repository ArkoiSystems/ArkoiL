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
package com.arkoisystems.arkoicompiler.stage.parser.ast.types

import com.arkoisystems.arkoicompiler.error.ArkoiError
import com.arkoisystems.arkoicompiler.stage.parser.ParserErrorType
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiASTNodeSpec

class ParameterListASTSpec extends ArkoiASTNodeSpec {
	
	def "#1 '(' expected"() {
		given:
		def parser = this.createSyntaxAnalyzer("test: int, test2: string)", false)
		def errors = new HashSet([
				ArkoiError.builder()
						.compilerClass(parser.getCompilerClass())
						.message(ParserErrorType.SYNTAX_ERROR_TEMPLATE)
						.arguments("Parameter list", "'('", "test")
						.positions([
								ArkoiError.ErrorPosition.builder()
										.lineRange(LineRange.make(
												parser.getCompilerClass(),
												0, 0))
										.charStart(0)
										.charEnd(4)
										.build()])
						.build()
		])
		
		expect:
		ParameterListNode.builder()
				.parser(parser)
				.build()
				.parseAST(null)
		parser.getErrorHandler().getCompilerErrors() == errors
	}
	
	def "#2 ')' expected"() {
		given:
		def parser = this.createSyntaxAnalyzer("(test: int, test2: string", false)
		def errors = new HashSet([
				ArkoiError.builder()
						.compilerClass(parser.getCompilerClass())
						.message(ParserErrorType.SYNTAX_ERROR_TEMPLATE)
						.arguments("Parameter list", "')'", "nothing")
						.positions([
								ArkoiError.ErrorPosition.builder()
										.lineRange(LineRange.make(
												parser.getCompilerClass(),
												0, 0))
										.charStart(24)
										.charEnd(25)
										.build()])
						.build()
		])
		
		expect:
		ParameterListNode.builder()
				.parser(parser)
				.build()
				.parseAST(null)
		parser.getErrorHandler().getCompilerErrors() == errors
	}
	
}
