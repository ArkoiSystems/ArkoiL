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
package com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types

import com.arkoisystems.arkoicompiler.error.ArkoiError
import com.arkoisystems.arkoicompiler.stage.parser.ParserErrorType
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNodeSpec

class FunctionStatementSpec extends ArkoiNodeSpec {
	
	def "#1 'fun' expected"() {
		given:
		def parser = this.createSyntaxAnalyzer("main<int>() = 0", false)
		def errors = new HashSet([
				ArkoiError.builder()
						.compilerClass(parser.getCompilerClass())
						.message(ParserErrorType.SYNTAX_ERROR_TEMPLATE)
						.arguments("Function", "'fun'", "main")
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
		FunctionStatement.builder()
				.parser(parser)
				.build()
				.parseAST(null)
		parser.getErrorHandler().getCompilerErrors() == errors
	}
	
	def "#2 <identifier> expected"() {
		given:
		def parser = this.createSyntaxAnalyzer("fun .<int>() = 0", false)
		def errors = new HashSet([
				ArkoiError.builder()
						.compilerClass(parser.getCompilerClass())
						.message(ParserErrorType.SYNTAX_ERROR_TEMPLATE)
						.arguments("Function", "<identifier>", ".")
						.positions([
								ArkoiError.ErrorPosition.builder()
										.lineRange(LineRange.make(
												parser.getCompilerClass(),
												0, 0))
										.charStart(4)
										.charEnd(5)
										.build()])
						.build()
		])
		
		expect:
		FunctionStatement.builder()
				.parser(parser)
				.build()
				.parseAST(null)
		parser.getErrorHandler().getCompilerErrors() == errors
	}
	
	def "#3 '<' expected"() {
		given:
		def parser = this.createSyntaxAnalyzer("fun main int>() = 0", false)
		def errors = new HashSet([
				ArkoiError.builder()
						.compilerClass(parser.getCompilerClass())
						.message(ParserErrorType.SYNTAX_ERROR_TEMPLATE)
						.arguments("Function", "'<'", "int")
						.positions([
								ArkoiError.ErrorPosition.builder()
										.lineRange(LineRange.make(
												parser.getCompilerClass(),
												0, 0))
										.charStart(9)
										.charEnd(12)
										.build()])
						.build()
		])
		
		expect:
		FunctionStatement.builder()
				.parser(parser)
				.build()
				.parseAST(null)
		parser.getErrorHandler().getCompilerErrors() == errors
	}
	
	def "#4 '>' expected"() {
		given:
		def parser = this.createSyntaxAnalyzer("fun main<int() = 0", false)
		def errors = new HashSet([
				ArkoiError.builder()
						.compilerClass(parser.getCompilerClass())
						.message(ParserErrorType.SYNTAX_ERROR_TEMPLATE)
						.arguments("Function", "'>'", "(")
						.positions([
								ArkoiError.ErrorPosition.builder()
										.lineRange(LineRange.make(
												parser.getCompilerClass(),
												0, 0))
										.charStart(12)
										.charEnd(13)
										.build()])
						.build()
		])
		
		expect:
		FunctionStatement.builder()
				.parser(parser)
				.build()
				.parseAST(null)
		parser.getErrorHandler().getCompilerErrors() == errors
	}
	
	def "#5 '(' expected"() {
		given:
		def parser = this.createSyntaxAnalyzer("fun main<int>) = 0", false)
		def errors = new HashSet([
				ArkoiError.builder()
						.compilerClass(parser.getCompilerClass())
						.message(ParserErrorType.SYNTAX_ERROR_TEMPLATE)
						.arguments("Function", "'('", ")")
						.positions([
								ArkoiError.ErrorPosition.builder()
										.lineRange(LineRange.make(
												parser.getCompilerClass(),
												0, 0
										))
										.charStart(13)
										.charEnd(14)
										.build()
						])
						.build()
		])
		
		expect:
		FunctionStatement.builder()
				.parser(parser)
				.build()
				.parseAST(null)
		parser.getErrorHandler().getCompilerErrors() == errors
	}
	
	def "#6 <block> expected"() {
		given:
		def parser = this.createSyntaxAnalyzer("fun main<int>() . 0", false)
		def errors = new HashSet([
				ArkoiError.builder()
						.compilerClass(parser.getCompilerClass())
						.message(ParserErrorType.SYNTAX_ERROR_TEMPLATE)
						.arguments("Function", "<block>", ".")
						.positions([
								ArkoiError.ErrorPosition.builder()
										.lineRange(LineRange.make(
												parser.getCompilerClass(),
												0, 0
										))
										.charStart(16)
										.charEnd(17)
										.build()
						])
						.build()
		])
		
		expect:
		FunctionStatement.builder()
				.parser(parser)
				.build()
				.parseAST(null)
		parser.getErrorHandler().getCompilerErrors() == errors
	}
	
}
