//
// Created by timo on 8/10/20.
//

#ifndef ARKOICOMPILER_SCOPECHECK_H
#define ARKOICOMPILER_SCOPECHECK_H

#include <memory>

class RootNode;

class ScopeCheck {

public:
    ScopeCheck() = delete;

public:
    static void visitRoot(const std::shared_ptr<RootNode> &rootNode);

};

#endif //ARKOICOMPILER_SCOPECHECK_H
