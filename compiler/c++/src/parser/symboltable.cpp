//
// Created by timo on 8/7/20.
//

#include "symboltable.h"
#include "astnodes.h"

void SymbolTable::insert(const std::string &id, const std::shared_ptr<ASTNode> &node) {
    auto iterator = table.find(id);
    if (iterator != table.end()) {
        iterator->second.push_back(node);
    } else {
        Symbols symbols{};
        symbols.push_back(node);
        table.emplace(id, symbols);
    }
}

std::shared_ptr<Symbols>
SymbolTable::all(const std::string &id,
                 const std::function<bool(const std::shared_ptr<ASTNode> &)> &predicate) {
    auto scopeSymbols = scope(id, predicate);
    if (scopeSymbols != nullptr)
        return scopeSymbols;

    if (parent != nullptr)
        return parent->all(id, predicate);
    return nullptr;
}

std::shared_ptr<Symbols>
SymbolTable::scope(const std::string &id,
                   const std::function<bool(const std::shared_ptr<ASTNode> &)> &predicate) {
    auto iterator = table.find(id);
    if (iterator != table.end()) {
        auto nodes = iterator->second;

        auto newSymbols = std::make_shared<Symbols>();
        for (const auto &node : nodes) {
            if (!predicate(node))
                continue;
            newSymbols->push_back(node);
        }

        if (!newSymbols->empty())
            return newSymbols;
    }

    return nullptr;

}
