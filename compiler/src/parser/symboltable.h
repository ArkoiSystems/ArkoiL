//
// Created by timo on 8/7/20.
//

#pragma once

#include <unordered_map>
#include <functional>
#include <memory>
#include <vector>

class ASTNode;

class SymbolTable {

    typedef std::vector<std::shared_ptr<ASTNode>> Symbols;
    typedef std::unordered_map<std::string, Symbols> Table;
    typedef std::function<bool(const std::shared_ptr<ASTNode> &)> Predicate;

private:
    std::shared_ptr<SymbolTable> m_Parent;

public:
    Table m_Table;

public:
    explicit SymbolTable(std::shared_ptr<SymbolTable> parent);

    SymbolTable(const SymbolTable &other);

    SymbolTable &operator=(const SymbolTable &) = delete;

public:
    std::shared_ptr<SymbolTable::Symbols> all(const std::string &id,
                                              const SymbolTable::Predicate &predicate);

    std::shared_ptr<SymbolTable::Symbols> scope(const std::string &id,
                                                const SymbolTable::Predicate &predicate);

    void insert(const std::string &id, const std::shared_ptr<ASTNode> &node);

public:
    void setParent(const std::shared_ptr<SymbolTable> &parent);

};