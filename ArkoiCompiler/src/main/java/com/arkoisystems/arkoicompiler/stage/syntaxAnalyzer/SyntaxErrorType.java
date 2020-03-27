/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 03, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer;

public class SyntaxErrorType
{
    
    public static final String TYPE_DOES_NOT_START_WITH_TYPE_KEYWORD = "Couldn't parse the Type because the parsing doesn't start with a type keyword.";
    public static final String TYPE_NOT_A_VALID_TYPE = "Couldn't parse the Type because it isn't a valid type keyword.";
    
    public static final String ROOT_BLOCK_FUNCTION_HAS_WRONG_ENDING = "Couldn't parse the \"function definition\" statement because it doesn't end with a closing brace.";
    public static final String ROOT_NO_PARSER_FOUND = "Couldn't parse the AST because no parser could parse this token. Check for misspelling or something else.";
    
    public static final String BLOCK_BLOCK_HAS_WRONG_ENDING = "Couldn't parse the \"BlockAST\" because it doesn't end with a closing brace.";
    public static final String BLOCK_NO_PARSER_FOUND = "Couldn't parse the AST because no parser could parse this token. Check for misspelling or something else.";
    public static final String BLOCK_NO_VALID_EXPRESSION = "Couldn't parse the BlockAST because the equal sign isn't followed by an valid expression.";
    public static final String BLOCK_INVALID_SEPARATOR = "Couldn't parse the BlockAST because the parsing doesn't start with an opening brace or equal sign to identify the block type.";
    public static final String BLOCK_WRONG_START = "Couldn't parse the BlockAST because it isn't declared inside a function/variable definition or block.";
    
    public static final String PARAMETER_WRONG_START = "Couldn't parse the parameter definition because the parsing doesn't start with an identifier as name.";
    public static final String PARAMETER_NO_SEPARATOR = "Couldn't parse the parameter definition because the parameter name isn't followed by a colon.";
    public static final String PARAMETER_NO_VALID_TYPE = "Couldn't parse the parameter definition because the colon isn't followed by a valid type.";
    
    public static final String VARIABLE_DEFINITION_WRONG_PARENT = "Couldn't parse the \"variable definition\" statement because it isn't declared inside the root file or in a block.";
    public static final String VARIABLE_DEFINITION_WRONG_STAR = "Couldn't parse the \"variable definition\" statement because the parsing doesn't start with the \"var\" keyword.";
    public static final String VARIABLE_DEFINITION_NO_NAME = "Couldn't parse the \"variable definition\" statement because the \"var\" keyword isn't followed by an variable name.";
    public static final String VARIABLE_DEFINITION_NO_EQUAL_SIGN = "Couldn't parse the \"variable definition\" statement because the variable name isn't followed by an equal sign for deceleration of the following expression.";
    public static final String VARIABLE_DEFINITION_ERROR_DURING_EXPRESSION_PARSING = "Couldn't parse the \"variable definition\" statement because the equal sign is followed by an invalid expression.";
    
    public static final String IMPORT_DEFINITION_WRONG_PARENT = "Couldn't parse the \"import\" statement because it isn't declared inside the root file.";
    public static final String IMPORT_DEFINITION_WRONG_START = "Couldn't parse the \"import\" statement because the parsing doesn't start with the \"import\" keyword.";
    public static final String IMPORT_DEFINITION_NO_FILEPATH = "Couldn't parse the \"import\" statement because the \"import\" keyword isn't followed by an file path.";
    public static final String IMPORT_DEFINITION_NOT_FOLLOWED_BY_NAME = "Couldn't parse the \"import\" statement because the \"named\" keyword isn't followed by an name identifier.";
    
    public static final String RETURN_STATEMENT_WRONG_PARENT = "Couldn't parse the \"return\" statement because it isn't declared inside a block.";
    public static final String RETURN_STATEMENT_WRONG_START = "Couldn't parse the \"return\" statement because the parsing doesn't start with the \"return\" keyword.";
    public static final String RETURN_STATEMENT_NO_VALID_EXPRESSION = "Couldn't parse the \"return\" statement because the keyword isn't followed by an valid expression.";
    
    public static final String FUNCTION_DEFINITION_WRONG_PARENT = "Couldn't parse the \"function definition\" statement because it isn't declared inside the root file.";
    public static final String FUNCTION_DEFINITION_WRONG_START = "Couldn't parse the \"function definition\" statement because the parsing doesn't start with the \"fun\" keyword.";
    public static final String FUNCTION_DEFINITION_NO_NAME = "Couldn't parse the \"function definition\" statement because the \"fun\" keyword isn't followed by a function name.";
    public static final String FUNCTION_DEFINITION_WRONG_RETURN_TYPE_START = "Couldn't parse the \"function definition\" statement because the function name isn't followed by an opening sign aka. \"<\".";
    public static final String FUNCTION_DEFINITION_WRONG_RETURN_TYPE_ENDING = "Couldn't parse the \"function definition\" statement because the return type section doesn't end with a closing sign aka. \">\".";
    public static final String FUNCTION_DEFINITION_WRONG_ARGUMENTS_START = "Couldn't parse the \"function definition\" statement because the argument section doesn't start with an opening parenthesis.";
    public static final String FUNCTION_DEFINITION_WRONG_ARGUMENTS_ENDING = "Couldn't parse the \"function definition\" statement because the argument section doesn't end with a closing parenthesis.";
    public static final String FUNCTION_DEFINITION_WRONG_BLOCK_START = "Couldn't parse the \"function definition\" statement because after the argument section no opening brace or equal sign was declared. You need one of them to declare if this function uses a block or is inlined.";
    public static final String FUNCTION_DEFINITION_NO_VALID_BLOCK = "Couldn't parse the \"function definition\" statement because the block separator isn't followed by a valid block.";
    public static final String FUNCTION_DEFINITION_WRONG_BLOCK_ENDING = "Couldn't parse the \"function definition\" statement because a block needs to end with a closing brace aka. \"}\".";
    
    public static final String OPERABLE_UNSUPPORTED_SYMBOL_TYPE = "Couldn't parse the operable because the SymbolType isn't supported.";
    public static final String OPERABLE_IDENTIFIER_NOT_PARSEABLE = "Couldn't parse the operable statement because it isn't parsable.";
    public static final String OPERABLE_UNSUPPORTED_STATEMENT = "Couldn't parse the operable because it isn't a supported statement.";
    
    public static final String STRING_OPERABLE_NO_STRING = "Couldn't parse the string operable because the parsing doesn't start with a string.";
    
    public static final String NUMBER_OPERABLE_NO_NUMBER = "Couldn't parse the number operable because the parsing doesn't start with a number.";
    
    public static final String IDENTIFIER_CALL_NO_IDENTIFIER = "Couldn't parse the \"identifier call\" statement because the parsing doesn't start with an Identifier.";
    public static final String IDENTIFIER_THIS_NO_DOT = "Couldn't parse the \"identifier call\" statement because the \"this\" keyword isn't followed by a period.";
    public static final String IDENTIFIER_CALL_WRONG_CALL_APPEND = "Couldn't parse the \"identifier call\" statement because the period isn't followed by a valid identifier call.";
    
    public static final String COLLECTION_OPERABLE_WRONG_START = "Couldn't parse the collection operable because the parsing doesn't start with an opening bracket.";
    public static final String COLLECTION_OPERABLE_INVALID_EXPRESSION = "Couldn't parse the collection operable because there is an invalid expression inside.";
    public static final String COLLECTION_OPERABLE_WRONG_ENDING = "Couldn't parse the collection operable because it doesn't end with an closing bracket.";
    
    public static final String PARAMETERS_WRONG_START = "Couldn't parse the parameters because parsing doesn't start with an opening parenthesis.";
    public static final String PARAMETERS_WRONG_ENDING = "Couldn't parse the parameters because the parsing doesn't end with a closing parenthesis.";
    
    public static final String ANNOTATION_WRONG_PARENT = "Couldn't parse the Annotation because it isn't declared inside the root file.";
    public static final String ANNOTATION_WRONG_START = "Couldn't parse the Annotation because the parsing doesn't start with an at sign aka. \"@\".";
    public static final String ANNOTATION_NO_NAME = "Couldn't parse the Annotation because the at sign isn't followed by an name for the annotation.";
    public static final String ANNOTATION_NO_COMMA_SEPARATION = "Couldn't parse the Annotation because you can't define a non IdentifierToken inside the arguments section beside a comma after an argument if it should get followed by an extra one.";
    public static final String ANNOTATION_UNSUPPORTED_TOKEN_INSIDE = "Couldn't parse the Annotation because you can't declare something else then a closing bracket and a comma after an argument.";
    public static final String ANNOTATION_WRONG_ENDING = "Couldn't parse the Annotation because the arguments section doesn't end with a closing bracket.";
    public static final String ANNOTATION_NO_PARSEABLE_STATEMENT = "Couldn't parse the Annotation because an there is no parsable statement after it.";
    public static final String ANNOTATION_NO_VARIABLE_OR_FUNCTION = "Couldn't parse the Annotation because it isn't followed by an function or variable definition.";
    
    public static final String STATEMENT_WRONG_START = "Couldn't parse the statement because it doesn't start with a keyword.";
    
    public static final String EXPRESSION_ADD_ASSIGNMENT_SEPARATED = "Couldn't parse the add assignment expression because there is a whitespace between the operators.";
    public static final String EXPRESSION_EXPONENTIAL_OPERABLE_SEPARATED = "Couldn't parse the exponential expression because there is a whitespace between the operators.";
    
    public static final String EXPRESSION_PARENTHESIZED_WRONG_ENDING = "Couldn't parse the parenthesized expression because it doesn't end with a closing parenthesis.";
    public static final String EXPRESSION_PARENTHESIZED_WRONG_START = "Couldn't parse the parenthesized expression because it doesn't start with an opening parenthesis.";
    
    public static final String EXPRESSION_CAST_WRONG_IDENTIFIER = "Couldn't parse the cast expression because it contains a unsupported identifier.";
    
    public static final String ARGUMENT_WRONG_START = "Couldn't parse the argument definition because the parsing doesn't start with an identifier as name.";
    public static final String ARGUMENT_NO_SEPARATOR = "Couldn't parse the parameter definition because the argument name isnÄt followed by a colon.";
    public static final String PARAMETER_NO_VALID_EXPRESSION = "Couldn't parse the parameter definition because the equal sign isn't followed by a valid expression.";
    
    public static final String ARGUMENTS_WRONG_START = "Couldn't parse the arguments because parsing doesn't start with an opening bracket.";
    public static final String ARGUMENTS_WRONG_ENDING = "Couldn't parse the arguments because the parsing doesn't end with a closing bracket.";
    
    //    public static final String PARAMETER_WRONG_START = "Couldn't parse the argument definition because the parsing doesn't start with an identifier as name.";
    //    public static final String PARAMETER_NO_SEPARATOR = "Couldn't parse the argument definition because the argument name isn't followed by a colon.";
    //    public static final String PARAMETER_NO_VALID_TYPE = "Couldn't parse the argument definition because the colon isn't followed by a valid type.";
    //
}
