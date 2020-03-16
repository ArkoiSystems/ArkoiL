package com.arkoisystems.lang.arkoi.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.arkoisystems.lang.arkoi.ArkoiTokenTypes.*;

%%

%{
  public _ArkoiLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _ArkoiLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

WHITE_SPACE=[ \n\r\t\f]
NUMBER_LITERAL=[0-9]+(\.[0-9]*)?
STRING_LITERAL=\"([^\"\\]|\\.)*\"
IDENTIFIER=[a-zA-Z_@$][a-zA-Z0-9_@$]*
COMMENT=#.*

%%
<YYINITIAL> {
  {WHITE_SPACE}         { return WHITE_SPACE; }

  "["                   { return L_BRACKET; }
  "]"                   { return R_BRACKET; }
  "("                   { return L_PARENTHESIS; }
  ")"                   { return R_PARENTHESIS; }
  "{"                   { return L_BRACE; }
  "}"                   { return R_BRACE; }
  "<"                   { return LESS_THAN; }
  ">"                   { return GREATER_THAN; }
  "="                   { return EQUALS; }
  ";"                   { return SEMICOLON; }
  "."                   { return DOT; }
  ","                   { return COMMA; }
  ":"                   { return COLON; }
  "+="                  { return ADD_ASSIGN; }
  "-="                  { return SUB_ASSIGN; }
  "*="                  { return MUL_ASSIGN; }
  "/="                  { return DIV_ASSIGN; }
  "%="                  { return MOD_ASSIGN; }
  "**="                 { return EXP_ASSIGN; }
  "++"                  { return DOUBLE_PLUS; }
  "--"                  { return DOUBLE_MINUS; }
  "**"                  { return DOUBLE_ASTERISK; }
  "+"                   { return PLUS; }
  "-"                   { return MINUS; }
  "*"                   { return ASTERISK; }
  "/"                   { return SLASH; }
  "%"                   { return PERCENT; }
  "import"              { return IMPORT; }
  "as"                  { return AS; }
  "var"                 { return VAR; }
  "fun"                 { return FUN; }
  "return"              { return RETURN; }
  "this"                { return THIS; }
  "int"                 { return INT; }
  "boolean"             { return BOOLEAN; }
  "short"               { return SHORT; }
  "byte"                { return BYTE; }
  "char"                { return CHAR; }
  "string"              { return STRING; }
  "long"                { return LONG; }

  {WHITE_SPACE}         { return WHITE_SPACE; }
  {NUMBER_LITERAL}      { return NUMBER_LITERAL; }
  {STRING_LITERAL}      { return STRING_LITERAL; }
  {IDENTIFIER}          { return IDENTIFIER; }
  {COMMENT}             { return COMMENT; }

}

[^] { return BAD_CHARACTER; }
