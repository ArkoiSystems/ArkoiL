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
include test/CMakeFiles/ostream-test.dir/depend.make

# Include the progress variables for this target.
include test/CMakeFiles/ostream-test.dir/progress.make

# Include the compile flags for this target's objects.
include test/CMakeFiles/ostream-test.dir/flags.make

test/CMakeFiles/ostream-test.dir/ostream-test.cc.o: test/CMakeFiles/ostream-test.dir/flags.make
test/CMakeFiles/ostream-test.dir/ostream-test.cc.o: ../test/ostream-test.cc
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object test/CMakeFiles/ostream-test.dir/ostream-test.cc.o"
	cd /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/test && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/ostream-test.dir/ostream-test.cc.o -c /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/test/ostream-test.cc

test/CMakeFiles/ostream-test.dir/ostream-test.cc.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/ostream-test.dir/ostream-test.cc.i"
	cd /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/test && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/test/ostream-test.cc > CMakeFiles/ostream-test.dir/ostream-test.cc.i

test/CMakeFiles/ostream-test.dir/ostream-test.cc.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/ostream-test.dir/ostream-test.cc.s"
	cd /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/test && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/test/ostream-test.cc -o CMakeFiles/ostream-test.dir/ostream-test.cc.s

test/CMakeFiles/ostream-test.dir/ostream-test.cc.o.requires:

.PHONY : test/CMakeFiles/ostream-test.dir/ostream-test.cc.o.requires

test/CMakeFiles/ostream-test.dir/ostream-test.cc.o.provides: test/CMakeFiles/ostream-test.dir/ostream-test.cc.o.requires
	$(MAKE) -f test/CMakeFiles/ostream-test.dir/build.make test/CMakeFiles/ostream-test.dir/ostream-test.cc.o.provides.build
.PHONY : test/CMakeFiles/ostream-test.dir/ostream-test.cc.o.provides

test/CMakeFiles/ostream-test.dir/ostream-test.cc.o.provides.build: test/CMakeFiles/ostream-test.dir/ostream-test.cc.o


# Object files for target ostream-test
ostream__test_OBJECTS = \
"CMakeFiles/ostream-test.dir/ostream-test.cc.o"

# External object files for target ostream-test
ostream__test_EXTERNAL_OBJECTS =

bin/ostream-test: test/CMakeFiles/ostream-test.dir/ostream-test.cc.o
bin/ostream-test: test/CMakeFiles/ostream-test.dir/build.make
bin/ostream-test: test/libtest-main.a
bin/ostream-test: test/libgmock.a
bin/ostream-test: libfmt.a
bin/ostream-test: test/CMakeFiles/ostream-test.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX executable ../bin/ostream-test"
	cd /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/test && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/ostream-test.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
test/CMakeFiles/ostream-test.dir/build: bin/ostream-test

.PHONY : test/CMakeFiles/ostream-test.dir/build

test/CMakeFiles/ostream-test.dir/requires: test/CMakeFiles/ostream-test.dir/ostream-test.cc.o.requires

.PHONY : test/CMakeFiles/ostream-test.dir/requires

test/CMakeFiles/ostream-test.dir/clean:
	cd /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/test && $(CMAKE_COMMAND) -P CMakeFiles/ostream-test.dir/cmake_clean.cmake
.PHONY : test/CMakeFiles/ostream-test.dir/clean

test/CMakeFiles/ostream-test.dir/depend:
	cd /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2 /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/test /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/test /home/timo/dev/ArkoiSystems/ArkoiL/compiler/c++/libs/fmt-7.0.2/build/test/CMakeFiles/ostream-test.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : test/CMakeFiles/ostream-test.dir/depend

