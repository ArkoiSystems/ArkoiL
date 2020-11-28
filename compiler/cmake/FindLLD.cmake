macro(LLD_FIND_AND_ADD_LIB _libname_)
    string(TOUPPER ${_libname_} _prettyLibname_)
    find_library(LLD_${_prettyLibname_}_LIB
            NAME
            ${_libname_}
            PATHS
            /usr/lib/llvm-10/lib)
    if (LLD_${_prettyLibname_}_LIB)
        set(LLD_LIBRARIES ${LLD_LIBRARIES} ${LLD_${_prettyLibname_}_LIB})
    endif ()
endmacro()

find_path(LLD_INCLUDE_DIRS
        NAME
        lld/Common/Driver.h
        PATHS
        /usr/lib/llvm-10/include)

find_library(LLD_LIBRARY
        NAMES
        lld-10.0
        lld
        PATHS
        /usr/lib/llvm-10/lib)

if (LLD_LIBRARY)
    set(LLD_LIBRARIES ${LLD_LIBRARY})
else ()
    LLD_FIND_AND_ADD_LIB(lldDriver)
    LLD_FIND_AND_ADD_LIB(lldMinGW)
    LLD_FIND_AND_ADD_LIB(lldELF)
    LLD_FIND_AND_ADD_LIB(lldCOFF)
    LLD_FIND_AND_ADD_LIB(lldMachO)
    LLD_FIND_AND_ADD_LIB(lldWasm)
    LLD_FIND_AND_ADD_LIB(lldReaderWriter)
    LLD_FIND_AND_ADD_LIB(lldCore)
    LLD_FIND_AND_ADD_LIB(lldYAML)
    LLD_FIND_AND_ADD_LIB(lldCommon)
endif ()

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args(LLD DEFAULT_MSG LLD_LIBRARIES LLD_INCLUDE_DIRS)

mark_as_advanced(LLD_LIBRARIES LLD_INCLUDE_DIRS)