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
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types

import com.arkoisystems.arkoicompiler.ArkoiError
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiASTNodeSpec

class ArgumentASTSpec extends ArkoiASTNodeSpec {
	
	def "#1 <identifier> expected"() {
		given:
		def syntaxAnalyzer = this.createSyntaxAnalyzer("2 = true", false)
		def errors = new HashSet([
				ArkoiError.builder()
						.compilerClass(syntaxAnalyzer.getCompilerClass())
						.message(SyntaxErrorType.SYNTAX_ERROR_TEMPLATE)
						.arguments("Argument", "<identifier>", "2")
						.positions([
								ArkoiError.ErrorPosition.builder()
										.lineRange(ArkoiError.ErrorPosition.LineRange.make(
												syntaxAnalyzer.getCompilerClass(),
												0, 0
										))
										.charStart(0)
										.charEnd(1)
										.build()
						])
						.build()
		])
		
		expect:
		ArgumentAST.builder()
				.syntaxAnalyzer(syntaxAnalyzer)
				.build()
				.parseAST(null)
		syntaxAnalyzer.getErrorHandler().getCompilerErrors() == errors
	}
	
	
	def "#2 '=' expected"() {
		given:
		def syntaxAnalyzer = this.createSyntaxAnalyzer("test true", false)
		def errors = new HashSet([
				ArkoiError.builder()
						.compilerClass(syntaxAnalyzer.getCompilerClass())
						.message(SyntaxErrorType.SYNTAX_ERROR_TEMPLATE)
						.arguments("Argument", "'='", "true")
						.positions([
								ArkoiError.ErrorPosition.builder()
										.lineRange(ArkoiError.ErrorPosition.LineRange.make(
												syntaxAnalyzer.getCompilerClass(),
												0, 0
										))
										.charStart(5)
										.charEnd(9)
										.build()
						])
						.build()
		])
		
		expect:
		ArgumentAST.builder()
				.syntaxAnalyzer(syntaxAnalyzer)
				.build()
				.parseAST(null)
		syntaxAnalyzer.getErrorHandler().getCompilerErrors() == errors
	}
	
	
	def "#3 <expression> expected"() {
		given:
		def syntaxAnalyzer = this.createSyntaxAnalyzer("test = .", false)
		def errors = new HashSet([
				ArkoiError.builder()
						.compilerClass(syntaxAnalyzer.getCompilerClass())
						.message(SyntaxErrorType.SYNTAX_ERROR_TEMPLATE)
						.arguments("Argument", "<expression>", ".")
						.positions([
								ArkoiError.ErrorPosition.builder()
										.lineRange(ArkoiError.ErrorPosition.LineRange.make(
												syntaxAnalyzer.getCompilerClass(),
												0, 0
										))
										.charStart(7)
										.charEnd(8)
										.build()
						])
						.build()
		])
		
		expect:
		ArgumentAST.builder()
				.syntaxAnalyzer(syntaxAnalyzer)
				.build()
				.parseAST(null)
		syntaxAnalyzer.getErrorHandler().getCompilerErrors() == errors
	}
	
}
