find_path(LLD_INCLUDE_DIRS
        NAMES
            ldd/Common/Driver.h
        PATHS
            /usr/lib/llvm-10/include
            /usr/local/llvm100/include
            /usr/local/llvm10/include
            /mingw64/include)

if(NOT DEFINED LLD_INCLUDE_DIRS)
    message(ERROR_FATAL "You need to have LLD installed to use the compiler")
endif()

find_library(LLD_LIBRARY
        NAMES
            lld-10.0
            lld
        PATHS
            /usr/lib/llvm-10/lib)

if(EXISTS ${LLD_LIBRARY})
    set(LLD_LIBRARIES ${LLD_LIBRARY})
else()
    macro(FIND_AND_ADD_LLD_LIB _libname_)
        string(TOUPPER ${_libname_} _prettyLibname_)
        find_library(LLD_${_prettyLibname_}_LIB
                NAMES
                    ${_libname_}
                PATHS
                    ${LLD_LIBDIRS}
                    /usr/lib/llvm-10/lib
                    /usr/local/llvm100/lib
                    /usr/local/llvm10/lib
                    /mingw64/lib
                    /c/msys64/mingw64/lib
                    c:/msys64/mingw64/lib)
        if(LLD_${_prettyLibname_}_LIB)
            set(LLD_LIBRARIES ${LLD_LIBRARIES} ${LLD_${_prettyLibname_}_LIB})
        endif()
    endmacro(FIND_AND_ADD_LLD_LIB)

    FIND_AND_ADD_LLD_LIB(lldDriver)
    FIND_AND_ADD_LLD_LIB(lldMinGW)
    FIND_AND_ADD_LLD_LIB(lldELF)
    FIND_AND_ADD_LLD_LIB(lldCOFF)
    FIND_AND_ADD_LLD_LIB(lldMachO)
    FIND_AND_ADD_LLD_LIB(lldWasm)
    FIND_AND_ADD_LLD_LIB(lldReaderWriter)
    FIND_AND_ADD_LLD_LIB(lldCore)
    FIND_AND_ADD_LLD_LIB(lldYAML)
    FIND_AND_ADD_LLD_LIB(lldCommon)
endif()

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args(LLD DEFAULT_MSG LLD_LIBRARIES LLD_INCLUDE_DIRS)

mark_as_advanced(LLD_INCLUDE_DIRS LLD_LIBRARIES)