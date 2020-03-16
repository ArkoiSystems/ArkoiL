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
  static boolean add_assign_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "add_assign_expression")) return false;
    if (!nextTokenIs(b, "<expression>", ADD_ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null, "<expression>");
    r = consumeToken(b, ADD_ASSIGN);
    p = r; // pin = 1
    r = r && assignment_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // IDENTIFIER COLON primitives argument_list_part?
  static boolean argument_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_list")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeTokens(b, 2, IDENTIFIER, COLON);
    p = r; // pin = 2
    r = r && report_error_(b, primitives(b, l + 1));
    r = p && argument_list_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // argument_list_part?
  private static boolean argument_list_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_list_3")) return false;
    argument_list_part(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // COMMA argument_list
  static boolean argument_list_part(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_list_part")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && argument_list(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // binary_additive_expression (add_assign_expression
  //                                                     | sub_assign_expression
  //                                                     | mul_assign_expression
  //                                                     | div_assign_expression
  //                                                     | mod_assign_expression
  //                                                     | exp_assign_expression)?
  public static boolean assignment_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ASSIGNMENT_EXPRESSION, "<expression>");
    r = binary_additive_expression(b, l + 1);
    r = r && assignment_expression_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (add_assign_expression
  //                                                     | sub_assign_expression
  //                                                     | mul_assign_expression
  //                                                     | div_assign_expression
  //                                                     | mod_assign_expression
  //                                                     | exp_assign_expression)?
  private static boolean assignment_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment_expression_1")) return false;
    assignment_expression_1_0(b, l + 1);
    return true;
  }

  // add_assign_expression
  //                                                     | sub_assign_expression
  //                                                     | mul_assign_expression
  //                                                     | div_assign_expression
  //                                                     | mod_assign_expression
  //                                                     | exp_assign_expression
  private static boolean assignment_expression_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment_expression_1_0")) return false;
    boolean r;
    r = add_assign_expression(b, l + 1);
    if (!r) r = sub_assign_expression(b, l + 1);
    if (!r) r = mul_assign_expression(b, l + 1);
    if (!r) r = div_assign_expression(b, l + 1);
    if (!r) r = mod_assign_expression(b, l + 1);
    if (!r) r = exp_assign_expression(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // PLUS binary_additive_expression
  static boolean binary_add_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_add_expression")) return false;
    if (!nextTokenIs(b, "<expression>", PLUS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null, "<expression>");
    r = consumeToken(b, PLUS);
    p = r; // pin = 1
    r = r && binary_additive_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // binary_multiplicative_expression (binary_add_expression
  //                                                                | binary_sub_expression)?
  public static boolean binary_additive_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_additive_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINARY_ADDITIVE_EXPRESSION, "<expression>");
    r = binary_multiplicative_expression(b, l + 1);
    r = r && binary_additive_expression_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (binary_add_expression
  //                                                                | binary_sub_expression)?
  private static boolean binary_additive_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_additive_expression_1")) return false;
    binary_additive_expression_1_0(b, l + 1);
    return true;
  }

  // binary_add_expression
  //                                                                | binary_sub_expression
  private static boolean binary_additive_expression_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_additive_expression_1_0")) return false;
    boolean r;
    r = binary_add_expression(b, l + 1);
    if (!r) r = binary_sub_expression(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // SLASH binary_multiplicative_expression
  static boolean binary_div_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_div_expression")) return false;
    if (!nextTokenIs(b, "<expression>", SLASH)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null, "<expression>");
    r = consumeToken(b, SLASH);
    p = r; // pin = 1
    r = r && binary_multiplicative_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // DOUBLE_ASTERISK binary_multiplicative_expression
  static boolean binary_exp_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_exp_expression")) return false;
    if (!nextTokenIs(b, "<expression>", DOUBLE_ASTERISK)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null, "<expression>");
    r = consumeToken(b, DOUBLE_ASTERISK);
    p = r; // pin = 1
    r = r && binary_multiplicative_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // PERCENT binary_multiplicative_expression
  static boolean binary_mod_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_mod_expression")) return false;
    if (!nextTokenIs(b, "<expression>", PERCENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null, "<expression>");
    r = consumeToken(b, PERCENT);
    p = r; // pin = 1
    r = r && binary_multiplicative_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ASTERISK binary_multiplicative_expression
  static boolean binary_mul_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_mul_expression")) return false;
    if (!nextTokenIs(b, "<expression>", ASTERISK)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null, "<expression>");
    r = consumeToken(b, ASTERISK);
    p = r; // pin = 1
    r = r && binary_multiplicative_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // collection_expression (binary_mul_expression
  //                                                         | binary_div_expression
  //                                                         | binary_mod_expression
  //                                                         | binary_exp_expression)?
  public static boolean binary_multiplicative_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_multiplicative_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINARY_MULTIPLICATIVE_EXPRESSION, "<expression>");
    r = collection_expression(b, l + 1);
    r = r && binary_multiplicative_expression_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (binary_mul_expression
  //                                                         | binary_div_expression
  //                                                         | binary_mod_expression
  //                                                         | binary_exp_expression)?
  private static boolean binary_multiplicative_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_multiplicative_expression_1")) return false;
    binary_multiplicative_expression_1_0(b, l + 1);
    return true;
  }

  // binary_mul_expression
  //                                                         | binary_div_expression
  //                                                         | binary_mod_expression
  //                                                         | binary_exp_expression
  private static boolean binary_multiplicative_expression_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_multiplicative_expression_1_0")) return false;
    boolean r;
    r = binary_mul_expression(b, l + 1);
    if (!r) r = binary_div_expression(b, l + 1);
    if (!r) r = binary_mod_expression(b, l + 1);
    if (!r) r = binary_exp_expression(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // MINUS binary_additive_expression
  static boolean binary_sub_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binary_sub_expression")) return false;
    if (!nextTokenIs(b, "<expression>", MINUS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null, "<expression>");
    r = consumeToken(b, MINUS);
    p = r; // pin = 1
    r = r && binary_additive_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // brace_block | inlined_block
  static boolean block_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "block_declaration")) return false;
    if (!nextTokenIs(b, "", EQUALS, L_BRACE)) return false;
    boolean r;
    r = brace_block(b, l + 1);
    if (!r) r = inlined_block(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // (function_invoke SEMICOLON) | (variable_invoke SEMICOLON) | variable_declaration | return_declaration
  static boolean block_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "block_statement")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = block_statement_0(b, l + 1);
    if (!r) r = block_statement_1(b, l + 1);
    if (!r) r = variable_declaration(b, l + 1);
    if (!r) r = return_declaration(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // function_invoke SEMICOLON
  private static boolean block_statement_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "block_statement_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = function_invoke(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // variable_invoke SEMICOLON
  private static boolean block_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "block_statement_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = variable_invoke(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
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
  // collection_expression_part | operable_expression
  public static boolean collection_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collection_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COLLECTION_EXPRESSION, "<expression>");
    r = collection_expression_part(b, l + 1);
    if (!r) r = operable_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // L_BRACKET expression_list R_BRACKET
  static boolean collection_expression_part(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collection_expression_part")) return false;
    if (!nextTokenIs(b, L_BRACKET)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, L_BRACKET);
    p = r; // pin = 1
    r = r && report_error_(b, expression_list(b, l + 1));
    r = p && consumeToken(b, R_BRACKET) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // DIV_ASSIGN assignment_expression
  static boolean div_assign_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "div_assign_expression")) return false;
    if (!nextTokenIs(b, "<expression>", DIV_ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null, "<expression>");
    r = consumeToken(b, DIV_ASSIGN);
    p = r; // pin = 1
    r = r && assignment_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // EXP_ASSIGN assignment_expression
  static boolean exp_assign_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exp_assign_expression")) return false;
    if (!nextTokenIs(b, "<expression>", EXP_ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null, "<expression>");
    r = consumeToken(b, EXP_ASSIGN);
    p = r; // pin = 1
    r = r && assignment_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // expression expression_list_part?
  static boolean expression_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_list")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expression(b, l + 1);
    r = r && expression_list_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expression_list_part?
  private static boolean expression_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_list_1")) return false;
    expression_list_part(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // COMMA expression_list
  static boolean expression_list_part(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_list_part")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && expression_list(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // fun IDENTIFIER LESS_THAN primitives? GREATER_THAN L_PARENTHESIS argument_list? R_PARENTHESIS block_declaration
  public static boolean function_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_declaration")) return false;
    if (!nextTokenIs(b, FUN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_DECLARATION, null);
    r = consumeTokens(b, 1, FUN, IDENTIFIER, LESS_THAN);
    p = r; // pin = 1
    r = r && report_error_(b, function_declaration_3(b, l + 1));
    r = p && report_error_(b, consumeTokens(b, -1, GREATER_THAN, L_PARENTHESIS)) && r;
    r = p && report_error_(b, function_declaration_6(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, R_PARENTHESIS)) && r;
    r = p && block_declaration(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // primitives?
  private static boolean function_declaration_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_declaration_3")) return false;
    primitives(b, l + 1);
    return true;
  }

  // argument_list?
  private static boolean function_declaration_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_declaration_6")) return false;
    argument_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // (this DOT)? IDENTIFIER L_PARENTHESIS expression_list? R_PARENTHESIS function_invoke?
  public static boolean function_invoke(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_invoke")) return false;
    if (!nextTokenIs(b, "<function invoke>", IDENTIFIER, THIS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_INVOKE, "<function invoke>");
    r = function_invoke_0(b, l + 1);
    r = r && consumeTokens(b, 2, IDENTIFIER, L_PARENTHESIS);
    p = r; // pin = 3
    r = r && report_error_(b, function_invoke_3(b, l + 1));
    r = p && report_error_(b, consumeToken(b, R_PARENTHESIS)) && r;
    r = p && function_invoke_5(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (this DOT)?
  private static boolean function_invoke_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_invoke_0")) return false;
    function_invoke_0_0(b, l + 1);
    return true;
  }

  // this DOT
  private static boolean function_invoke_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_invoke_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, THIS, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  // expression_list?
  private static boolean function_invoke_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_invoke_3")) return false;
    expression_list(b, l + 1);
    return true;
  }

  // function_invoke?
  private static boolean function_invoke_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_invoke_5")) return false;
    function_invoke(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // import STRING_LITERAL (as IDENTIFIER) SEMICOLON
  public static boolean import_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "import_declaration")) return false;
    if (!nextTokenIs(b, IMPORT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, IMPORT_DECLARATION, null);
    r = consumeTokens(b, 1, IMPORT, STRING_LITERAL);
    p = r; // pin = 1
    r = r && report_error_(b, import_declaration_2(b, l + 1));
    r = p && consumeToken(b, SEMICOLON) && r;
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
  // EQUALS expression SEMICOLON
  public static boolean inlined_block(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inlined_block")) return false;
    if (!nextTokenIs(b, EQUALS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INLINED_BLOCK, null);
    r = consumeToken(b, EQUALS);
    p = r; // pin = 1
    r = r && report_error_(b, expression(b, l + 1));
    r = p && consumeToken(b, SEMICOLON) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  static boolean mod_assign_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mod_assign_expression")) return false;
    if (!nextTokenIs(b, "<expression>", MOD_ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null, "<expression>");
    r = consumeToken(b, MOD_ASSIGN);
    p = r; // pin = 1
    r = r && assignment_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // MUL_ASSIGN assignment_expression
  static boolean mul_assign_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mul_assign_expression")) return false;
    if (!nextTokenIs(b, "<expression>", MUL_ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null, "<expression>");
    r = consumeToken(b, MUL_ASSIGN);
    p = r; // pin = 1
    r = r && assignment_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // literals | function_invoke | variable_invoke
  public static boolean operable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operable")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OPERABLE, "<operable>");
    r = literals(b, l + 1);
    if (!r) r = function_invoke(b, l + 1);
    if (!r) r = variable_invoke(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // prefix_expression? (parenthesized_expression_part | operable) postfix_expression? cast_expression?
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

  // parenthesized_expression_part | operable
  private static boolean operable_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operable_expression_1")) return false;
    boolean r;
    r = parenthesized_expression_part(b, l + 1);
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
  // L_PARENTHESIS expression_list R_PARENTHESIS
  static boolean parenthesized_expression_part(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parenthesized_expression_part")) return false;
    if (!nextTokenIs(b, L_PARENTHESIS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, L_PARENTHESIS);
    p = r; // pin = 1
    r = r && report_error_(b, expression_list(b, l + 1));
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
  // return expression? SEMICOLON
  public static boolean return_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_declaration")) return false;
    if (!nextTokenIs(b, RETURN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, RETURN_DECLARATION, null);
    r = consumeToken(b, RETURN);
    p = r; // pin = 1
    r = r && report_error_(b, return_declaration_1(b, l + 1));
    r = p && consumeToken(b, SEMICOLON) && r;
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
  // (COMMENT | root_statement)*
  static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    while (true) {
      int c = current_position_(b);
      if (!root_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "root", c)) break;
    }
    return true;
  }

  // COMMENT | root_statement
  private static boolean root_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_0")) return false;
    boolean r;
    r = consumeToken(b, COMMENT);
    if (!r) r = root_statement(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // variable_declaration | function_declaration | import_declaration
  static boolean root_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_statement")) return false;
    boolean r;
    r = variable_declaration(b, l + 1);
    if (!r) r = function_declaration(b, l + 1);
    if (!r) r = import_declaration(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // SUB_ASSIGN assignment_expression
  static boolean sub_assign_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sub_assign_expression")) return false;
    if (!nextTokenIs(b, "<expression>", SUB_ASSIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null, "<expression>");
    r = consumeToken(b, SUB_ASSIGN);
    p = r; // pin = 1
    r = r && assignment_expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // var IDENTIFIER EQUALS expression SEMICOLON
  public static boolean variable_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_declaration")) return false;
    if (!nextTokenIs(b, VAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE_DECLARATION, null);
    r = consumeTokens(b, 1, VAR, IDENTIFIER, EQUALS);
    p = r; // pin = 1
    r = r && report_error_(b, expression(b, l + 1));
    r = p && consumeToken(b, SEMICOLON) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (this DOT)? IDENTIFIER variable_invoke?
  public static boolean variable_invoke(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_invoke")) return false;
    if (!nextTokenIs(b, "<variable invoke>", IDENTIFIER, THIS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE_INVOKE, "<variable invoke>");
    r = variable_invoke_0(b, l + 1);
    r = r && consumeToken(b, IDENTIFIER);
    r = r && variable_invoke_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (this DOT)?
  private static boolean variable_invoke_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_invoke_0")) return false;
    variable_invoke_0_0(b, l + 1);
    return true;
  }

  // this DOT
  private static boolean variable_invoke_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_invoke_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, THIS, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  // variable_invoke?
  private static boolean variable_invoke_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_invoke_2")) return false;
    variable_invoke(b, l + 1);
    return true;
  }

}
