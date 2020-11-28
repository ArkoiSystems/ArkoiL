#include_directories(../src)
#link_directories(../src)
#
#add_executable(arkoic_test lexer.cpp)
#target_link_libraries(arkoic_test arkoic)
#
#add_test(NAME tests COMMAND arkoic_test)

#set(UNIT_TEST arkoic_test)
#add_test(NAME ${UNIT_TEST} COMMAND ${UNIT_TEST})
#add_custom_command(
#        TARGET ${UNIT_TEST}
#        COMMENT "Run tests"
#        POST_BUILD
#        WORKING_DIRECTORY ${CMAKE_BINARY_DIR}
#        COMMAND ${CMAKE_CTEST_COMMAND} -C $<CONFIGURATION> -R "^${UNIT_TEST}$" --output-on-failures
#)