cmake_minimum_required(VERSION 3.17.3)

project(ArkoiCompiler)

set(CMAKE_CXX_STANDARD 14)
#set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(ARKOI_VERSION_MAJOR 0)
set(ARKOI_VERSION_MINOR 1)
set(ARKOI_VERSION_PATCH 0)
set(ARKOI_VERSION "${ARKOI_VERSION_MAJOR}.${ARKOI_VERSION_MINOR}.${ARKOI_VERSION_PATCH}")

set(lld_DIR "${CMAKE_SOURCE_DIR}/cmake/")

find_package(LLVM REQUIRED CONFIG)
find_package(fmt REQUIRED)

include_directories(${LLVM_INCLUDE_DIRS})

add_executable(ArkoiCompiler
        "${PROJECT_SOURCE_DIR}/src/main.cpp"
        "${PROJECT_SOURCE_DIR}/src/lexer/lexer.cpp"
        "${PROJECT_SOURCE_DIR}/src/lexer/lexer.h"
        "${PROJECT_SOURCE_DIR}/src/compiler/options.h"
        "${PROJECT_SOURCE_DIR}/src/compiler/compiler.cpp"
        "${PROJECT_SOURCE_DIR}/src/compiler/compiler.h"
        "${PROJECT_SOURCE_DIR}/src/utils.h"
        "${PROJECT_SOURCE_DIR}/src/parser/parser.cpp"
        "${PROJECT_SOURCE_DIR}/src/parser/parser.h"
        "${PROJECT_SOURCE_DIR}/src/lexer/token.cpp"
        "${PROJECT_SOURCE_DIR}/src/lexer/token.h"
        "${PROJECT_SOURCE_DIR}/src/parser/astnodes.h"
        "${PROJECT_SOURCE_DIR}/src/compiler/error.cpp"
        "${PROJECT_SOURCE_DIR}/src/compiler/error.h"
        "${PROJECT_SOURCE_DIR}/src/parser/typeresolver.cpp"
        "${PROJECT_SOURCE_DIR}/src/parser/typeresolver.h"
        "${PROJECT_SOURCE_DIR}/src/parser/symboltable.cpp"
        "${PROJECT_SOURCE_DIR}/src/parser/symboltable.h"
        "${PROJECT_SOURCE_DIR}/src/semantic/typecheck.cpp"
        "${PROJECT_SOURCE_DIR}/src/semantic/typecheck.h"
        "${PROJECT_SOURCE_DIR}/src/semantic/scopecheck.cpp"
        "${PROJECT_SOURCE_DIR}/src/semantic/scopecheck.h"
        "${PROJECT_SOURCE_DIR}/src/codegen/codegen.cpp"
        "${PROJECT_SOURCE_DIR}/src/codegen/codegen.h"
        "${PROJECT_SOURCE_DIR}/src/parser/astnodes.cpp")

llvm_map_components_to_libnames(llvm_libs core analysis ipo)

target_link_libraries(ArkoiCompiler PRIVATE fmt::fmt ${llvm_libs})