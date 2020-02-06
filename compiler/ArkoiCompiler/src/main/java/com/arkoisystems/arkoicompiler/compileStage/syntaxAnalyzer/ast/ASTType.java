package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka Timo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
public enum ASTType
{
    
    IMPORT_STATEMENT,
    THIS_STATEMENT,
    RETURN_STATEMENT,
    
    BINARY_EXPRESSION,
    PARENTHESIZED_EXPRESSION,
    ASSIGNMENT_EXPRESSION,
    EQUALITY_EXPRESSION,
    LOGICAL_EXPRESSION,
    POSTFIX_EXPRESSION,
    PREFIX_EXPRESSION,
    RELATIONAL_EXPRESSION,
    EXPRESSION,
    
    ARGUMENT_DEFINITION,
    VARIABLE_DEFINITION,
    FUNCTION_DEFINITION,
    
    IDENTIFIER_CALL,
    FUNCTION_CALL,
    
    IDENTIFIER_INVOKE,
    FUNCTION_INVOKE,
    
    IDENTIFIER_INVOKE_OPERABLE,
    IDENTIFIER_CALL_OPERABLE,
    FUNCTION_RESULT_OPERABLE,
    COLLECTION_OPERABLE,
    NUMBER_OPERABLE,
    STRING_OPERABLE,
    
    ANNOTATION,
    BLOCK,
    ROOT,
    TYPE;
}
