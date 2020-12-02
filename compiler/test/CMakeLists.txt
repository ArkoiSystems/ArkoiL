include_directories(../src)
link_directories(../src)

add_subdirectory(lib/googletest)
include_directories(${gtest_SOURCE_DIR}/include ${gtest_SOURCE_DIR})

add_executable(arkoic_test
        tests/levenstein.cpp)
target_link_libraries(arkoic_test gtest gtest_main)