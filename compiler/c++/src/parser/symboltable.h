//
// Created by timo on 8/7/20.
//

#ifndef ARKOICOMPILER_SYMBOLTABLE_H
#define ARKOICOMPILER_SYMBOLTABLE_H

#include <unordered_map>
#include <utility>
#include <memory>
#include <vector>
#include <iostream>

class ASTNode;

typedef std::vector<std::shared_ptr<ASTNode>> Symbols;
typedef std::unordered_map<std::string, Symbols> Table;

class SymbolTable {

public:
    std::shared_ptr<SymbolTable> parent;
    Table table;

public:
    explicit SymbolTable(std::shared_ptr<SymbolTable> parent) :
            parent(std::move(parent)),
            table({}) {}

public:
    template<typename Function>
    std::shared_ptr<Symbols> general(const std::string &id, Function predicate = nullptr) {
        auto scopeSymbols = scope(id, predicate);
        if (scopeSymbols != nullptr)
            return scopeSymbols;

        if (parent != nullptr)
            return parent->general(id, predicate);
        return nullptr;
    }

    template<typename Function>
    std::shared_ptr<Symbols> scope(const std::string &id, Function predicate = nullptr) {
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

    void insert(const std::string &id, const std::shared_ptr<ASTNode> &node);

};

#endif //ARKOICOMPILER_SYMBOLTABLE_H
