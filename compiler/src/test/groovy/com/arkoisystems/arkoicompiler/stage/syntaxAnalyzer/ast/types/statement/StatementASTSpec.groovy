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
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement

import com.arkoisystems.arkoicompiler.ArkoiError
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiASTNodeSpec
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ReturnAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType
import spock.lang.Unroll

class StatementASTSpec extends ArkoiASTNodeSpec {
	
	@Unroll
	def "#1 variable ast expected | #name"() {
		given:
		def syntaxAnalyzer = this.createSyntaxAnalyzer(code, false)
		
		expect:
		StatementAST.statementBuilder()
				.syntaxAnalyzer(syntaxAnalyzer)
				.astType(ASTType.STATEMENT)
				.build()
				.parseAST(null).getClass() == ast
		
		where:
		name     | code                                || ast
		"var"    | "var test = 0"                      || VariableAST.class
		"import" | "import \"test\""                   || ImportAST.class
		"fun"    | "fun main<int>(args: string[]) = 0" || FunctionAST.class
		"return" | "return 0"                          || ReturnAST.class
		"test"   | "test"                              || IdentifierCallAST.class
	}
	
	@Unroll
	def "#2 <identifier call> expected | #name"() {
		given:
		def syntaxAnalyzer = this.createSyntaxAnalyzer(code, false)
		def errors = useErrors ? new HashSet([
				ArkoiError.builder()
						.compilerClass(syntaxAnalyzer.getCompilerClass())
						.message(SyntaxErrorType.SYNTAX_ERROR_TEMPLATE)
						.arguments("Statement", "<identifier call>", name)
						.positions([
								ArkoiError.ErrorPosition.builder()
										.lineRange(ArkoiError.ErrorPosition.LineRange.make(
												syntaxAnalyzer.getCompilerClass(),
												0, 0
										))
										.charStart(0)
										.charEnd(name.length())
										.build()
						])
						.build()
		]) : new HashSet<>()
		
		expect:
		StatementAST.statementBuilder()
				.syntaxAnalyzer(syntaxAnalyzer)
				.astType(ASTType.STATEMENT)
				.build()
				.parseAST(ExpressionAST.expressionBuilder()
						.syntaxAnalyzer(syntaxAnalyzer)
						.astType(ASTType.EXPRESSION)
						.build()
				)
		syntaxAnalyzer.getErrorHandler().getCompilerErrors() == errors
		
		where:
		name     | code                  || useErrors
		"var"    | "var test = 0"        || true
		"return" | "return 0"            || true
		"import" | "import \"Test\""     || true
		"fun"    | "fun main<int>() = 0" || true
		"test"   | "test"                || false
	}
	
}
