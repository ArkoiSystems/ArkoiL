cmake_minimum_required(VERSION 3.16)
project(ArkoiCompiler)

set(CMAKE_CXX_STANDARD 20)

add_subdirectory(deps/fmt-7.0.2)
add_subdirectory(deps/dbg-macro)

add_executable(ArkoiCompiler
        src/main.cpp
        src/lexer/lexer.cpp
        src/lexer/lexer.h
        src/compiler/options.h
        src/compiler/compiler.cpp
        src/compiler/compiler.h
        src/compiler/utils.h
        src/parser/parser.cpp
        src/parser/parser.h
        src/lexer/token.cpp
        src/lexer/token.h
        src/parser/astnodes.h
        src/compiler/error.cpp
        src/compiler/error.h
        src/parser/typeresolver.cpp
        src/parser/typeresolver.h
        src/parser/symboltable.cpp
        src/parser/symboltable.h)

target_link_libraries(ArkoiCompiler PRIVATE fmt::fmt)