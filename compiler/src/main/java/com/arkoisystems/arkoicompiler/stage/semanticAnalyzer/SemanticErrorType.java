/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 02, 2020
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
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer;

public class SemanticErrorType
{
    
    public static final String FUNCTION_DESC_ALREADY_EXISTS = "There already exists another function with the same name and arguments:";
    public static final String FUNCTION_ANNOTATION_SAME_NAME = "This function already has another annotation with the same name:";
    public static final String FUNCTION_ARGUMENT_SAME_NAME = "There already exists another arguments with the same name:";
    public static final String FUNCTION_NO_SUCH_FUNCTION = "There doesn't exists any function with this name and arguments:";
    
    public static final String IMPORT_NAME_ALREADY_TAKEN = "There already exists %s with the same name:";
    public static final String IMPORT_INVALID_PATH = "The specified path doesn't lead to an valid arkoi file:";
    
    public static final String BLOCK_INLINE_EXPRESSION = "You can't add any other ASTs besides an Expression to an inlined block:";
    public static final String BLOCK_AST_NOT_SUPPORTED = "The declared AST inside the block is not supported currently:";
    
    public static final String VARIABLE_ANNOTATION_SAME_NAME = "This variable already has another annotation with the same name:";
    public static final String VARIABLE_NAME_ALREADY_TAKEN = "There already exists %s with the same name:";
    
    public static final String BINARY_ADDITION_NOT_SUPPORTED = "Addition isn't supported by these operable.";
    public static final String BINARY_SUBTRACTION_NOT_SUPPORTED = "Subtraction isn't supported by these operable.";
    public static final String BINARY_MULTIPLICATION_NOT_SUPPORTED = "Multiplication isn't supported by these operable.";
    public static final String BINARY_DIVISION_NOT_SUPPORTED = "Division isn't supported by these operable.";
    public static final String BINARY_MODULO_NOT_SUPPORTED = "Modulo isn't supported by these operable.";
    public static final String BINARY_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the binary expression:";
    public static final String BINARY_ADDITION_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the binary addition operator:";
    public static final String BINARY_SUBTRACTION_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the binary subtraction operator:";
    public static final String BINARY_MULTIPLICATION_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the binary multiplication operator:";
    public static final String BINARY_DIVISION_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the binary division operator:";
    public static final String BINARY_MODULO_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the binary modular operator:";
    public static final String BINARY_EXPONENTIAL_NOT_SUPPORTED = "The operable isn't supported by the binary exponential operator:";
    public static final String BINARY_EXPONENTIAL_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the binary division operator:";
    
    public static final String ASSIGN_ASSIGNMENT_NOT_SUPPORTED = "Assignment isn't supported by these operable.";
    public static final String ASSIGN_ADD_ASSIGNMENT_NOT_SUPPORTED = "Addition assignment isn't supported by these operable.";
    public static final String ASSIGN_SUB_ASSIGNMENT_NOT_SUPPORTED = "Subtraction assignment isn't supported by these operable.";
    public static final String ASSIGN_MUL_ASSIGNMENT_NOT_SUPPORTED = "Multiplication assignment isn't supported by these operable.";
    public static final String ASSIGN_DIV_ASSIGNMENT_NOT_SUPPORTED = "Division assignment isn't supported by these operable.";
    public static final String ASSIGN_MOD_ASSIGNMENT_NOT_SUPPORTED = "Modulo assignment isn't supported by these operable.";
    public static final String ASSIGN_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the assignment expression:";
    public static final String ASSIGN_ASSIGNMENT_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the assign operator";
    public static final String ASSIGN_ADD_ASSIGNMENT_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the addition assignment operator:";
    public static final String ASSIGN_SUB_ASSIGNMENT_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the subtract assignment operator:";
    public static final String ASSIGN_MUL_ASSIGNMENT_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the multiplication assignment operator:";
    public static final String ASSIGN_DIV_ASSIGNMENT_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the division assignment operator:";
    public static final String ASSIGN_MOD_ASSIGNMENT_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the modular assignment operator:";
    
    public static final String EQUAL_NOT_SUPPORTED = "The \"equal\" operation isn't supported by these operable.";
    public static final String NOT_EQUAL_NOT_SUPPORTED = "The \"not equal\" operation isn't supported by these operable.";
    
    public static final String LOGICAL_OR_NOT_SUPPORTED = "The \"logical or\" operation isn't supported by these operable.";
    public static final String LOGICAL_AND_NOT_SUPPORTED = "The \"logical and\" operation isn't supported by these operable.";
    
    public static final String POSTFIX_ADD_NOT_SUPPORTED = "The \"post addition\" operator isn't supported by this operable.";
    public static final String POSTFIX_SUB_NOT_SUPPORTED = "The \"post subtraction\" operator isn't supported by this operable.";
    public static final String POSTFIX_SUB_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the post-subtraction operation:";
    public static final String POSTFIX_ADD_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the post-addition operation:";
    public static final String POSTFIX_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the postfix expression:";
    
    public static final String PREFIX_ADD_NOT_SUPPORTED = "The \"pre addition\" operator isn't supported by this operable.";
    public static final String PREFIX_SUB_NOT_SUPPORTED = "The \"pre subtraction\" operator isn't supported by this operable.";
    public static final String PREFIX_NEGATE_NOT_SUPPORTED = "The \"negate\" operator isn't supported by this operable.";
    public static final String PREFIX_AFFIRM_NOT_SUPPORTED = "The \"affirm\" operator isn't supported by this operable.";
    public static final String PREFIX_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the prefix expression:";
    public static final String PREFIX_ADD_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the pre-addition operation:";
    public static final String PREFIX_SUB_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the pre-subtraction operation:";
    public static final String PREFIX_NEGATE_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the negate operation:";
    public static final String PREFIX_AFFIRM_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the affirm operation:";
    
    public static final String RELATIONAL_LESS_THAN_NOT_SUPPORTED = "The \"less than\" operator isn't supported by these operable.";
    public static final String RELATIONAL_GREATER_THAN_NOT_SUPPORTED = "The \"greater than\" operator isn't supported by these operable.";
    public static final String RELATIONAL_LESS_EQUAL_THAN_NOT_SUPPORTED = "The \"less equal than\" operator isn't supported by these operable.";
    public static final String RELATIONAL_GREATER_EQUAL_THAN_NOT_SUPPORTED = "The \"greater equal than\" operator isn't supported by these operable.";
    public static final String RELATIONAL_IS_NOT_SUPPORTED = "The \"is\" keyword isn't supported by these operable.";
    
    public static final String EXPRESSION_AST_NOT_SUPPORTED = "This AST isn't supported by an expression:";
    
    public static final String IDENTIFIER_CALL_AST_NOT_SUPPORTED = "The found identifier isn't supported by the identifier call operation:";
    public static final String IDENTIFIER_CALL_NO_SUCH_IDENTIFIER = "No identifier with the same name could be found:";
    
    public static final String CAST_OPERABLE_NOT_SUPPORTED = "The operable isn't supported by the cast expression:";
    
}
