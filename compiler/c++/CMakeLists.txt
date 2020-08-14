cmake_minimum_required(VERSION 3.16)

project(ArkoiCompiler)
set(CMAKE_CXX_STANDARD 20)

set(ARKOI_VERSION_MAJOR 0)
set(ARKOI_VERSION_MINOR 1)
set(ARKOI_VERSION_PATCH 0)
set(ARKOI_VERSION "${ARKOI_VERSION_MAJOR}.${ARKOI_VERSION_MINOR}.${ARKOI_VERSION_PATCH}")

find_package(LLVM REQUIRED CONFIG)

add_subdirectory("${CMAKE_SOURCE_DIR}/deps/fmt-7.0.2")
add_subdirectory("${CMAKE_SOURCE_DIR}/deps/dbg-macro")

include_directories(${LLVM_INCLUDE_DIRS})
add_definitions(${LLVM_DEFINITIONS})

add_executable(ArkoiCompiler
        "${CMAKE_SOURCE_DIR}/src/main.cpp"
        "${CMAKE_SOURCE_DIR}/src/lexer/lexer.cpp"
        "${CMAKE_SOURCE_DIR}/src/lexer/lexer.h"
        "${CMAKE_SOURCE_DIR}/src/compiler/options.h"
        "${CMAKE_SOURCE_DIR}/src/compiler/compiler.cpp"
        "${CMAKE_SOURCE_DIR}/src/compiler/compiler.h"
        "${CMAKE_SOURCE_DIR}/src/utils.h"
        "${CMAKE_SOURCE_DIR}/src/parser/parser.cpp"
        "${CMAKE_SOURCE_DIR}/src/parser/parser.h"
        "${CMAKE_SOURCE_DIR}/src/lexer/token.cpp"
        "${CMAKE_SOURCE_DIR}/src/lexer/token.h"
        "${CMAKE_SOURCE_DIR}/src/parser/astnodes.h"
        "${CMAKE_SOURCE_DIR}/src/compiler/error.cpp"
        "${CMAKE_SOURCE_DIR}/src/compiler/error.h"
        "${CMAKE_SOURCE_DIR}/src/parser/typeresolver.cpp"
        "${CMAKE_SOURCE_DIR}/src/parser/typeresolver.h"
        "${CMAKE_SOURCE_DIR}/src/parser/symboltable.cpp"
        "${CMAKE_SOURCE_DIR}/src/parser/symboltable.h"
        "${CMAKE_SOURCE_DIR}/src/semantic/typecheck.cpp"
        "${CMAKE_SOURCE_DIR}/src/semantic/typecheck.h"
        "${CMAKE_SOURCE_DIR}/src/semantic/scopecheck.cpp"
        "${CMAKE_SOURCE_DIR}/src/semantic/scopecheck.h" src/codegen/codegen.cpp src/codegen/codegen.h)

llvm_map_components_to_libnames(llvm_libs core analysis)

target_link_libraries(ArkoiCompiler PRIVATE fmt::fmt ${llvm_libs})