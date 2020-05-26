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
package com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement

import com.arkoisystems.arkoicompiler.error.ArkoiError
import com.arkoisystems.arkoicompiler.stage.parser.ParserErrorType
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiASTNodeSpec
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.IdentifierCallNode
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.ExpressionNode
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.FunctionNode
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ImportNode
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ReturnNode
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.VariableNode
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType
import spock.lang.Unroll

class StatementASTSpec extends ArkoiASTNodeSpec {
	
	@Unroll
	def "#1 variable ast expected | #name"() {
		given:
		def parser = this.createSyntaxAnalyzer(code, false)
		
		expect:
		StatementNode.statementBuilder()
				.parser(parser)
				.astType(ASTType.STATEMENT)
				.build()
				.parseAST(null).getClass() == ast
		
		where:
		name     | code                                || ast
		"var"    | "var test = 0"                      || VariableNode.class
		"import" | "import \"test\""                   || ImportNode.class
		"fun"    | "fun main<int>(args: string[]) = 0" || FunctionNode.class
		"return" | "return 0"                          || ReturnNode.class
		"test"   | "test"                              || IdentifierCallNode.class
	}
	
	@Unroll
	def "#2 <identifier call> expected | #name"() {
		given:
		def parser = this.createSyntaxAnalyzer(code, false)
		def errors = useErrors ? new HashSet([
				ArkoiError.builder()
						.compilerClass(parser.getCompilerClass())
						.message(ParserErrorType.SYNTAX_ERROR_TEMPLATE)
						.arguments("Statement", "<identifier call>", name)
						.positions([
								ArkoiError.ErrorPosition.builder()
										.lineRange(LineRange.make(
												parser.getCompilerClass(),
												0, 0))
										.charStart(0)
										.charEnd(name.length())
										.build()])
						.build()
		]) : new HashSet<>()
		
		expect:
		StatementNode.statementBuilder()
				.parser(parser)
				.astType(ASTType.STATEMENT)
				.build()
				.parseAST(ExpressionNode.expressionBuilder()
						.parser(parser)
						.astType(ASTType.EXPRESSION)
						.build())
		parser.getErrorHandler().getCompilerErrors() == errors
		
		where:
		name     | code                  || useErrors
		"var"    | "var test = 0"        || true
		"return" | "return 0"            || true
		"import" | "import \"Test\""     || true
		"fun"    | "fun main<int>() = 0" || true
		"test"   | "test"                || false
	}
	
}
