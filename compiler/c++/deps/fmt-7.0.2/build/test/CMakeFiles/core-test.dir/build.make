# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.10

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/bin/cmake

# The command to remove a file.
RM = /usr/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build

# Include any dependencies generated for this target.
include test/CMakeFiles/core-test.dir/depend.make

# Include the progress variables for this target.
include test/CMakeFiles/core-test.dir/progress.make

# Include the compile flags for this target's objects.
include test/CMakeFiles/core-test.dir/flags.make

test/CMakeFiles/core-test.dir/core-test.cc.o: test/CMakeFiles/core-test.dir/flags.make
test/CMakeFiles/core-test.dir/core-test.cc.o: ../test/core-test.cc
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object test/CMakeFiles/core-test.dir/core-test.cc.o"
	cd /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/test && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/core-test.dir/core-test.cc.o -c /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/test/core-test.cc

test/CMakeFiles/core-test.dir/core-test.cc.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/core-test.dir/core-test.cc.i"
	cd /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/test && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/test/core-test.cc > CMakeFiles/core-test.dir/core-test.cc.i

test/CMakeFiles/core-test.dir/core-test.cc.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/core-test.dir/core-test.cc.s"
	cd /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/test && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/test/core-test.cc -o CMakeFiles/core-test.dir/core-test.cc.s

test/CMakeFiles/core-test.dir/core-test.cc.o.requires:

.PHONY : test/CMakeFiles/core-test.dir/core-test.cc.o.requires

test/CMakeFiles/core-test.dir/core-test.cc.o.provides: test/CMakeFiles/core-test.dir/core-test.cc.o.requires
	$(MAKE) -f test/CMakeFiles/core-test.dir/build.make test/CMakeFiles/core-test.dir/core-test.cc.o.provides.build
.PHONY : test/CMakeFiles/core-test.dir/core-test.cc.o.provides

test/CMakeFiles/core-test.dir/core-test.cc.o.provides.build: test/CMakeFiles/core-test.dir/core-test.cc.o


# Object files for target core-test
core__test_OBJECTS = \
"CMakeFiles/core-test.dir/core-test.cc.o"

# External object files for target core-test
core__test_EXTERNAL_OBJECTS =

bin/core-test: test/CMakeFiles/core-test.dir/core-test.cc.o
bin/core-test: test/CMakeFiles/core-test.dir/build.make
bin/core-test: test/libtest-main.a
bin/core-test: test/libgmock.a
bin/core-test: libfmt.a
bin/core-test: test/CMakeFiles/core-test.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX executable ../bin/core-test"
	cd /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/test && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/core-test.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
test/CMakeFiles/core-test.dir/build: bin/core-test

.PHONY : test/CMakeFiles/core-test.dir/build

test/CMakeFiles/core-test.dir/requires: test/CMakeFiles/core-test.dir/core-test.cc.o.requires

.PHONY : test/CMakeFiles/core-test.dir/requires

test/CMakeFiles/core-test.dir/clean:
	cd /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/test && $(CMAKE_COMMAND) -P CMakeFiles/core-test.dir/cmake_clean.cmake
.PHONY : test/CMakeFiles/core-test.dir/clean

test/CMakeFiles/core-test.dir/depend:
	cd /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2 /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/test /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/test /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/test/CMakeFiles/core-test.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : test/CMakeFiles/core-test.dir/depend

