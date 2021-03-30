set(CMAKE_MODULE_PATH ../cmake ${CMAKE_MODULE_PATH})

find_package(fmt 7.0.3 REQUIRED)

add_subdirectory(lib/cxxopts-2.2.1)
include_directories(lib/cxxopts-2.2.1/include)

find_package(LLVM REQUIRED)
include_directories(${LLVM_INCLUDE_DIRS})

find_package(LLD REQUIRED)
include_directories(${LLD_INCLUDE_DIRS})

set(SOURCE_FILES
        parser/astnodes.cpp
        ../include/parser/astnodes.h
        lexer/lexer.cpp
        ../include/lexer/lexer.h
        compiler/compiler.cpp
        ../include/compiler/compiler.h
        parser/parser.cpp
        ../include/parser/parser.h
        lexer/token.cpp
        ../include/lexer/token.h
        compiler/error.cpp
        ../include/compiler/error.h
        semantic/typeresolver.cpp
        ../include/semantic/typeresolver.h
        parser/symboltable.cpp
        ../include/parser/symboltable.h
        semantic/typecheck.cpp
        ../include/semantic/typecheck.h
        semantic/scopecheck.cpp
        ../include/semantic/scopecheck.h
        codegen/codegen.cpp
        ../include/codegen/codegen.h
        semantic/inliner.cpp
        ../include/semantic/inliner.h
        utils/astprinter.cpp
        ../include/utils/astprinter.h
        ../include/compiler/options.h
        ../include/parser/allnodes.h
        ../include/utils/utils.h)

# Library stuff here

add_library(arkoic_lib STATIC
        ${SOURCE_FILES})

set_target_properties(arkoic_lib
        PROPERTIES
        VERSION ${PROJECT_VERSION}
        OUTPUT_NAME "arkoic")
target_link_libraries(arkoic_lib LINK_PUBLIC
        fmt::fmt
        ${LLVM_LIBRARIES}
        ${LLD_LIBRARIES})

include(GNUInstallDirs)
install(TARGETS arkoic_lib
        LIBRARY
        DESTINATION ${CMAKE_INSTALL_LIBDIR})
install(DIRECTORY ${CMAKE_SOURCE_DIR}/include/
        DESTINATION "${CMAKE_INSTALL_INCLUDEDIR}/arkoic")

# Executable stuff here

add_executable(arkoic_exe
        main.cpp
        ${SOURCE_FILES} ../include/utils/ansi.h)

set_target_properties(arkoic_exe
        PROPERTIES
        VERSION ${PROJECT_VERSION}
        OUTPUT_NAME "arkoic")
target_link_libraries(arkoic_exe LINK_PUBLIC
        fmt::fmt
        cxxopts
        ${LLVM_LIBRARIES}
        ${LLD_LIBRARIES})

install(TARGETS arkoic_exe
        RUNTIME
        DESTINATION ${CMAKE_INSTALL_BINDIR})