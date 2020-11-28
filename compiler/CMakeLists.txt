cmake_minimum_required(VERSION 3.10.2)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(ARKOI_VERSION_MAJOR 0)
set(ARKOI_VERSION_MINOR 1)
set(ARKOI_VERSION_PATCH 0)
set(ARKOI_VERSION ${ARKOI_VERSION_MAJOR}.${ARKOI_VERSION_MINOR}.${ARKOI_VERSION_PATCH})

set(ARES_DESCRIPTION "The official compiler for the Arkoi Language. This project is not serious and so it won't be updated as often.")

project(arkoic VERSION ${ARKOI_VERSION} DESCRIPTION ${ARES_DESCRIPTION})
add_subdirectory(src)

enable_testing()
add_subdirectory(tests)