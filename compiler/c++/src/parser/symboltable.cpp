//
// Created by timo on 8/7/20.
//

#include "symboltable.h"
#include "astnodes.h"
#include "../../deps/dbg-macro/dbg.h"

void SymbolTable::insert(const std::string &id, const std::shared_ptr<ASTNode> &node) {
    auto iterator = table.find(id);
    if(iterator != table.end()) {
        iterator->second.push_back(node);
    } else {
        Symbols symbols{};
        symbols.push_back(node);
        table.emplace(id, symbols);
    }
}