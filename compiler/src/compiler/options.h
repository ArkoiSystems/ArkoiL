//
// Created by timo on 7/29/20.
//

#pragma once

#include <vector>

struct CompilerOptions {
    std::vector<std::string> m_SearchPaths;
    std::string m_SourceFile;
    bool mb_VerboseLLVM_IR;
};