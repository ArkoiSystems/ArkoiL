macro(ARKOI_LLD_FIND_AND_ADD_LIB _libname_)
    string(TOUPPER ${_libname_} _prettyLibname_)
    find_library(ARKOI_LLD_${_prettyLibname_}_LIB
            NAME
            ${_libname_}
            PATHS
            /usr/lib/llvm-10/lib)
    if (ARKOI_LLD_${_prettyLibname_}_LIB)
        set(ARKOI_LLD_LIBRARIES ${ARKOI_LLD_LIBRARIES} ${ARKOI_LLD_${_prettyLibname_}_LIB})
    endif ()
endmacro()

find_path(ARKOI_LLD_INCLUDE_DIRS
        NAME
        lld/Common/Driver.h
        PATHS
        /usr/lib/llvm-10/include)

find_library(ARKOI_LLD_LIBRARY
        NAMES
        lld-10.0
        lld
        PATHS
        /usr/lib/llvm-10/lib)

if (ARKOI_LLD_LIBRARY)
    set(ARKOI_LLD_LIBRARIES ${ARKOI_LLD_LIBRARY})
else ()
    ARKOI_LLD_FIND_AND_ADD_LIB(lldDriver)
    ARKOI_LLD_FIND_AND_ADD_LIB(lldMinGW)
    ARKOI_LLD_FIND_AND_ADD_LIB(lldELF)
    ARKOI_LLD_FIND_AND_ADD_LIB(lldCOFF)
    ARKOI_LLD_FIND_AND_ADD_LIB(lldMachO)
    ARKOI_LLD_FIND_AND_ADD_LIB(lldWasm)
    ARKOI_LLD_FIND_AND_ADD_LIB(lldReaderWriter)
    ARKOI_LLD_FIND_AND_ADD_LIB(lldCore)
    ARKOI_LLD_FIND_AND_ADD_LIB(lldYAML)
    ARKOI_LLD_FIND_AND_ADD_LIB(lldCommon)
endif ()

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args(ArkoiLLD DEFAULT_MSG ARKOI_LLD_LIBRARIES ARKOI_LLD_INCLUDE_DIRS)

mark_as_advanced(ARKOI_LLD_LIBRARIES ARKOI_LLD_INCLUDE_DIRS)