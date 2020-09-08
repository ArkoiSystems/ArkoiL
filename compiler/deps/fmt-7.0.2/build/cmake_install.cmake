# Install script for directory: /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/deps/fmt-7.0.2

# Set the install prefix
if(NOT DEFINED CMAKE_INSTALL_PREFIX)
  set(CMAKE_INSTALL_PREFIX "/usr/local")
endif()
string(REGEX REPLACE "/$" "" CMAKE_INSTALL_PREFIX "${CMAKE_INSTALL_PREFIX}")

# Set the install configuration name.
if(NOT DEFINED CMAKE_INSTALL_CONFIG_NAME)
  if(BUILD_TYPE)
    string(REGEX REPLACE "^[^A-Za-z0-9_]+" ""
           CMAKE_INSTALL_CONFIG_NAME "${BUILD_TYPE}")
  else()
    set(CMAKE_INSTALL_CONFIG_NAME "Release")
  endif()
  message(STATUS "Install configuration: \"${CMAKE_INSTALL_CONFIG_NAME}\"")
endif()

# Set the component getting installed.
if(NOT CMAKE_INSTALL_COMPONENT)
  if(COMPONENT)
    message(STATUS "Install component: \"${COMPONENT}\"")
    set(CMAKE_INSTALL_COMPONENT "${COMPONENT}")
  else()
    set(CMAKE_INSTALL_COMPONENT)
  endif()
endif()

# Install shared libraries without execute permission?
if(NOT DEFINED CMAKE_INSTALL_SO_NO_EXE)
  set(CMAKE_INSTALL_SO_NO_EXE "1")
endif()

# Is this installation the result of a crosscompile?
if(NOT DEFINED CMAKE_CROSSCOMPILING)
  set(CMAKE_CROSSCOMPILING "FALSE")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/cmake/fmt" TYPE FILE FILES
    "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/fmt-config.cmake"
    "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/fmt-config-version.cmake"
    )
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/cmake/fmt/fmt-targets.cmake")
    file(DIFFERENT EXPORT_FILE_CHANGED FILES
         "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/cmake/fmt/fmt-targets.cmake"
         "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/CMakeFiles/Export/lib/cmake/fmt/fmt-targets.cmake")
    if(EXPORT_FILE_CHANGED)
      file(GLOB OLD_CONFIG_FILES "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/cmake/fmt/fmt-targets-*.cmake")
      if(OLD_CONFIG_FILES)
        message(STATUS "Old export file \"$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/cmake/fmt/fmt-targets.cmake\" will be replaced.  Removing files [${OLD_CONFIG_FILES}].")
        file(REMOVE ${OLD_CONFIG_FILES})
      endif()
    endif()
  endif()
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/cmake/fmt" TYPE FILE FILES "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/CMakeFiles/Export/lib/cmake/fmt/fmt-targets.cmake")
  if("${CMAKE_INSTALL_CONFIG_NAME}" MATCHES "^([Rr][Ee][Ll][Ee][Aa][Ss][Ee])$")
    file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/cmake/fmt" TYPE FILE FILES "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/CMakeFiles/Export/lib/cmake/fmt/fmt-targets-release.cmake")
  endif()
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib" TYPE STATIC_LIBRARY FILES "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/libfmt.a")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib" TYPE FILE OPTIONAL FILES
    "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/$<TARGET_PDB_FILE:fmt"
    "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/fmt-header-only>"
    )
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/include/fmt" TYPE FILE FILES
    "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/include/fmt/chrono.h"
    "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/include/fmt/color.h"
    "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/include/fmt/compile.h"
    "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/include/fmt/core.h"
    "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/include/fmt/format.h"
    "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/include/fmt/format-inl.h"
    "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/include/fmt/locale.h"
    "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/include/fmt/os.h"
    "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/include/fmt/ostream.h"
    "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/include/fmt/posix.h"
    "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/include/fmt/printf.h"
    "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/include/fmt/ranges.h"
    )
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/pkgconfig" TYPE FILE FILES "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/fmt.pc")
endif()

if(NOT CMAKE_INSTALL_LOCAL_ONLY)
  # Include the install script for each subdirectory.
  include("/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/doc/cmake_install.cmake")
  include("/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/test/cmake_install.cmake")

endif()

if(CMAKE_INSTALL_COMPONENT)
  set(CMAKE_INSTALL_MANIFEST "install_manifest_${CMAKE_INSTALL_COMPONENT}.txt")
else()
  set(CMAKE_INSTALL_MANIFEST "install_manifest.txt")
endif()

string(REPLACE ";" "\n" CMAKE_INSTALL_MANIFEST_CONTENT
       "${CMAKE_INSTALL_MANIFEST_FILES}")
file(WRITE "/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/${CMAKE_INSTALL_MANIFEST}"
     "${CMAKE_INSTALL_MANIFEST_CONTENT}")
