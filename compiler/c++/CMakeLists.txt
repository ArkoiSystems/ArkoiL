cmake_minimum_required(VERSION 3.16)
project(ArkoiCompiler)

set(CMAKE_CXX_STANDARD 20)

add_executable(ArkoiCompiler main.cpp
        lexer/lexer.cpp
        lexer/lexer.h
        compiler/options.h
        compiler/compiler.cpp
        compiler/compiler.h
        compiler/utils.h
        parser/parser.cpp
        parser/parser.h
        lexer/token.cpp
        lexer/token.h parser/astnode.h)