// This is a generated file. Not intended for manual editing.
package com.arkoisystems.lang.arkoi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.arkoisystems.lang.arkoi.psi.ArkoiElementType;
import com.arkoisystems.lang.arkoi.psi.ArkoiTokenType;
import com.arkoisystems.lang.arkoi.psi.impl.*;

public interface ArkoiTokenTypes {

  IElementType ASSIGNMENT_EXPRESSION = new ArkoiElementType("ASSIGNMENT_EXPRESSION");
  IElementType BINARY_ADDITIVE_EXPRESSION = new ArkoiElementType("BINARY_ADDITIVE_EXPRESSION");
  IElementType BINARY_MULTIPLICATIVE_EXPRESSION = new ArkoiElementType("BINARY_MULTIPLICATIVE_EXPRESSION");
  IElementType BRACE_BLOCK = new ArkoiElementType("BRACE_BLOCK");
  IElementType CAST_EXPRESSION = new ArkoiElementType("CAST_EXPRESSION");
  IElementType COLLECTION_EXPRESSION = new ArkoiElementType("COLLECTION_EXPRESSION");
  IElementType EXPRESSION = new ArkoiElementType("EXPRESSION");
  IElementType FUNCTION_DECLARATION = new ArkoiElementType("FUNCTION_DECLARATION");
  IElementType FUNCTION_INVOKE = new ArkoiElementType("FUNCTION_INVOKE");
  IElementType IMPORT_DECLARATION = new ArkoiElementType("IMPORT_DECLARATION");
  IElementType INLINED_BLOCK = new ArkoiElementType("INLINED_BLOCK");
  IElementType LITERALS = new ArkoiElementType("LITERALS");
  IElementType OPERABLE = new ArkoiElementType("OPERABLE");
  IElementType OPERABLE_EXPRESSION = new ArkoiElementType("OPERABLE_EXPRESSION");
  IElementType POSTFIX_EXPRESSION = new ArkoiElementType("POSTFIX_EXPRESSION");
  IElementType PREFIX_EXPRESSION = new ArkoiElementType("PREFIX_EXPRESSION");
  IElementType PRIMITIVES = new ArkoiElementType("PRIMITIVES");
  IElementType RETURN_DECLARATION = new ArkoiElementType("RETURN_DECLARATION");
  IElementType VARIABLE_DECLARATION = new ArkoiElementType("VARIABLE_DECLARATION");
  IElementType VARIABLE_INVOKE = new ArkoiElementType("VARIABLE_INVOKE");

  IElementType ADD_ASSIGN = new ArkoiTokenType("+=");
  IElementType AS = new ArkoiTokenType("as");
  IElementType ASTERISK = new ArkoiTokenType("*");
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
      if (type == ASSIGNMENT_EXPRESSION) {
        return new ArkoiAssignmentExpressionImpl(node);
      }
      else if (type == BINARY_ADDITIVE_EXPRESSION) {
        return new ArkoiBinaryAdditiveExpressionImpl(node);
      }
      else if (type == BINARY_MULTIPLICATIVE_EXPRESSION) {
        return new ArkoiBinaryMultiplicativeExpressionImpl(node);
      }
      else if (type == BRACE_BLOCK) {
        return new ArkoiBraceBlockImpl(node);
      }
      else if (type == CAST_EXPRESSION) {
        return new ArkoiCastExpressionImpl(node);
      }
      else if (type == COLLECTION_EXPRESSION) {
        return new ArkoiCollectionExpressionImpl(node);
      }
      else if (type == EXPRESSION) {
        return new ArkoiExpressionImpl(node);
      }
      else if (type == FUNCTION_DECLARATION) {
        return new ArkoiFunctionDeclarationImpl(node);
      }
      else if (type == FUNCTION_INVOKE) {
        return new ArkoiFunctionInvokeImpl(node);
      }
      else if (type == IMPORT_DECLARATION) {
        return new ArkoiImportDeclarationImpl(node);
      }
      else if (type == INLINED_BLOCK) {
        return new ArkoiInlinedBlockImpl(node);
      }
      else if (type == LITERALS) {
        return new ArkoiLiteralsImpl(node);
      }
      else if (type == OPERABLE) {
        return new ArkoiOperableImpl(node);
      }
      else if (type == OPERABLE_EXPRESSION) {
        return new ArkoiOperableExpressionImpl(node);
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
      else if (type == VARIABLE_DECLARATION) {
        return new ArkoiVariableDeclarationImpl(node);
      }
      else if (type == VARIABLE_INVOKE) {
        return new ArkoiVariableInvokeImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
