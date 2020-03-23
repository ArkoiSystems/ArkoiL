// This is a generated file. Not intended for manual editing.
package com.arkoisystems.lang.arkoi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.arkoisystems.lang.arkoi.psi.ArkoiElementType;
import com.arkoisystems.lang.arkoi.psi.ArkoiTokenType;
import com.arkoisystems.lang.arkoi.psi.impl.*;

public interface ArkoiTokenTypes {

  IElementType ADD_ASSIGN_EXPRESSION = new ArkoiElementType("ADD_ASSIGN_EXPRESSION");
  IElementType ANNOTATION_CALL = new ArkoiElementType("ANNOTATION_CALL");
  IElementType ARGUMENT_LIST = new ArkoiElementType("ARGUMENT_LIST");
  IElementType ARGUMENT_LIST_PART = new ArkoiElementType("ARGUMENT_LIST_PART");
  IElementType ASSIGNMENT_EXPRESSION = new ArkoiElementType("ASSIGNMENT_EXPRESSION");
  IElementType ASSIGN_EXPRESSION = new ArkoiElementType("ASSIGN_EXPRESSION");
  IElementType ASSIGN_EXPRESSION_PART = new ArkoiElementType("ASSIGN_EXPRESSION_PART");
  IElementType BINARY_ADDITIVE_EXPRESSION = new ArkoiElementType("BINARY_ADDITIVE_EXPRESSION");
  IElementType BINARY_ADDITIVE_EXPRESSION_PART = new ArkoiElementType("BINARY_ADDITIVE_EXPRESSION_PART");
  IElementType BINARY_ADD_EXPRESSION = new ArkoiElementType("BINARY_ADD_EXPRESSION");
  IElementType BINARY_DIV_EXPRESSION = new ArkoiElementType("BINARY_DIV_EXPRESSION");
  IElementType BINARY_MOD_EXPRESSION = new ArkoiElementType("BINARY_MOD_EXPRESSION");
  IElementType BINARY_MULTIPLICATIVE_EXPRESSION = new ArkoiElementType("BINARY_MULTIPLICATIVE_EXPRESSION");
  IElementType BINARY_MULTIPLICATIVE_EXPRESSION_PART = new ArkoiElementType("BINARY_MULTIPLICATIVE_EXPRESSION_PART");
  IElementType BINARY_MUL_EXPRESSION = new ArkoiElementType("BINARY_MUL_EXPRESSION");
  IElementType BINARY_SUB_EXPRESSION = new ArkoiElementType("BINARY_SUB_EXPRESSION");
  IElementType BLOCK_DECLARATION = new ArkoiElementType("BLOCK_DECLARATION");
  IElementType BRACE_BLOCK = new ArkoiElementType("BRACE_BLOCK");
  IElementType CAST_EXPRESSION = new ArkoiElementType("CAST_EXPRESSION");
  IElementType COLLECTION = new ArkoiElementType("COLLECTION");
  IElementType COMMENT_DECLARATION = new ArkoiElementType("COMMENT_DECLARATION");
  IElementType DIV_ASSIGN_EXPRESSION = new ArkoiElementType("DIV_ASSIGN_EXPRESSION");
  IElementType EXPONENTIAL_EXPRESSION = new ArkoiElementType("EXPONENTIAL_EXPRESSION");
  IElementType EXPONENTIAL_EXPRESSION_PART = new ArkoiElementType("EXPONENTIAL_EXPRESSION_PART");
  IElementType EXPRESSION = new ArkoiElementType("EXPRESSION");
  IElementType EXPRESSION_LIST = new ArkoiElementType("EXPRESSION_LIST");
  IElementType EXP_ASSIGN_EXPRESSION = new ArkoiElementType("EXP_ASSIGN_EXPRESSION");
  IElementType FUNCTION_CALL_PART = new ArkoiElementType("FUNCTION_CALL_PART");
  IElementType FUNCTION_DECLARATION = new ArkoiElementType("FUNCTION_DECLARATION");
  IElementType IDENTIFIER_CALL = new ArkoiElementType("IDENTIFIER_CALL");
  IElementType IDENTIFIER_CALL_PART = new ArkoiElementType("IDENTIFIER_CALL_PART");
  IElementType IMPORT_DECLARATION = new ArkoiElementType("IMPORT_DECLARATION");
  IElementType INLINED_BLOCK = new ArkoiElementType("INLINED_BLOCK");
  IElementType LINE_COMMENT = new ArkoiElementType("LINE_COMMENT");
  IElementType LITERALS = new ArkoiElementType("LITERALS");
  IElementType MOD_ASSIGN_EXPRESSION = new ArkoiElementType("MOD_ASSIGN_EXPRESSION");
  IElementType MUL_ASSIGN_EXPRESSION = new ArkoiElementType("MUL_ASSIGN_EXPRESSION");
  IElementType OPERABLE = new ArkoiElementType("OPERABLE");
  IElementType OPERABLE_EXPRESSION = new ArkoiElementType("OPERABLE_EXPRESSION");
  IElementType PARAMETER_LIST = new ArkoiElementType("PARAMETER_LIST");
  IElementType PARAMETER_LIST_PART = new ArkoiElementType("PARAMETER_LIST_PART");
  IElementType PARENTHESIZED_EXPRESSION = new ArkoiElementType("PARENTHESIZED_EXPRESSION");
  IElementType POSTFIX_EXPRESSION = new ArkoiElementType("POSTFIX_EXPRESSION");
  IElementType PREFIX_EXPRESSION = new ArkoiElementType("PREFIX_EXPRESSION");
  IElementType PRIMITIVES = new ArkoiElementType("PRIMITIVES");
  IElementType RETURN_DECLARATION = new ArkoiElementType("RETURN_DECLARATION");
  IElementType SUB_ASSIGN_EXPRESSION = new ArkoiElementType("SUB_ASSIGN_EXPRESSION");
  IElementType VARIABLE_DECLARATION = new ArkoiElementType("VARIABLE_DECLARATION");

  IElementType ADD_ASSIGN = new ArkoiTokenType("+=");
  IElementType AS = new ArkoiTokenType("as");
  IElementType ASTERISK = new ArkoiTokenType("*");
  IElementType AT = new ArkoiTokenType("@");
  IElementType BOOLEAN = new ArkoiTokenType("boolean");
  IElementType BYTE = new ArkoiTokenType("byte");
  IElementType CHAR = new ArkoiTokenType("char");
  IElementType COLON = new ArkoiTokenType(":");
  IElementType COMMA = new ArkoiTokenType(",");
  IElementType COMMENT = new ArkoiTokenType("COMMENT");
  IElementType DIV_ASSIGN = new ArkoiTokenType("/=");
  IElementType DOT = new ArkoiTokenType(".");
  IElementType DOUBLE_ASTERISK = new ArkoiTokenType("**");
  IElementType DOUBLE_MINUS = new ArkoiTokenType("--");
  IElementType DOUBLE_PLUS = new ArkoiTokenType("++");
  IElementType EQUALS = new ArkoiTokenType("=");
  IElementType EXP_ASSIGN = new ArkoiTokenType("**=");
  IElementType FUN = new ArkoiTokenType("fun");
  IElementType GREATER_THAN = new ArkoiTokenType(">");
  IElementType IDENTIFIER = new ArkoiTokenType("IDENTIFIER");
  IElementType IMPORT = new ArkoiTokenType("import");
  IElementType INT = new ArkoiTokenType("int");
  IElementType LESS_THAN = new ArkoiTokenType("<");
  IElementType LONG = new ArkoiTokenType("long");
  IElementType L_BRACE = new ArkoiTokenType("{");
  IElementType L_BRACKET = new ArkoiTokenType("[");
  IElementType L_PARENTHESIS = new ArkoiTokenType("(");
  IElementType MINUS = new ArkoiTokenType("-");
  IElementType MOD_ASSIGN = new ArkoiTokenType("%=");
  IElementType MUL_ASSIGN = new ArkoiTokenType("*=");
  IElementType NUMBER_LITERAL = new ArkoiTokenType("NUMBER_LITERAL");
  IElementType PERCENT = new ArkoiTokenType("%");
  IElementType PLUS = new ArkoiTokenType("+");
  IElementType RETURN = new ArkoiTokenType("return");
  IElementType R_BRACE = new ArkoiTokenType("}");
  IElementType R_BRACKET = new ArkoiTokenType("]");
  IElementType R_PARENTHESIS = new ArkoiTokenType(")");
  IElementType SEMICOLON = new ArkoiTokenType(";");
  IElementType SHORT = new ArkoiTokenType("short");
  IElementType SLASH = new ArkoiTokenType("/");
  IElementType STRING = new ArkoiTokenType("string");
  IElementType STRING_LITERAL = new ArkoiTokenType("STRING_LITERAL");
  IElementType SUB_ASSIGN = new ArkoiTokenType("-=");
  IElementType THIS = new ArkoiTokenType("this");
  IElementType VAR = new ArkoiTokenType("var");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ADD_ASSIGN_EXPRESSION) {
        return new ArkoiAddAssignExpressionImpl(node);
      }
      else if (type == ANNOTATION_CALL) {
        return new ArkoiAnnotationCallImpl(node);
      }
      else if (type == ARGUMENT_LIST) {
        return new ArkoiArgumentListImpl(node);
      }
      else if (type == ARGUMENT_LIST_PART) {
        return new ArkoiArgumentListPartImpl(node);
      }
      else if (type == ASSIGNMENT_EXPRESSION) {
        return new ArkoiAssignmentExpressionImpl(node);
      }
      else if (type == ASSIGN_EXPRESSION) {
        return new ArkoiAssignExpressionImpl(node);
      }
      else if (type == ASSIGN_EXPRESSION_PART) {
        return new ArkoiAssignExpressionPartImpl(node);
      }
      else if (type == BINARY_ADDITIVE_EXPRESSION) {
        return new ArkoiBinaryAdditiveExpressionImpl(node);
      }
      else if (type == BINARY_ADDITIVE_EXPRESSION_PART) {
        return new ArkoiBinaryAdditiveExpressionPartImpl(node);
      }
      else if (type == BINARY_ADD_EXPRESSION) {
        return new ArkoiBinaryAddExpressionImpl(node);
      }
      else if (type == BINARY_DIV_EXPRESSION) {
        return new ArkoiBinaryDivExpressionImpl(node);
      }
      else if (type == BINARY_MOD_EXPRESSION) {
        return new ArkoiBinaryModExpressionImpl(node);
      }
      else if (type == BINARY_MULTIPLICATIVE_EXPRESSION) {
        return new ArkoiBinaryMultiplicativeExpressionImpl(node);
      }
      else if (type == BINARY_MULTIPLICATIVE_EXPRESSION_PART) {
        return new ArkoiBinaryMultiplicativeExpressionPartImpl(node);
      }
      else if (type == BINARY_MUL_EXPRESSION) {
        return new ArkoiBinaryMulExpressionImpl(node);
      }
      else if (type == BINARY_SUB_EXPRESSION) {
        return new ArkoiBinarySubExpressionImpl(node);
      }
      else if (type == BLOCK_DECLARATION) {
        return new ArkoiBlockDeclarationImpl(node);
      }
      else if (type == BRACE_BLOCK) {
        return new ArkoiBraceBlockImpl(node);
      }
      else if (type == CAST_EXPRESSION) {
        return new ArkoiCastExpressionImpl(node);
      }
      else if (type == COLLECTION) {
        return new ArkoiCollectionImpl(node);
      }
      else if (type == COMMENT_DECLARATION) {
        return new ArkoiCommentDeclarationImpl(node);
      }
      else if (type == DIV_ASSIGN_EXPRESSION) {
        return new ArkoiDivAssignExpressionImpl(node);
      }
      else if (type == EXPONENTIAL_EXPRESSION) {
        return new ArkoiExponentialExpressionImpl(node);
      }
      else if (type == EXPONENTIAL_EXPRESSION_PART) {
        return new ArkoiExponentialExpressionPartImpl(node);
      }
      else if (type == EXPRESSION) {
        return new ArkoiExpressionImpl(node);
      }
      else if (type == EXPRESSION_LIST) {
        return new ArkoiExpressionListImpl(node);
      }
      else if (type == EXP_ASSIGN_EXPRESSION) {
        return new ArkoiExpAssignExpressionImpl(node);
      }
      else if (type == FUNCTION_CALL_PART) {
        return new ArkoiFunctionCallPartImpl(node);
      }
      else if (type == FUNCTION_DECLARATION) {
        return new ArkoiFunctionDeclarationImpl(node);
      }
      else if (type == IDENTIFIER_CALL) {
        return new ArkoiIdentifierCallImpl(node);
      }
      else if (type == IDENTIFIER_CALL_PART) {
        return new ArkoiIdentifierCallPartImpl(node);
      }
      else if (type == IMPORT_DECLARATION) {
        return new ArkoiImportDeclarationImpl(node);
      }
      else if (type == INLINED_BLOCK) {
        return new ArkoiInlinedBlockImpl(node);
      }
      else if (type == LINE_COMMENT) {
        return new ArkoiLineCommentImpl(node);
      }
      else if (type == LITERALS) {
        return new ArkoiLiteralsImpl(node);
      }
      else if (type == MOD_ASSIGN_EXPRESSION) {
        return new ArkoiModAssignExpressionImpl(node);
      }
      else if (type == MUL_ASSIGN_EXPRESSION) {
        return new ArkoiMulAssignExpressionImpl(node);
      }
      else if (type == OPERABLE) {
        return new ArkoiOperableImpl(node);
      }
      else if (type == OPERABLE_EXPRESSION) {
        return new ArkoiOperableExpressionImpl(node);
      }
      else if (type == PARAMETER_LIST) {
        return new ArkoiParameterListImpl(node);
      }
      else if (type == PARAMETER_LIST_PART) {
        return new ArkoiParameterListPartImpl(node);
      }
      else if (type == PARENTHESIZED_EXPRESSION) {
        return new ArkoiParenthesizedExpressionImpl(node);
      }
      else if (type == POSTFIX_EXPRESSION) {
        return new ArkoiPostfixExpressionImpl(node);
      }
      else if (type == PREFIX_EXPRESSION) {
        return new ArkoiPrefixExpressionImpl(node);
      }
      else if (type == PRIMITIVES) {
        return new ArkoiPrimitivesImpl(node);
      }
      else if (type == RETURN_DECLARATION) {
        return new ArkoiReturnDeclarationImpl(node);
      }
      else if (type == SUB_ASSIGN_EXPRESSION) {
        return new ArkoiSubAssignExpressionImpl(node);
      }
      else if (type == VARIABLE_DECLARATION) {
        return new ArkoiVariableDeclarationImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
