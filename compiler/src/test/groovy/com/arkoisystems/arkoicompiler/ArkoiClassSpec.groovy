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
package com.arkoisystems.arkoicompiler

import spock.lang.Specification

class ArkoiClassSpec extends Specification {
	
	def "is class and semantic analyzer detailed"() {
		given:
		def arkoiCompiler = new ArkoiCompiler()
		
		expect:
		arkoiClass.detailed == detailed
		arkoiClass.semanticAnalyzer.detailed == detailed
		
		where:
		arkoiClass                                            || detailed
		new ArkoiClass(arkoiCompiler, "", new byte[0], true)  || true
		new ArkoiClass(arkoiCompiler, "", new byte[0], false) || false
	}
	
	def "is content not null"() {
		given:
		def arkoiCompiler = new ArkoiCompiler()
		def arkoiClass = new ArkoiClass(arkoiCompiler, "", new byte[0], true)
		
		expect:
		arkoiClass.content != null
	}
	
}
