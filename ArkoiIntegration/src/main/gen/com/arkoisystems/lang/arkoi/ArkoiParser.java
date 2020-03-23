// This is a generated file. Not intended for manual editing.
package com.arkoisystems.lang.arkoi;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.arkoisystems.lang.arkoi.ArkoiTokenTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ArkoiParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return root(b, l + 1);
  }

  /* ********************************************************** */
  // ADD_ASSIGN assignment_expression
  public static boolean add_assign_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "add_assign_expression")) return false;
    if (!nextTokenIs(b, "<expression>", ADD_ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ADD_ASSIGN_EXPRESSION, "<expression>");
    r = consumeToken(b, ADD_ASSIGN);
    p = r; // pin = 1
    r = r && assignment_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // AT identifier_call (L_BRACKET argument_list R_BRACKET)?
  public static boolean annotation_call(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "annotation_call")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ANNOTATION_CALL, null);
    r = consumeToken(b, AT);
    p = r; // pin = 1
    r = r && report_error_(b, identifier_call(b, l + 1));
    r = p && annotation_call_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (L_BRACKET argument_list R_BRACKET)?
  private static boolean annotation_call_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "annotation_call_2")) return false;
    annotation_call_2_0(b, l + 1);
    return true;
  }

  // L_BRACKET argument_list R_BRACKET
  private static boolean annotation_call_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "annotation_call_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_BRACKET);
    r = r && argument_list(b, l + 1);
    r = r && consumeToken(b, R_BRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // argument_list_part (COMMA argument_list_part)*
  public static boolean argument_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_list")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = argument_list_part(b, l + 1);
    r = r && argument_list_1(b, l + 1);
    exit_section_(b, m, ARGUMENT_LIST, r);
    return r;
  }

  // (COMMA argument_list_part)*
  private static boolean argument_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_list_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument_list_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "argument_list_1", c)) break;
    }
    return true;
  }

  // COMMA argument_list_part
  private static boolean argument_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && argument_list_part(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER EQUALS expression
  public static boolean argument_list_part(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_list_part")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ARGUMENT_LIST_PART, null);
    r = consumeTokens(b, 2, IDENTIFIER, EQUALS);
    p = r; // pin = 2
    r = r && expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // EQUALS assignment_expression
  public static boolean assign_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assign_expression")) return false;
    if (!nextTokenIs(b, "<expression>", EQUALS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ASSIGN_EXPRESSION, "<expression>");
    r = consumeToken(b, EQUALS);
    p = r; // pin = 1
    r = r && assignment_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // assign_expression
  //                          | add_assign_expression
  //                          | sub_assign_expression
  //                          | mul_assign_expression
  //                          | div_assign_expression
  //                          | mod_assign_expression
  //                          | exp_assign_expression
  public static boolean assign_expression_part(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assign_expression_part")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ASSIGN_EXPRESSION_PART, "<assign expression part>");
    r = assign_expression(b, l + 1);
    if (!r) r = add_assign_expression(b, l + 1);
    if (!r) r = sub_assign_expression(b, l + 1);
    if (!r) r = mul_assign_expression(b, l + 1);
    if (!r) r = div_assign_expression(b, l + 1);
    if (!r) r = mod_assign_expression(b, l + 1);
    if (!r) r = exp_assign_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // binary_additive_expression assign_expression_part*
  public static boolean assignment_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ASSIGNMENT_EXPRESSION, "<expression>");
    r = binary_additive_expression(b, l + 1);
    r = r && assignment_expression_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // assign_expression_part*
  private static boolean assignment_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment_expression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!assign_expression_part(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "assignment_expression_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // PLUS binary_additive_expression
  public static boolean binary_add_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_add_expression")) return false;
    if (!nextTokenIs(b, "<expression>", PLUS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, BINARY_ADD_EXPRESSION, "<expression>");
    r = consumeToken(b, PLUS);
    p = r; // pin = 1
    r = r && binary_additive_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // binary_multiplicative_expression binary_additive_expression_part*
  public static boolean binary_additive_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_additive_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINARY_ADDITIVE_EXPRESSION, "<expression>");
    r = binary_multiplicative_expression(b, l + 1);
    r = r && binary_additive_expression_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // binary_additive_expression_part*
  private static boolean binary_additive_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_additive_expression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!binary_additive_expression_part(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "binary_additive_expression_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // binary_add_expression
  //                        | binary_sub_expression
  public static boolean binary_additive_expression_part(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_additive_expression_part")) return false;
    if (!nextTokenIs(b, "<binary additive expression part>", MINUS, PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINARY_ADDITIVE_EXPRESSION_PART, "<binary additive expression part>");
    r = binary_add_expression(b, l + 1);
    if (!r) r = binary_sub_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // SLASH binary_multiplicative_expression
  public static boolean binary_div_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_div_expression")) return false;
    if (!nextTokenIs(b, "<expression>", SLASH)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, BINARY_DIV_EXPRESSION, "<expression>");
    r = consumeToken(b, SLASH);
    p = r; // pin = 1
    r = r && binary_multiplicative_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // PERCENT binary_multiplicative_expression
  public static boolean binary_mod_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_mod_expression")) return false;
    if (!nextTokenIs(b, "<expression>", PERCENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, BINARY_MOD_EXPRESSION, "<expression>");
    r = consumeToken(b, PERCENT);
    p = r; // pin = 1
    r = r && binary_multiplicative_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ASTERISK binary_multiplicative_expression
  public static boolean binary_mul_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_mul_expression")) return false;
    if (!nextTokenIs(b, "<expression>", ASTERISK)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, BINARY_MUL_EXPRESSION, "<expression>");
    r = consumeToken(b, ASTERISK);
    p = r; // pin = 1
    r = r && binary_multiplicative_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // exponential_expression binary_multiplicative_expression_part*
  public static boolean binary_multiplicative_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_multiplicative_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINARY_MULTIPLICATIVE_EXPRESSION, "<expression>");
    r = exponential_expression(b, l + 1);
    r = r && binary_multiplicative_expression_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // binary_multiplicative_expression_part*
  private static boolean binary_multiplicative_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_multiplicative_expression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!binary_multiplicative_expression_part(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "binary_multiplicative_expression_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // binary_mul_expression
  //                              | binary_div_expression
  //                              | binary_mod_expression
  public static boolean binary_multiplicative_expression_part(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_multiplicative_expression_part")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINARY_MULTIPLICATIVE_EXPRESSION_PART, "<binary multiplicative expression part>");
    r = binary_mul_expression(b, l + 1);
    if (!r) r = binary_div_expression(b, l + 1);
    if (!r) r = binary_mod_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // MINUS binary_additive_expression
  public static boolean binary_sub_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_sub_expression")) return false;
    if (!nextTokenIs(b, "<expression>", MINUS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, BINARY_SUB_EXPRESSION, "<expression>");
    r = consumeToken(b, MINUS);
    p = r; // pin = 1
    r = r && binary_additive_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // brace_block | inlined_block
  public static boolean block_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "block_declaration")) return false;
    if (!nextTokenIs(b, "<block declaration>", EQUALS, L_BRACE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BLOCK_DECLARATION, "<block declaration>");
    r = brace_block(b, l + 1);
    if (!r) r = inlined_block(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier_call | variable_declaration | return_declaration
  static boolean block_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "block_statement")) return false;
    boolean r;
    r = identifier_call(b, l + 1);
    if (!r) r = variable_declaration(b, l + 1);
    if (!r) r = return_declaration(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // L_BRACE block_statement* R_BRACE
  public static boolean brace_block(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "brace_block")) return false;
    if (!nextTokenIs(b, L_BRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, BRACE_BLOCK, null);
    r = consumeToken(b, L_BRACE);
    p = r; // pin = 1
    r = r && report_error_(b, brace_block_1(b, l + 1));
    r = p && consumeToken(b, R_BRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // block_statement*
  private static boolean brace_block_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "brace_block_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!block_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "brace_block_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'f' | 'F' | 'd' | 'D' | 'i' | 'I' | 'c' | 'C'
  public static boolean cast_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cast_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CAST_EXPRESSION, "<expression>");
    r = consumeToken(b, "f");
    if (!r) r = consumeToken(b, "F");
    if (!r) r = consumeToken(b, "d");
    if (!r) r = consumeToken(b, "D");
    if (!r) r = consumeToken(b, "i");
    if (!r) r = consumeToken(b, "I");
    if (!r) r = consumeToken(b, "c");
    if (!r) r = consumeToken(b, "C");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // variable_declaration | function_declaration | import_declaration
  static boolean class_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_statement")) return false;
    boolean r;
    r = variable_declaration(b, l + 1);
    if (!r) r = function_declaration(b, l + 1);
    if (!r) r = import_declaration(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // L_BRACKET expression_list R_BRACKET
  public static boolean collection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collection")) return false;
    if (!nextTokenIs(b, L_BRACKET)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, COLLECTION, null);
    r = consumeToken(b, L_BRACKET);
    p = r; // pin = 1
    r = r && report_error_(b, expression_list(b, l + 1));
    r = p && consumeToken(b, R_BRACKET) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // line_comment
  public static boolean comment_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comment_declaration")) return false;
    if (!nextTokenIs(b, COMMENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = line_comment(b, l + 1);
    exit_section_(b, m, COMMENT_DECLARATION, r);
    return r;
  }

  /* ********************************************************** */
  // DIV_ASSIGN assignment_expression
  public static boolean div_assign_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "div_assign_expression")) return false;
    if (!nextTokenIs(b, "<expression>", DIV_ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DIV_ASSIGN_EXPRESSION, "<expression>");
    r = consumeToken(b, DIV_ASSIGN);
    p = r; // pin = 1
    r = r && assignment_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // EXP_ASSIGN assignment_expression
  public static boolean exp_assign_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exp_assign_expression")) return false;
    if (!nextTokenIs(b, "<expression>", EXP_ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, EXP_ASSIGN_EXPRESSION, "<expression>");
    r = consumeToken(b, EXP_ASSIGN);
    p = r; // pin = 1
    r = r && assignment_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // operable_expression exponential_expression_part*
  public static boolean exponential_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exponential_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPONENTIAL_EXPRESSION, "<expression>");
    r = operable_expression(b, l + 1);
    r = r && exponential_expression_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // exponential_expression_part*
  private static boolean exponential_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exponential_expression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!exponential_expression_part(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "exponential_expression_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // DOUBLE_ASTERISK exponential_expression
  public static boolean exponential_expression_part(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exponential_expression_part")) return false;
    if (!nextTokenIs(b, DOUBLE_ASTERISK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOUBLE_ASTERISK);
    r = r && exponential_expression(b, l + 1);
    exit_section_(b, m, EXPONENTIAL_EXPRESSION_PART, r);
    return r;
  }

  /* ********************************************************** */
  // assignment_expression
  public static boolean expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION, "<expression>");
    r = assignment_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expression (COMMA expression)*
  public static boolean expression_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_list")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION_LIST, "<expression list>");
    r = expression(b, l + 1);
    r = r && expression_list_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA expression)*
  private static boolean expression_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_list_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expression_list_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expression_list_1", c)) break;
    }
    return true;
  }

  // COMMA expression
  private static boolean expression_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // L_PARENTHESIS expression_list? R_PARENTHESIS
  public static boolean function_call_part(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_call_part")) return false;
    if (!nextTokenIs(b, L_PARENTHESIS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_CALL_PART, null);
    r = consumeToken(b, L_PARENTHESIS);
    p = r; // pin = 1
    r = r && report_error_(b, function_call_part_1(b, l + 1));
    r = p && consumeToken(b, R_PARENTHESIS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // expression_list?
  private static boolean function_call_part_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_call_part_1")) return false;
    expression_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // (annotation_call | comment_declaration)* fun IDENTIFIER LESS_THAN primitives? GREATER_THAN L_PARENTHESIS parameter_list? R_PARENTHESIS block_declaration
  public static boolean function_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_declaration")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_DECLARATION, "<function declaration>");
    r = function_declaration_0(b, l + 1);
    r = r && consumeTokens(b, 1, FUN, IDENTIFIER, LESS_THAN);
    p = r; // pin = 2
    r = r && report_error_(b, function_declaration_4(b, l + 1));
    r = p && report_error_(b, consumeTokens(b, -1, GREATER_THAN, L_PARENTHESIS)) && r;
    r = p && report_error_(b, function_declaration_7(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, R_PARENTHESIS)) && r;
    r = p && block_declaration(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (annotation_call | comment_declaration)*
  private static boolean function_declaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_declaration_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!function_declaration_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "function_declaration_0", c)) break;
    }
    return true;
  }

  // annotation_call | comment_declaration
  private static boolean function_declaration_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_declaration_0_0")) return false;
    boolean r;
    r = annotation_call(b, l + 1);
    if (!r) r = comment_declaration(b, l + 1);
    return r;
  }

  // primitives?
  private static boolean function_declaration_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_declaration_4")) return false;
    primitives(b, l + 1);
    return true;
  }

  // parameter_list?
  private static boolean function_declaration_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_declaration_7")) return false;
    parameter_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // (this DOT)? identifier_call_part (DOT identifier_call_part)*
  public static boolean identifier_call(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifier_call")) return false;
    if (!nextTokenIs(b, "<identifier call>", IDENTIFIER, THIS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IDENTIFIER_CALL, "<identifier call>");
    r = identifier_call_0(b, l + 1);
    r = r && identifier_call_part(b, l + 1);
    r = r && identifier_call_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (this DOT)?
  private static boolean identifier_call_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifier_call_0")) return false;
    identifier_call_0_0(b, l + 1);
    return true;
  }

  // this DOT
  private static boolean identifier_call_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifier_call_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, THIS, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DOT identifier_call_part)*
  private static boolean identifier_call_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifier_call_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!identifier_call_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "identifier_call_2", c)) break;
    }
    return true;
  }

  // DOT identifier_call_part
  private static boolean identifier_call_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifier_call_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && identifier_call_part(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER function_call_part?
  public static boolean identifier_call_part(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifier_call_part")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    r = r && identifier_call_part_1(b, l + 1);
    exit_section_(b, m, IDENTIFIER_CALL_PART, r);
    return r;
  }

  // function_call_part?
  private static boolean identifier_call_part_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifier_call_part_1")) return false;
    function_call_part(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // import STRING_LITERAL (as IDENTIFIER)
  public static boolean import_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "import_declaration")) return false;
    if (!nextTokenIs(b, IMPORT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, IMPORT_DECLARATION, null);
    r = consumeTokens(b, 1, IMPORT, STRING_LITERAL);
    p = r; // pin = 1
    r = r && import_declaration_2(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // as IDENTIFIER
  private static boolean import_declaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "import_declaration_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AS, IDENTIFIER);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // EQUALS expression
  public static boolean inlined_block(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inlined_block")) return false;
    if (!nextTokenIs(b, EQUALS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INLINED_BLOCK, null);
    r = consumeToken(b, EQUALS);
    p = r; // pin = 1
    r = r && expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // COMMENT
  public static boolean line_comment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "line_comment")) return false;
    if (!nextTokenIs(b, COMMENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMENT);
    exit_section_(b, m, LINE_COMMENT, r);
    return r;
  }

  /* ********************************************************** */
  // NUMBER_LITERAL | STRING_LITERAL
  public static boolean literals(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literals")) return false;
    if (!nextTokenIs(b, "<literals>", NUMBER_LITERAL, STRING_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERALS, "<literals>");
    r = consumeToken(b, NUMBER_LITERAL);
    if (!r) r = consumeToken(b, STRING_LITERAL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // MOD_ASSIGN assignment_expression
  public static boolean mod_assign_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mod_assign_expression")) return false;
    if (!nextTokenIs(b, "<expression>", MOD_ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MOD_ASSIGN_EXPRESSION, "<expression>");
    r = consumeToken(b, MOD_ASSIGN);
    p = r; // pin = 1
    r = r && assignment_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // MUL_ASSIGN assignment_expression
  public static boolean mul_assign_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mul_assign_expression")) return false;
    if (!nextTokenIs(b, "<expression>", MUL_ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MUL_ASSIGN_EXPRESSION, "<expression>");
    r = consumeToken(b, MUL_ASSIGN);
    p = r; // pin = 1
    r = r && assignment_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // collection | literals | identifier_call
  public static boolean operable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operable")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OPERABLE, "<operable>");
    r = collection(b, l + 1);
    if (!r) r = literals(b, l + 1);
    if (!r) r = identifier_call(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // prefix_expression? (parenthesized_expression | operable) postfix_expression? cast_expression?
  public static boolean operable_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operable_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OPERABLE_EXPRESSION, "<expression>");
    r = operable_expression_0(b, l + 1);
    r = r && operable_expression_1(b, l + 1);
    r = r && operable_expression_2(b, l + 1);
    r = r && operable_expression_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // prefix_expression?
  private static boolean operable_expression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operable_expression_0")) return false;
    prefix_expression(b, l + 1);
    return true;
  }

  // parenthesized_expression | operable
  private static boolean operable_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operable_expression_1")) return false;
    boolean r;
    r = parenthesized_expression(b, l + 1);
    if (!r) r = operable(b, l + 1);
    return r;
  }

  // postfix_expression?
  private static boolean operable_expression_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operable_expression_2")) return false;
    postfix_expression(b, l + 1);
    return true;
  }

  // cast_expression?
  private static boolean operable_expression_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operable_expression_3")) return false;
    cast_expression(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // parameter_list_part (COMMA parameter_list_part)*
  public static boolean parameter_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_list")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_list_part(b, l + 1);
    r = r && parameter_list_1(b, l + 1);
    exit_section_(b, m, PARAMETER_LIST, r);
    return r;
  }

  // (COMMA parameter_list_part)*
  private static boolean parameter_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_list_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!parameter_list_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "parameter_list_1", c)) break;
    }
    return true;
  }

  // COMMA parameter_list_part
  private static boolean parameter_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && parameter_list_part(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER COLON primitives
  public static boolean parameter_list_part(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_list_part")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PARAMETER_LIST_PART, null);
    r = consumeTokens(b, 2, IDENTIFIER, COLON);
    p = r; // pin = 2
    r = r && primitives(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // L_PARENTHESIS expression R_PARENTHESIS
  public static boolean parenthesized_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parenthesized_expression")) return false;
    if (!nextTokenIs(b, "<expression>", L_PARENTHESIS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PARENTHESIZED_EXPRESSION, "<expression>");
    r = consumeToken(b, L_PARENTHESIS);
    p = r; // pin = 1
    r = r && report_error_(b, expression(b, l + 1));
    r = p && consumeToken(b, R_PARENTHESIS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // DOUBLE_PLUS | DOUBLE_MINUS
  public static boolean postfix_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "postfix_expression")) return false;
    if (!nextTokenIs(b, "<expression>", DOUBLE_MINUS, DOUBLE_PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, POSTFIX_EXPRESSION, "<expression>");
    r = consumeToken(b, DOUBLE_PLUS);
    if (!r) r = consumeToken(b, DOUBLE_MINUS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DOUBLE_PLUS | DOUBLE_MINUS | PLUS | MINUS
  public static boolean prefix_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "prefix_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PREFIX_EXPRESSION, "<expression>");
    r = consumeToken(b, DOUBLE_PLUS);
    if (!r) r = consumeToken(b, DOUBLE_MINUS);
    if (!r) r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (int | boolean | short | byte | char | string | long) (L_BRACKET R_BRACKET)?
  public static boolean primitives(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitives")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PRIMITIVES, "<primitives>");
    r = primitives_0(b, l + 1);
    r = r && primitives_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // int | boolean | short | byte | char | string | long
  private static boolean primitives_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitives_0")) return false;
    boolean r;
    r = consumeToken(b, INT);
    if (!r) r = consumeToken(b, BOOLEAN);
    if (!r) r = consumeToken(b, SHORT);
    if (!r) r = consumeToken(b, BYTE);
    if (!r) r = consumeToken(b, CHAR);
    if (!r) r = consumeToken(b, STRING);
    if (!r) r = consumeToken(b, LONG);
    return r;
  }

  // (L_BRACKET R_BRACKET)?
  private static boolean primitives_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitives_1")) return false;
    primitives_1_0(b, l + 1);
    return true;
  }

  // L_BRACKET R_BRACKET
  private static boolean primitives_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitives_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, L_BRACKET, R_BRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // return expression?
  public static boolean return_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_declaration")) return false;
    if (!nextTokenIs(b, RETURN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, RETURN_DECLARATION, null);
    r = consumeToken(b, RETURN);
    p = r; // pin = 1
    r = r && return_declaration_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // expression?
  private static boolean return_declaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_declaration_1")) return false;
    expression(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // (comment_declaration | class_statement)*
  static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    while (true) {
      int c = current_position_(b);
      if (!root_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "root", c)) break;
    }
    return true;
  }

  // comment_declaration | class_statement
  private static boolean root_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_0")) return false;
    boolean r;
    r = comment_declaration(b, l + 1);
    if (!r) r = class_statement(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // SUB_ASSIGN assignment_expression
  public static boolean sub_assign_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sub_assign_expression")) return false;
    if (!nextTokenIs(b, "<expression>", SUB_ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SUB_ASSIGN_EXPRESSION, "<expression>");
    r = consumeToken(b, SUB_ASSIGN);
    p = r; // pin = 1
    r = r && assignment_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (annotation_call | comment_declaration)* var IDENTIFIER inlined_block
  public static boolean variable_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_declaration")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE_DECLARATION, "<variable declaration>");
    r = variable_declaration_0(b, l + 1);
    r = r && consumeTokens(b, 1, VAR, IDENTIFIER);
    p = r; // pin = 2
    r = r && inlined_block(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (annotation_call | comment_declaration)*
  private static boolean variable_declaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_declaration_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!variable_declaration_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "variable_declaration_0", c)) break;
    }
    return true;
  }

  // annotation_call | comment_declaration
  private static boolean variable_declaration_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_declaration_0_0")) return false;
    boolean r;
    r = annotation_call(b, l + 1);
    if (!r) r = comment_declaration(b, l + 1);
    return r;
  }

}
