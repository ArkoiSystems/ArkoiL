//
// Created by timo on 9/7/20.
//

#include <sstream>
#include "astnodes.h"

#include "../lexer/token.h"
#include "symboltable.h"
#include "../utils.h"

/* ----------======== AST-NODE ========---------- */

ASTNode::ASTNode()
        : m_Kind(ASTNode::NONE), mb_Failed(false),
          m_StartToken(nullptr), m_EndToken(nullptr),
          m_Parent(nullptr), m_Scope(nullptr) {}

ASTNode::ASTNode(const ASTNode &other)
        : m_Kind(other.m_Kind), mb_Failed(other.mb_Failed),
          m_StartToken(nullptr), m_EndToken(nullptr),
          m_Parent(nullptr), m_Scope(nullptr) {
    if (other.m_StartToken)
        m_StartToken = std::make_shared<Token>(*other.m_StartToken);
    if (other.m_EndToken)
        m_EndToken = std::make_shared<Token>(*other.m_EndToken);
    if (other.m_Scope)
        m_Scope = std::make_shared<SymbolTable>(*other.m_Scope);
}

const std::shared_ptr<Token> &ASTNode::getStartToken() const {
    return m_StartToken;
}

void ASTNode::setStartToken(const std::shared_ptr<Token> &startToken) {
    m_StartToken = startToken;
}

const std::shared_ptr<Token> &ASTNode::getEndToken() const {
    return m_EndToken;
}

void ASTNode::setEndToken(const std::shared_ptr<Token> &endToken) {
    m_EndToken = endToken;
}

const std::shared_ptr<SymbolTable> &ASTNode::getScope() const {
    return m_Scope;
}

void ASTNode::setScope(const std::shared_ptr<SymbolTable> &scope) {
    m_Scope = scope;
}

const std::shared_ptr<ASTNode> &ASTNode::getParent() const {
    return m_Parent;
}

void ASTNode::setParent(const std::shared_ptr<ASTNode> &parent) {
    m_Parent = parent;
}

bool ASTNode::isFailed() const {
    return mb_Failed;
}

void ASTNode::setFailed(bool failed) {
    mb_Failed = failed;
}

std::string ASTNode::getKindAsString() const {
    std::stringstream kindString;
    kindString << getKind();
    return kindString.str();
}

ASTNode::ASTKind ASTNode::getKind() const {
    return m_Kind;
}

void ASTNode::setKind(ASTNode::ASTKind kind) {
    m_Kind = kind;
}

std::ostream &operator<<(std::ostream &os, const ASTNode::ASTKind &kind) {
    switch (kind) {
        case ASTNode::NONE:
            os << "None";
            break;
        case ASTNode::ROOT:
            os << "Root";
            break;
        case ASTNode::IMPORT:
            os << "Import";
            break;
        case ASTNode::FUNCTION:
            os << "Function";
            break;
        case ASTNode::PARAMETER:
            os << "Parameter";
            break;
        case ASTNode::TYPE:
            os << "Type";
            break;
        case ASTNode::BLOCK:
            os << "Block";
            break;
        case ASTNode::VARIABLE:
            os << "Variable";
            break;
        case ASTNode::BINARY:
            os << "Binary";
            break;
        case ASTNode::UNARY:
            os << "Unary";
            break;
        case ASTNode::PARENTHESIZED:
            os << "Parenthesized";
            break;
        case ASTNode::NUMBER:
            os << "Number";
            break;
        case ASTNode::STRING:
            os << "String";
            break;
        case ASTNode::IDENTIFIER:
            os << "Identifier";
            break;
        case ASTNode::FUNCTION_ARGUMENT:
            os << "Function Argument";
            break;
        case ASTNode::FUNCTION_CALL:
            os << "Function Call";
            break;
        case ASTNode::STRUCT_ARGUMENT:
            os << "Struct Argument";
            break;
        case ASTNode::STRUCT_CREATE:
            os << "Struct Create";
            break;
        case ASTNode::ASSIGNMENT:
            os << "Assignment";
            break;
        case ASTNode::RETURN:
            os << "Return";
            break;
        case ASTNode::STRUCT:
            os << "Struct";
            break;
        case ASTNode::OPERABLE:
            os << "Operable";
            break;
        default:
            os << "No name for this node.. Terminating." << std::endl;
            exit(-1);
    }

    return os;
}


/* ----------======== TYPED-NODE ========---------- */

TypedNode::TypedNode()
        : mb_TypeResolved(false), m_Type(nullptr),
          m_TargetNode(nullptr), mb_Accessed(false) {}

TypedNode::TypedNode(const TypedNode &other)
        : ASTNode(other),
          mb_TypeResolved(false), m_Type(nullptr),
          m_TargetNode(nullptr), mb_Accessed(false) {}

TypedNode *TypedNode::clone(std::shared_ptr<ASTNode> parent,
                            std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new TypedNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

const std::shared_ptr<ASTNode> &TypedNode::getTargetNode() const {
    return m_TargetNode;
}

void TypedNode::setTargetNode(const std::shared_ptr<ASTNode> &targetNode) {
    m_TargetNode = targetNode;
}

const std::shared_ptr<TypeNode> &TypedNode::getType() const {
    return m_Type;
}

void TypedNode::setType(const std::shared_ptr<TypeNode> &type) {
    m_Type = type;
}

bool TypedNode::isTypeResolved() const {
    return mb_TypeResolved;
}

void TypedNode::setTypeResolved(bool typeResolved) {
    mb_TypeResolved = typeResolved;
}

bool TypedNode::isAccessed() const {
    return mb_Accessed;
}

void TypedNode::setAccessed(bool mbAccessed) {
    mb_Accessed = mbAccessed;
}


/* ----------======== IMPORT-NODE ========---------- */

ImportNode::ImportNode()
        : m_Target(nullptr), m_Path(nullptr) {
    setKind(ASTNode::IMPORT);
}

ImportNode::ImportNode(const ImportNode &other)
        : ASTNode(other),
          m_Target(nullptr), m_Path(nullptr) {
    if (other.m_Target)
        m_Target = std::shared_ptr<RootNode>(other.m_Target->clone(std::shared_ptr<ImportNode>(this),
                                                                   this->getScope()));
    if (other.m_Path)
        m_Path = std::make_shared<Token>(*other.m_Path);
}

ImportNode *ImportNode::clone(std::shared_ptr<ASTNode> parent,
                              std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new ImportNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

const std::shared_ptr<RootNode> &ImportNode::getTarget() const {
    return m_Target;
}

void ImportNode::setTarget(const std::shared_ptr<RootNode> &target) {
    m_Target = target;
}

const std::shared_ptr<Token> &ImportNode::getPath() const {
    return m_Path;
}

void ImportNode::setPath(const std::shared_ptr<Token> &path) {
    m_Path = path;
}


/* ----------======== ROOT-NODE ========---------- */

RootNode::RootNode()
        : m_SourceCode({}), m_SourcePath({}),
          m_Nodes({}) {
    setKind(ASTNode::ROOT);
}

RootNode::RootNode(const RootNode &other)
        : ASTNode(other),
          m_SourcePath(other.m_SourcePath), m_SourceCode(other.m_SourceCode),
          m_Nodes({}) {
    for (const auto &node : other.m_Nodes)
        m_Nodes.push_back(std::shared_ptr<ASTNode>(node->clone(std::shared_ptr<RootNode>(this),
                                                               this->getScope())));
}

RootNode *RootNode::clone(std::shared_ptr<ASTNode> parent,
                          std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new RootNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

std::vector<std::shared_ptr<RootNode>> RootNode::getImportedRoots() {
    std::vector<std::shared_ptr<RootNode>> importedRoots;
    getImportedRoots(importedRoots);
    return importedRoots;
}

void RootNode::getImportedRoots(std::vector<std::shared_ptr<RootNode>> &importedRoots) {
    for (const auto &node : m_Nodes) {
        if (node->getKind() != IMPORT)
            continue;

        auto importNode = std::static_pointer_cast<ImportNode>(node);
        importNode->getTarget()->getImportedRoots(importedRoots);
        importedRoots.push_back(importNode->getTarget());
    }
}

std::shared_ptr<std::vector<std::shared_ptr<ASTNode>>>
RootNode::searchWithImports(const std::string &id,
                            const std::function<bool(const std::shared_ptr<ASTNode> &)> &predicate) {
    auto foundNodes = std::make_shared<std::vector<std::shared_ptr<ASTNode>>>();

    auto currentFounds = getScope()->scope(id, predicate);
    if (currentFounds != nullptr && !currentFounds->empty())
        foundNodes->insert(foundNodes->end(), currentFounds->begin(), currentFounds->end());

    auto importedRoots = getImportedRoots();
    for (const auto &importedRoot : importedRoots) {
        auto importedFounds = importedRoot->getScope()->scope(id, predicate);
        if (importedFounds == nullptr || importedFounds->empty())
            continue;

        foundNodes->insert(foundNodes->end(), importedFounds->begin(), importedFounds->end());
    }

    return foundNodes;
}

const std::vector<std::shared_ptr<ASTNode>> &RootNode::getNodes() const {
    return m_Nodes;
}

void RootNode::addNode(const std::shared_ptr<ASTNode> &node) {
    m_Nodes.push_back(node);
}

const std::string &RootNode::getSourcePath() const {
    return m_SourcePath;
}

void RootNode::setSourcePath(const std::string &sourcePath) {
    m_SourcePath = sourcePath;
}

const std::string &RootNode::getSourceCode() const {
    return m_SourceCode;
}

void RootNode::setSourceCode(const std::string &sourceCode) {
    m_SourceCode = sourceCode;
}


/* ----------======== PARAMETER-NODE ========---------- */

ParameterNode::ParameterNode()
        : m_Name(nullptr) {
    setKind(ASTNode::PARAMETER);
}

ParameterNode::ParameterNode(const ParameterNode &other)
        : TypedNode(other),
          m_Name(nullptr) {
    if (other.m_Name)
        m_Name = std::make_shared<Token>(*other.m_Name);
}

ParameterNode *ParameterNode::clone(std::shared_ptr<ASTNode> parent,
                                    std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new ParameterNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

const std::shared_ptr<Token> &ParameterNode::getName() const {
    return m_Name;
}

void ParameterNode::setName(const std::shared_ptr<Token> &name) {
    m_Name = name;
}


/* ----------======== BLOCK-NODE ========---------- */

BlockNode::BlockNode()
        : m_Nodes({}) {
    setKind(ASTNode::BLOCK);
}

BlockNode::BlockNode(const BlockNode &other)
        : TypedNode(other),
          m_Nodes({}) {
    for (auto const &node : other.m_Nodes)
        m_Nodes.push_back(std::shared_ptr<ASTNode>(node->clone(std::shared_ptr<BlockNode>(this),
                                                               this->getScope())));
}

BlockNode *BlockNode::clone(std::shared_ptr<ASTNode> parent,
                            std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new BlockNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

const std::vector<std::shared_ptr<ASTNode>> &BlockNode::getNodes() const {
    return m_Nodes;
}

void BlockNode::addNode(const std::shared_ptr<ASTNode> &node) {
    m_Nodes.push_back(node);
}


/* ----------======== OPERABLE-NODE ========---------- */

OperableNode::OperableNode() {
    setKind(ASTNode::OPERABLE);
}

OperableNode::OperableNode(const OperableNode &other)
        : TypedNode(other) {}

OperableNode *OperableNode::clone(std::shared_ptr<ASTNode> parent,
                                  std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new OperableNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}


/* ----------======== VARIABLE-NODE ========---------- */

VariableNode::VariableNode()
        : mb_Constant(false), mb_Local(false),
          m_Expression(nullptr), m_Name(nullptr) {
    setKind(ASTNode::VARIABLE);
}

VariableNode::VariableNode(const VariableNode &other)
        : TypedNode(other),
          mb_Constant(other.mb_Constant), mb_Local(other.mb_Local),
          m_Expression(nullptr), m_Name(nullptr) {
    if (other.m_Expression)
        m_Expression = std::shared_ptr<OperableNode>(other.m_Expression->clone(
                std::shared_ptr<VariableNode>(this),
                this->getScope()));
    if (other.m_Name)
        m_Name = std::make_shared<Token>(*other.m_Name);
}

VariableNode *VariableNode::clone(std::shared_ptr<ASTNode> parent,
                                  std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new VariableNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

bool VariableNode::isGlobal() {
    return !mb_Local && getParent()->getKind() != ASTNode::STRUCT;
}

const std::shared_ptr<OperableNode> &VariableNode::getExpression() const {
    return m_Expression;
}

void VariableNode::setExpression(const std::shared_ptr<OperableNode> &expression) {
    m_Expression = expression;
}

const std::shared_ptr<Token> &VariableNode::getName() const {
    return m_Name;
}

void VariableNode::setName(const std::shared_ptr<Token> &name) {
    m_Name = name;
}

bool VariableNode::isConstant() const {
    return mb_Constant;
}

void VariableNode::setConstant(bool constant) {
    mb_Constant = constant;
}

bool VariableNode::isLocal() const {
    return mb_Local;
}

void VariableNode::setLocal(bool local) {
    mb_Local = local;
}


/* ----------======== BINARY-NODE ========---------- */

BinaryNode::BinaryNode()
        : m_OperatorKind(BinaryNode::NONE),
          m_Lhs(nullptr), m_Rhs(nullptr) {
    setKind(ASTNode::BINARY);
}

BinaryNode::BinaryNode(const BinaryNode &other)
        : OperableNode(other),
          m_OperatorKind(other.m_OperatorKind),
          m_Lhs(nullptr), m_Rhs(nullptr) {
    if (other.m_Lhs)
        m_Lhs = std::shared_ptr<OperableNode>(other.m_Lhs->clone(std::shared_ptr<BinaryNode>(this),
                                                                 this->getScope()));
    if (other.m_Rhs)
        m_Rhs = std::shared_ptr<OperableNode>(other.m_Rhs->clone(std::shared_ptr<BinaryNode>(this),
                                                                 this->getScope()));
}

BinaryNode *BinaryNode::clone(std::shared_ptr<ASTNode> parent,
                              std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new BinaryNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

const std::shared_ptr<OperableNode> &BinaryNode::getLHS() const {
    return m_Lhs;
}

void BinaryNode::setLHS(const std::shared_ptr<OperableNode> &lhs) {
    m_Lhs = lhs;
}

const std::shared_ptr<OperableNode> &BinaryNode::getRHS() const {
    return m_Rhs;
}

void BinaryNode::setRHS(const std::shared_ptr<OperableNode> &rhs) {
    m_Rhs = rhs;
}

std::string BinaryNode::getOperatorKindAsString() const {
    std::stringstream kindString;
    kindString << getOperatorKind();
    return kindString.str();
}

BinaryNode::BinaryKind BinaryNode::getOperatorKind() const {
    return m_OperatorKind;
}

void BinaryNode::setOperatorKind(BinaryNode::BinaryKind operatorKind) {
    m_OperatorKind = operatorKind;
}

std::ostream &operator<<(std::ostream &os, const BinaryNode::BinaryKind &kind) {
    switch (kind) {
        case BinaryNode::NONE:
            os << "None";
            break;
        case BinaryNode::ADDITION:
            os << "Addition (+)";
            break;
        case BinaryNode::SUBTRACTION:
            os << "Subtraction (-)";
            break;
        case BinaryNode::MULTIPLICATION:
            os << "Multiplication (*)";
            break;
        case BinaryNode::DIVISION:
            os << "Division (/)";
            break;
        case BinaryNode::REMAINING:
            os << "Remaining (%)";
            break;
        case BinaryNode::LESS_THAN:
            os << "Less Than (<)";
            break;
        case BinaryNode::GREATER_THAN:
            os << "Greater Than (>)";
            break;
        case BinaryNode::LESS_EQUAL_THAN:
            os << "Less Equal Than (<=)";
            break;
        case BinaryNode::GREATER_EQUAL_THAN:
            os << "Greater Equal Than (>=)";
            break;
        case BinaryNode::EQUAL:
            os << "Equal (=)";
            break;
        case BinaryNode::NOT_EQUAL:
            os << "Not Equal (!=)";
            break;
        case BinaryNode::BIT_CAST:
            os << "Bitcast";
            break;
        default:
            os << "No name for this binary node.. Terminating." << std::endl;
            exit(-1);
    }

    return os;
}


/* ----------======== UNARY-NODE ========---------- */

UnaryNode::UnaryNode()
        : m_OperatorKind(UnaryNode::NONE), m_Expression(nullptr) {
    setKind(ASTNode::UNARY);
}

UnaryNode::UnaryNode(const UnaryNode &other)
        : OperableNode(other),
          m_OperatorKind(other.m_OperatorKind), m_Expression(nullptr) {
    if (other.m_Expression)
        m_Expression = std::shared_ptr<OperableNode>(other.m_Expression->clone(
                std::shared_ptr<UnaryNode>(this),
                this->getScope()));
}

UnaryNode *UnaryNode::clone(std::shared_ptr<ASTNode> parent,
                            std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new UnaryNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

const std::shared_ptr<OperableNode> &UnaryNode::getExpression() const {
    return m_Expression;
}

void UnaryNode::setExpression(const std::shared_ptr<OperableNode> &expression) {
    m_Expression = expression;
}

std::string UnaryNode::getOperatorKindAsString() const {
    std::stringstream kindString;
    kindString << getOperatorKind();
    return kindString.str();
}

UnaryNode::UnaryKind UnaryNode::getOperatorKind() const {
    return m_OperatorKind;
}

void UnaryNode::setOperatorKind(UnaryNode::UnaryKind operatorKind) {
    m_OperatorKind = operatorKind;
}

std::ostream &operator<<(std::ostream &os, const UnaryNode::UnaryKind &kind) {
    switch (kind) {
        case UnaryNode::NONE:
            os << "None";
            break;
        case UnaryNode::NEGATE:
            os << "Negate (!)";
            break;
        default:
            os << "No name for this unary node.. Terminating." << std::endl;
            exit(-1);
    }

    return os;
}


/* ----------======== PARENTHESIZED-NODE ========---------- */

ParenthesizedNode::ParenthesizedNode()
        : m_Expression(nullptr) {
    setKind(ASTNode::PARENTHESIZED);
}

ParenthesizedNode::ParenthesizedNode(const ParenthesizedNode &other)
        : OperableNode(other),
          m_Expression(nullptr) {
    if (other.m_Expression)
        m_Expression = std::shared_ptr<OperableNode>(other.m_Expression->clone(
                std::shared_ptr<ParenthesizedNode>(this),
                this->getScope()));
}

ParenthesizedNode *ParenthesizedNode::clone(std::shared_ptr<ASTNode> parent,
                                            std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new ParenthesizedNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

const std::shared_ptr<OperableNode> &ParenthesizedNode::getExpression() const {
    return m_Expression;
}

void ParenthesizedNode::setExpression(const std::shared_ptr<OperableNode> &expression) {
    m_Expression = expression;
}


/* ----------======== NUMBER-NODE ========---------- */

NumberNode::NumberNode()
        : m_Number(nullptr) {
    setKind(ASTNode::NUMBER);
}

NumberNode::NumberNode(const NumberNode &other)
        : OperableNode(other),
          m_Number(nullptr) {
    if (other.m_Number)
        m_Number = std::make_shared<Token>(*other.m_Number);
}

NumberNode *NumberNode::clone(std::shared_ptr<ASTNode> parent,
                              std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new NumberNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

const std::shared_ptr<Token> &NumberNode::getNumber() const {
    return m_Number;
}

void NumberNode::setNumber(const std::shared_ptr<Token> &number) {
    m_Number = number;
}


/* ----------======== STRING-NODE ========---------- */

StringNode::StringNode()
        : m_String(nullptr) {
    setKind(ASTNode::STRING);
}

StringNode::StringNode(const StringNode &other)
        : OperableNode(other),
          m_String(nullptr) {
    if (other.m_String)
        m_String = std::make_shared<Token>(*other.m_String);
}

StringNode *StringNode::clone(std::shared_ptr<ASTNode> parent,
                              std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new StringNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

const std::shared_ptr<Token> &StringNode::getString() const {
    return m_String;
}

void StringNode::setString(const std::shared_ptr<Token> &string) {
    m_String = string;
}


/* ----------======== ARGUMENT-NODE ========---------- */

FunctionArgumentNode::FunctionArgumentNode()
        : m_Expression(nullptr), m_Name(nullptr),
          mb_TypeWhitelisted(false) {
    setKind(ASTNode::FUNCTION_ARGUMENT);
}

FunctionArgumentNode::FunctionArgumentNode(const FunctionArgumentNode &other)
        : TypedNode(other),
          m_Expression(nullptr), m_Name(nullptr),
          mb_TypeWhitelisted(other.mb_TypeWhitelisted) {
    if (other.m_Expression)
        m_Expression = std::shared_ptr<OperableNode>(other.m_Expression->clone(
                std::shared_ptr<FunctionArgumentNode>(this),
                this->getScope()));
    if (other.m_Name)
        m_Name = std::make_shared<Token>(*other.m_Name);
}

FunctionArgumentNode *FunctionArgumentNode::clone(std::shared_ptr<ASTNode> parent,
                                                  std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new FunctionArgumentNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

const std::shared_ptr<OperableNode> &FunctionArgumentNode::getExpression() const {
    return m_Expression;
}

void FunctionArgumentNode::setExpression(const std::shared_ptr<OperableNode> &expression) {
    m_Expression = expression;
}

const std::shared_ptr<Token> &FunctionArgumentNode::getName() const {
    return m_Name;
}

void FunctionArgumentNode::setName(const std::shared_ptr<Token> &name) {
    m_Name = name;
}

bool FunctionArgumentNode::isTypeWhitelisted() const {
    return mb_TypeWhitelisted;
}

void FunctionArgumentNode::setTypeWhitelisted(bool typeWhitelisted) {
    mb_TypeWhitelisted = typeWhitelisted;
}


/* ----------======== IDENTIFIER-NODE ========---------- */

IdentifierNode::IdentifierNode()
        : mb_Dereference(false), mb_Pointer(false),
          m_NextIdentifier(nullptr), m_LastIdentifier(nullptr),
          m_Identifier(nullptr) {
    setKind(ASTNode::IDENTIFIER);
}

IdentifierNode::IdentifierNode(const IdentifierNode &other)
        : OperableNode(other),
          mb_Dereference(other.mb_Dereference), mb_Pointer(other.mb_Pointer),
          m_NextIdentifier(nullptr), m_LastIdentifier(nullptr),
          m_Identifier(nullptr) {
    if (other.m_NextIdentifier)
        m_NextIdentifier = std::shared_ptr<IdentifierNode>(other.m_NextIdentifier->clone(
                std::shared_ptr<IdentifierNode>(this),
                this->getScope()));
    if (other.m_LastIdentifier)
        m_LastIdentifier = std::shared_ptr<IdentifierNode>(other.m_LastIdentifier->clone(
                std::shared_ptr<IdentifierNode>(this),
                this->getScope()));
    if (other.m_Identifier)
        m_Identifier = std::make_shared<Token>(*other.m_Identifier);
}

IdentifierNode *IdentifierNode::clone(std::shared_ptr<ASTNode> parent,
                                      std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new IdentifierNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

const std::shared_ptr<IdentifierNode> &IdentifierNode::getNextIdentifier() const {
    return m_NextIdentifier;
}

void IdentifierNode::setNextIdentifier(const std::shared_ptr<IdentifierNode> &nextIdentifier) {
    m_NextIdentifier = nextIdentifier;
}

const std::shared_ptr<IdentifierNode> &IdentifierNode::getLastIdentifier() const {
    return m_LastIdentifier;
}

void IdentifierNode::setLastIdentifier(const std::shared_ptr<IdentifierNode> &lastIdentifier) {
    m_LastIdentifier = lastIdentifier;
}

const std::shared_ptr<Token> &IdentifierNode::getIdentifier() const {
    return m_Identifier;
}

void IdentifierNode::setIdentifier(const std::shared_ptr<Token> &identifier) {
    m_Identifier = identifier;
}

bool IdentifierNode::isPointer() const {
    return mb_Pointer;
}

void IdentifierNode::setPointer(bool pointer) {
    mb_Pointer = pointer;
}

bool IdentifierNode::isDereference() const {
    return mb_Dereference;
}

void IdentifierNode::setDereference(bool dereference) {
    mb_Dereference = dereference;
}


/* ----------======== ASSIGNMENT-NODE ========---------- */

AssignmentNode::AssignmentNode()
        : m_StartIdentifier(nullptr), m_EndIdentifier(nullptr),
          m_Expression(nullptr) {
    setKind(ASTNode::ASSIGNMENT);
}

AssignmentNode::AssignmentNode(const AssignmentNode &other)
        : OperableNode(other),
          m_StartIdentifier(nullptr), m_EndIdentifier(nullptr),
          m_Expression(nullptr) {
    if (other.m_StartIdentifier)
        m_StartIdentifier = std::shared_ptr<IdentifierNode>(other.m_StartIdentifier->clone(
                std::shared_ptr<AssignmentNode>(this),
                this->getScope()));
    if (other.m_EndIdentifier)
        m_EndIdentifier = std::shared_ptr<IdentifierNode>(other.m_EndIdentifier->clone(
                std::shared_ptr<AssignmentNode>(this),
                this->getScope()));
    if (other.m_Expression)
        m_Expression = std::shared_ptr<OperableNode>(other.m_Expression->clone(
                std::shared_ptr<AssignmentNode>(this),
                this->getScope()));
}

AssignmentNode *AssignmentNode::clone(std::shared_ptr<ASTNode> parent,
                                      std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new AssignmentNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

const std::shared_ptr<IdentifierNode> &AssignmentNode::getStartIdentifier() const {
    return m_StartIdentifier;
}

void AssignmentNode::setStartIdentifier(const std::shared_ptr<IdentifierNode> &startIdentifier) {
    m_StartIdentifier = startIdentifier;
}

const std::shared_ptr<IdentifierNode> &AssignmentNode::getEndIdentifier() const {
    return m_EndIdentifier;
}

void AssignmentNode::setEndIdentifier(const std::shared_ptr<IdentifierNode> &endIdentifier) {
    m_EndIdentifier = endIdentifier;
}

const std::shared_ptr<OperableNode> &AssignmentNode::getExpression() const {
    return m_Expression;
}

void AssignmentNode::setExpression(const std::shared_ptr<OperableNode> &expression) {
    m_Expression = expression;
}


/* ----------======== RETURN-NODE ========---------- */

ReturnNode::ReturnNode()
        : m_Expression(nullptr) {
    setKind(ASTNode::RETURN);
}

ReturnNode::ReturnNode(const ReturnNode &other)
        : TypedNode(other),
          m_Expression(nullptr) {
    if (other.m_Expression)
        m_Expression = std::shared_ptr<OperableNode>(other.m_Expression->clone(
                std::shared_ptr<ReturnNode>(this),
                this->getScope()));
}

ReturnNode *ReturnNode::clone(std::shared_ptr<ASTNode> parent,
                              std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new ReturnNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

const std::shared_ptr<OperableNode> &ReturnNode::getExpression() const {
    return m_Expression;
}

void ReturnNode::setExpression(const std::shared_ptr<OperableNode> &expression) {
    m_Expression = expression;
}


/* ----------======== STRUCT-NODE ========---------- */

StructNode::StructNode()
        : m_Variables({}), m_Name(nullptr) {
    setKind(ASTNode::STRUCT);
}

StructNode::StructNode(const StructNode &other)
        : TypedNode(other),
          m_Variables({}), m_Name(nullptr) {
    for (const auto &variable : other.m_Variables)
        m_Variables.push_back(std::shared_ptr<VariableNode>(variable->clone(
                std::shared_ptr<StructNode>(this),
                this->getScope())));

    if (other.m_Name)
        m_Name = std::make_shared<Token>(*other.m_Name);
}

StructNode *StructNode::clone(std::shared_ptr<ASTNode> parent,
                              std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new StructNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

void StructNode::addVariable(const std::shared_ptr<VariableNode> &variable) {
    m_Variables.push_back(variable);
}

const std::vector<std::shared_ptr<VariableNode>> &StructNode::getVariables() const {
    return m_Variables;
}

const std::shared_ptr<Token> &StructNode::getName() const {
    return m_Name;
}

void StructNode::setName(const std::shared_ptr<Token> &name) {
    m_Name = name;
}


/* ----------======== TYPE-NODE ========---------- */

TypeNode::TypeNode()
        : mb_Floating(false), mb_Signed(false),
          m_TargetStruct(nullptr), m_TypeToken(nullptr),
          m_PointerLevel(0), m_Bits(0) {
    setKind(ASTNode::TYPE);
}

TypeNode::TypeNode(const TypeNode &other)
        : OperableNode(other),
          mb_Floating(other.mb_Floating), mb_Signed(other.mb_Signed),
          m_TargetStruct(nullptr), m_TypeToken(nullptr),
          m_PointerLevel(other.m_PointerLevel), m_Bits(other.m_Bits) {
    if (other.m_TypeToken)
        m_TypeToken = std::make_shared<Token>(*other.m_TypeToken);
}

TypeNode *TypeNode::clone(std::shared_ptr<ASTNode> parent,
                          std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new TypeNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

bool TypeNode::isNumeric() const {
    if ((mb_Signed || mb_Floating) && m_Bits > 0)
        return true;
    return m_Bits > 0 && m_TargetStruct == nullptr;
}

const std::shared_ptr<StructNode> &TypeNode::getTargetStruct() const {
    return m_TargetStruct;
}

void TypeNode::setTargetStruct(const std::shared_ptr<StructNode> &targetStruct) {
    m_TargetStruct = targetStruct;
}

unsigned int TypeNode::getPointerLevel() const {
    return m_PointerLevel;
}

void TypeNode::setPointerLevel(unsigned int pointerLevel) {
    m_PointerLevel = pointerLevel;
}

unsigned int TypeNode::getBits() const {
    return m_Bits;
}

void TypeNode::setBits(unsigned int bits) {
    m_Bits = bits;
}

const std::shared_ptr<Token> &TypeNode::getTypeToken() const {
    return m_TypeToken;
}

void TypeNode::setTypeToken(const std::shared_ptr<Token> &typeToken) {
    m_TypeToken = typeToken;
}

bool TypeNode::isSigned() const {
    return mb_Signed;
}

void TypeNode::setSigned(bool isSigned) {
    mb_Signed = isSigned;
}

bool TypeNode::isFloating() const {
    return mb_Floating;
}

void TypeNode::setFloating(bool floating) {
    mb_Floating = floating;
}

std::ostream &operator<<(std::ostream &out, const std::shared_ptr<TypeNode> &typeNode) {
    if (typeNode == nullptr) {
        out << "null";
        return out;
    }

    out << "targetStruct: " << typeNode->getTargetStruct()
        << ", pointerLevel: " << typeNode->getPointerLevel()
        << ", bits: " << typeNode->getBits()
        << ", isSigned: " << std::boolalpha << typeNode->isSigned() << std::dec
        << ", isFloating: " << std::boolalpha << typeNode->isFloating() << std::dec;
    return out;
}

bool TypeNode::operator==(const TypeNode &other) const {
    return (m_TargetStruct == other.getTargetStruct()) &&
           (m_PointerLevel == other.getPointerLevel()) &&
           (m_Bits == other.getBits()) &&
           (mb_Signed == other.isSigned()) &&
           (mb_Floating == other.isFloating());
}

bool TypeNode::operator!=(const TypeNode &other) const {
    return !(other == *this);
}


/* ----------======== FUNCTION-NODE ========---------- */

FunctionNode::FunctionNode()
        : mb_Variadic(false), mb_Native(false),
          m_Parameters({}), m_Block(nullptr),
          m_Name(nullptr), m_InlinedFunctionCall(nullptr),
          m_EntryBlock(nullptr), m_Annotations({}) {
    setKind(ASTNode::FUNCTION);
}

FunctionNode::FunctionNode(const FunctionNode &other)
        : TypedNode(other),
          mb_Variadic(other.mb_Variadic), mb_Native(other.mb_Native),
          m_Parameters({}), m_Block(nullptr),
          m_Name(nullptr), m_InlinedFunctionCall(nullptr),
          m_EntryBlock(nullptr), m_Annotations({}) {
    for (const auto &parameter : other.m_Parameters)
        m_Parameters.push_back(std::shared_ptr<ParameterNode>(parameter->clone(
                std::shared_ptr<FunctionNode>(this),
                this->getScope())));
    for (const auto &annotation : other.m_Annotations)
        m_Annotations.emplace(annotation);

    if (other.m_Block)
        m_Block = std::shared_ptr<BlockNode>(other.m_Block->clone(
                std::shared_ptr<FunctionNode>(this),
                this->getScope()));
    if (other.m_Name)
        m_Name = std::make_shared<Token>(*other.m_Name);
    if (other.m_InlinedFunctionCall)
        m_InlinedFunctionCall = std::shared_ptr<FunctionCallNode>(other.m_InlinedFunctionCall->clone(
                std::shared_ptr<FunctionNode>(this),
                this->getScope()));
}

FunctionNode *FunctionNode::clone(std::shared_ptr<ASTNode> parent,
                                  std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new FunctionNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

bool FunctionNode::hasAnnotation(const std::string &annotation) {
    return m_Annotations.find(annotation) != m_Annotations.end();
}

const std::shared_ptr<FunctionCallNode> &FunctionNode::getInlinedFunctionCall() const {
    return m_InlinedFunctionCall;
}

void FunctionNode::setInlinedFunctionCall(const std::shared_ptr<FunctionCallNode> &inlinedFunctionCall) {
    FunctionNode::m_InlinedFunctionCall = inlinedFunctionCall;
}

void FunctionNode::addParameter(const std::shared_ptr<ParameterNode> &parameterNode) {
    m_Parameters.push_back(parameterNode);
}

const std::vector<std::shared_ptr<ParameterNode>> &FunctionNode::getParameters() const {
    return m_Parameters;
}

void FunctionNode::setAnnotations(const std::set<std::string> &annotations) {
    m_Annotations = annotations;
}

const std::shared_ptr<BlockNode> &FunctionNode::getBlock() const {
    return m_Block;
}

void FunctionNode::setBlock(const std::shared_ptr<BlockNode> &block) {
    m_Block = block;
}

llvm::BasicBlock *FunctionNode::getEntryBlock() const {
    return m_EntryBlock;
}

void FunctionNode::setEntryBlock(llvm::BasicBlock *entryBlock) {
    m_EntryBlock = entryBlock;
}

const std::shared_ptr<Token> &FunctionNode::getName() const {
    return m_Name;
}

void FunctionNode::setName(const std::shared_ptr<Token> &name) {
    m_Name = name;
}

bool FunctionNode::isVariadic() const {
    return mb_Variadic;
}

void FunctionNode::setVariadic(bool variadic) {
    mb_Variadic = variadic;
}

bool FunctionNode::isNative() const {
    return mb_Native;
}

void FunctionNode::setNative(bool native) {
    mb_Native = native;
}

bool FunctionNode::operator==(const FunctionNode &other) const {
    if (m_Name->getContent() != other.m_Name->getContent())
        return false;

    if (m_Parameters.size() != other.m_Parameters.size())
        return false;

    for (auto index = 0; index < other.m_Parameters.size(); index++) {
        auto otherParameter = other.m_Parameters[index];
        auto ownParameter = m_Parameters[index];

        if (*otherParameter->getType() != *ownParameter->getType())
            return false;
    }

    return true;
}

bool FunctionNode::operator!=(const FunctionNode &other) const {
    return !(other == *this);
}


/* ----------======== STRUCT-ARGUMENT-NODE ========---------- */

StructArgumentNode::StructArgumentNode()
        : m_Name(nullptr), m_Expression(nullptr) {
    setKind(ASTNode::STRUCT_ARGUMENT);
}

StructArgumentNode::StructArgumentNode(const StructArgumentNode &other)
        : TypedNode(other),
          m_Name(nullptr), m_Expression(nullptr) {
    if (other.m_Name)
        m_Name = std::make_shared<Token>(*other.m_Name);
    if (other.m_Expression)
        m_Expression = std::shared_ptr<OperableNode>(other.m_Expression->clone(
                std::shared_ptr<StructArgumentNode>(this),
                this->getScope()));
}

StructArgumentNode *StructArgumentNode::clone(std::shared_ptr<ASTNode> parent,
                                              std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new StructArgumentNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

const std::shared_ptr<OperableNode> &StructArgumentNode::getExpression() const {
    return m_Expression;
}

void StructArgumentNode::setExpression(const std::shared_ptr<OperableNode> &expression) {
    m_Expression = expression;
}

const std::shared_ptr<Token> &StructArgumentNode::getName() const {
    return m_Name;
}

void StructArgumentNode::setName(const std::shared_ptr<Token> &name) {
    m_Name = name;
}


/* ----------======== STRUCT-CREATE-NODE ========---------- */

StructCreateNode::StructCreateNode()
        : m_Identifier(nullptr), m_Arguments({}),
          mb_Unnamed(false) {
    setKind(ASTNode::STRUCT_CREATE);
}

StructCreateNode::StructCreateNode(const StructCreateNode &other)
        : OperableNode(other),
          m_Identifier(nullptr), m_Arguments({}),
          mb_Unnamed(other.mb_Unnamed) {
    for (const auto &argument : other.m_Arguments)
        m_Arguments.push_back(std::shared_ptr<StructArgumentNode>(argument->clone(
                std::shared_ptr<StructCreateNode>(this),
                this->getScope())));

    if (other.m_Identifier)
        m_Identifier = std::shared_ptr<IdentifierNode>(other.m_Identifier->clone(
                std::shared_ptr<StructCreateNode>(this),
                this->getScope()));
}

StructCreateNode *StructCreateNode::clone(std::shared_ptr<ASTNode> parent,
                                          std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new StructCreateNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

void StructCreateNode::addArgument(const std::shared_ptr<StructArgumentNode> &argumentNode) {
    m_Arguments.push_back(argumentNode);
}

void StructCreateNode::insertArgument(int index, const std::shared_ptr<StructArgumentNode> &argumentNode) {
    m_Arguments.insert(m_Arguments.begin() + index, argumentNode);
}

void StructCreateNode::removeArgument(int index) {
    m_Arguments.erase(m_Arguments.begin() + index);
}

const std::shared_ptr<IdentifierNode> &StructCreateNode::getIdentifier() const {
    return m_Identifier;
}

void StructCreateNode::setIdentifier(const std::shared_ptr<IdentifierNode> &mIdentifier) {
    m_Identifier = mIdentifier;
}

const std::vector<std::shared_ptr<StructArgumentNode>> &StructCreateNode::getArguments() const {
    return m_Arguments;
}

bool StructCreateNode::isUnnamed() const {
    return mb_Unnamed;
}

void StructCreateNode::setUnnamed(bool unnamed) {
    mb_Unnamed = unnamed;
}


/* ----------======== FUNCTION-CALL-NODE ========---------- */

FunctionCallNode::FunctionCallNode()
        : m_Arguments({}) {
    setKind(ASTNode::FUNCTION_CALL);
}

FunctionCallNode::FunctionCallNode(const FunctionCallNode &other)
        : IdentifierNode(other),
          m_Arguments({}) {
    for (const auto &argument : other.m_Arguments)
        m_Arguments.push_back(std::shared_ptr<FunctionArgumentNode>(argument->clone(
                std::shared_ptr<FunctionCallNode>(this),
                this->getScope())));
}

FunctionCallNode *FunctionCallNode::clone(std::shared_ptr<ASTNode> parent,
                                          std::shared_ptr<SymbolTable> symbolTable) const {
    auto node = new FunctionCallNode(*this);
    node->setParent(parent);
    node->setScope(symbolTable);
    return node;
}

bool FunctionCallNode::getSortedArguments(const std::shared_ptr<FunctionNode> &functionNode,
                                          std::vector<std::shared_ptr<FunctionArgumentNode>> &sortedArguments) {
    auto scopeCheck = [](const std::shared_ptr<ASTNode> &node) {
        return node->getKind() == PARAMETER;
    };

    for (const auto &argument : m_Arguments) {
        if (argument->getName() == nullptr)
            continue;

        auto foundParameters = functionNode->getScope()->scope(argument->getName()->getContent(), scopeCheck);
        if (foundParameters == nullptr)
            return false;

        auto foundParameter = std::static_pointer_cast<ParameterNode>(foundParameters->at(0));
        auto parameterIndex = Utils::indexOf(functionNode->getParameters(), foundParameter).second;
        auto argumentIndex = Utils::indexOf(sortedArguments, argument).second;
        sortedArguments.erase(sortedArguments.begin() + argumentIndex);
        sortedArguments.insert(sortedArguments.begin() + parameterIndex, argument);
    }

    return true;
}

void FunctionCallNode::addArgument(const std::shared_ptr<FunctionArgumentNode> &argumentNode) {
    m_Arguments.push_back(argumentNode);
}

const std::vector<std::shared_ptr<FunctionArgumentNode>> &FunctionCallNode::getArguments() const {
    return m_Arguments;
}