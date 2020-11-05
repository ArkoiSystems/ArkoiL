//
// Created by timo on 8/7/20.
//

#include "symboltable.h"

#include <iostream>
#include <utility>

#include "astnodes.h"

SymbolTable::SymbolTable(SharedSymbolTable parent)
        : m_Parent(std::move(parent)), m_Table({}) {}

SymbolTable::SymbolTable(const SymbolTable &other)
        : m_Parent(nullptr), m_Table(other.m_Table) {}


void SymbolTable::insert(const std::string &id, const SharedASTNode &node) {
    auto iterator = m_Table.find(id);
    if (iterator != m_Table.end()) {
        iterator->second.emplace_back(node);
    } else {
        Symbols symbols{};
        symbols.emplace_back(node);
        m_Table.emplace(id, symbols);
    }
}

void SymbolTable::all(Symbols &symbols, const std::string &id,
                      const SymbolTable::Predicate &predicate) {
    scope(symbols, id, predicate);
    if (!symbols.empty())
        return;

    if (m_Parent)
        return m_Parent->all(symbols, id, predicate);
}

void SymbolTable::scope(Symbols &symbols, const std::string &id,
                        const SymbolTable::Predicate &predicate) {
    auto iterator = m_Table.find(id);
    if (iterator == m_Table.end())
        return;

    for (const auto &node : iterator->second) {
        if (predicate && !predicate(node))
            continue;

        symbols.emplace_back(node);
    }
}

const SharedSymbolTable &SymbolTable::getParent() const {
    return m_Parent;
}

const SymbolTable::Table &SymbolTable::getTable() const {
    return m_Table;
}