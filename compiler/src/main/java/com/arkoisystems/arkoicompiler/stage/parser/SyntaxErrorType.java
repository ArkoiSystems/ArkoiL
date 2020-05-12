/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 03, 2020
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
package com.arkoisystems.arkoicompiler.stage.parser;

public class SyntaxErrorType
{
    
    public static final String SYNTAX_ERROR_TEMPLATE = "%s expected %s but got '%s' instead.";
    
    public static final String ROOT_NO_PARSER_FOUND = "There's no parser that can parse this token.";
    
    public static final String BLOCK_NO_PARSER_FOUND = "There's no parser that can parse this token.";
    
    public static final String OPERABLE_NOT_SUPPORTED = "This token is not a supported operable.";
   
}
