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

import com.arkoisystems.arkoicompiler.bootstrap.Bootstrap
import com.arkoisystems.arkoicompiler.utils.FileUtils
import spock.lang.Specification

class ArkoiCompilerSpec extends Specification {
	
	def "are natives compilable"() {
		given:
		def arkoiCompiler = new ArkoiCompiler()
		
		expect:
		arkoiCompiler.compile(System.out)
	}
	
	def "can pass basic test"() {
		expect:
		Bootstrap.compile("../examples/basic", false)
	}
	
}
