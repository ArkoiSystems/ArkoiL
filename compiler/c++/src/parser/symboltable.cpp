//
// Created by timo on 8/7/20.
//

#include "symboltable.h"

#include <iostream>

#include "astnodes.h"

SymbolTable::SymbolTable(const std::shared_ptr<SymbolTable> &parent) {
    this->parent = parent;
    table = {};
}

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

std::shared_ptr<SymbolTable::Symbols> SymbolTable::all(const std::string &id,
                                                       const SymbolTable::Predicate &predicate) {
    auto scopeSymbols = scope(id, predicate);
    if (scopeSymbols != nullptr)
        return scopeSymbols;

    if (parent != nullptr)
        return parent->all(id, predicate);
    return nullptr;
}

std::shared_ptr<SymbolTable::Symbols> SymbolTable::scope(const std::string &id,
                                                         const SymbolTable::Predicate &predicate) {
    auto iterator = table.find(id);
    if (iterator == table.end())
        return nullptr;

    auto nodes = iterator->second;
    if (nodes.empty())
        return nullptr;

    auto newSymbols = std::make_shared<Symbols>();
    for (const auto &node : nodes) {
        if (!predicate(node))
            continue;
        newSymbols->push_back(node);
    }

    if (newSymbols->empty())
        return nullptr;

    return newSymbols;
}