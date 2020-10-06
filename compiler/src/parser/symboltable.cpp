//
// Created by timo on 8/7/20.
//

#include "symboltable.h"

#include <iostream>
#include <utility>

#include "astnodes.h"

SymbolTable::SymbolTable(std::shared_ptr<SymbolTable> parent)
        : m_Parent(std::move(parent)), m_Table({}) {}

SymbolTable::SymbolTable(const SymbolTable &other)
        : m_Parent(nullptr), m_Table(other.m_Table) {}


void SymbolTable::insert(const std::string &id, const std::shared_ptr<ASTNode> &node) {
    auto iterator = m_Table.find(id);
    if (iterator != m_Table.end()) {
        iterator->second.push_back(node);
    } else {
        Symbols symbols{};
        symbols.push_back(node);
        m_Table.emplace(id, symbols);
    }
}

std::shared_ptr<SymbolTable::Symbols> SymbolTable::all(const std::string &id,
                                                       const SymbolTable::Predicate &predicate) {
    auto scopeSymbols = scope(id, predicate);
    if (scopeSymbols != nullptr)
        return scopeSymbols;

    if (m_Parent != nullptr)
        return m_Parent->all(id, predicate);
    return nullptr;
}

std::shared_ptr<SymbolTable::Symbols> SymbolTable::scope(const std::string &id,
                                                         const SymbolTable::Predicate &predicate) {
    auto iterator = m_Table.find(id);
    if (iterator == m_Table.end())
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

void SymbolTable::setParent(const std::shared_ptr<SymbolTable> &parent) {
    m_Parent = parent;
}