//
// Created by timo on 8/7/20.
//

#pragma once

#include <unordered_map>
#include <functional>
#include <memory>
#include <vector>

#include "allnodes.h"

class SymbolTable {

    typedef std::vector<SharedASTNode> Symbols;
    typedef std::unordered_map<std::string, Symbols> Table;
    typedef std::function<bool(const SharedASTNode &)> Predicate;

private:
    SharedSymbolTable m_Parent;
    Table m_Table;

public:
    explicit SymbolTable(SharedSymbolTable parent);

    SymbolTable(const SymbolTable &other);

    SymbolTable &operator=(const SymbolTable &) = delete;

public:
    std::shared_ptr<SymbolTable::Symbols> all(const std::string &id,
                                              const SymbolTable::Predicate &predicate);

    std::shared_ptr<SymbolTable::Symbols> scope(const std::string &id,
                                                const SymbolTable::Predicate &predicate);

    void insert(const std::string &id, const SharedASTNode &node);

public:
    const SharedSymbolTable &getParent() const;

    const Table &getTable() const;

};