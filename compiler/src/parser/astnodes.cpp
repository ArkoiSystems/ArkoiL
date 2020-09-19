//
// Created by timo on 9/7/20.
//

#include "astnodes.h"

#include "../lexer/token.h"
#include "symboltable.h"
#include "../utils.h"

/* ----------======== AST-NODE ========---------- */

ASTNode::ASTNode()
        : m_Kind(ASTNode::NONE), mb_Failed(false), m_StartToken({}),
          m_EndToken({}), m_Parent({}), m_Scope({}) {}

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

ASTNode::ASTKind ASTNode::getKind() const {
    return m_Kind;
}

void ASTNode::setKind(ASTNode::ASTKind kind) {
    m_Kind = kind;
}


/* ----------======== TYPED-NODE ========---------- */

TypedNode::TypedNode() : mb_TypeResolved(false), m_Type({}) {}

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


/* ----------======== IMPORT-NODE ========---------- */

ImportNode::ImportNode() : m_Target({}), m_Path({}) {
    setKind(ASTNode::IMPORT);
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

RootNode::RootNode() : m_SourceCode({}), m_SourcePath({}) {
    setKind(ASTNode::ROOT);
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

ParameterNode::ParameterNode() : m_Name({}) {
    setKind(ASTNode::PARAMETER);
}

const std::shared_ptr<Token> &ParameterNode::getName() const {
    return m_Name;
}

void ParameterNode::setName(const std::shared_ptr<Token> &name) {
    m_Name = name;
}


/* ----------======== BLOCK-NODE ========---------- */

BlockNode::BlockNode() {
    setKind(ASTNode::BLOCK);
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

OperableNode::OperableNode(const OperableNode &other) : TypedNode(other) {
    setKind(ASTNode::OPERABLE);
}


/* ----------======== VARIABLE-NODE ========---------- */

VariableNode::VariableNode()
        : mb_Constant(false), mb_Local(false),
          m_Expression({}), m_Name({}) {
    setKind(ASTNode::VARIABLE);
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
          m_Lhs({}), m_Rhs({}) {
    setKind(ASTNode::BINARY);
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

BinaryNode::BinaryKind BinaryNode::getOperatorKind() const {
    return m_OperatorKind;
}

void BinaryNode::setOperatorKind(BinaryNode::BinaryKind operatorKind) {
    m_OperatorKind = operatorKind;
}


/* ----------======== UNARY-NODE ========---------- */

UnaryNode::UnaryNode() : m_OperatorKind(UnaryNode::NONE), m_Expression({}) {
    setKind(ASTNode::UNARY);
}

const std::shared_ptr<OperableNode> &UnaryNode::getExpression() const {
    return m_Expression;
}

void UnaryNode::setExpression(const std::shared_ptr<OperableNode> &expression) {
    m_Expression = expression;
}

UnaryNode::UnaryKind UnaryNode::getOperatorKind() const {
    return m_OperatorKind;
}

void UnaryNode::setOperatorKind(UnaryNode::UnaryKind operatorKind) {
    m_OperatorKind = operatorKind;
}


/* ----------======== PARENTHESIZED-NODE ========---------- */

ParenthesizedNode::ParenthesizedNode() : m_Expression({}) {
    setKind(ASTNode::PARENTHESIZED);
}

const std::shared_ptr<OperableNode> &ParenthesizedNode::getExpression() const {
    return m_Expression;
}

void ParenthesizedNode::setExpression(const std::shared_ptr<OperableNode> &expression) {
    m_Expression = expression;
}


/* ----------======== NUMBER-NODE ========---------- */

NumberNode::NumberNode() : m_Number({}) {
    setKind(ASTNode::NUMBER);
}

const std::shared_ptr<Token> &NumberNode::getNumber() const {
    return m_Number;
}

void NumberNode::setNumber(const std::shared_ptr<Token> &number) {
    m_Number = number;
}


/* ----------======== STRING-NODE ========---------- */

StringNode::StringNode() : m_String({}) {
    setKind(ASTNode::STRING);
}

const std::shared_ptr<Token> &StringNode::getString() const {
    return m_String;
}

void StringNode::setString(const std::shared_ptr<Token> &string) {
    m_String = string;
}


/* ----------======== ARGUMENT-NODE ========---------- */

ArgumentNode::ArgumentNode() : m_Expression({}), m_Name({}) {
    setKind(ASTNode::ARGUMENT);
}

const std::shared_ptr<OperableNode> &ArgumentNode::getExpression() const {
    return m_Expression;
}

void ArgumentNode::setExpression(const std::shared_ptr<OperableNode> &expression) {
    m_Expression = expression;
}

const std::shared_ptr<Token> &ArgumentNode::getName() const {
    return m_Name;
}

void ArgumentNode::setName(const std::shared_ptr<Token> &name) {
    m_Name = name;
}


/* ----------======== IDENTIFIER-NODE ========---------- */

IdentifierNode::IdentifierNode()
        : mb_Dereference(false), mb_Pointer(false),
          m_NextIdentifier({}), m_LastIdentifier({}),
          m_Identifier({}) {
    setKind(ASTNode::IDENTIFIER);
}

IdentifierNode::IdentifierNode(const IdentifierNode &other) : OperableNode(other) {
    m_NextIdentifier = other.m_NextIdentifier;
    mb_Dereference = other.mb_Dereference;
    m_Identifier = other.m_Identifier;
    mb_Pointer = other.mb_Pointer;

    setKind(ASTNode::IDENTIFIER);
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
        : m_StartIdentifier({}), m_EndIdentifier({}),
          m_Expression({}) {
    setKind(ASTNode::ASSIGNMENT);
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

ReturnNode::ReturnNode() : m_Expression({}) {
    setKind(ASTNode::RETURN);
}

const std::shared_ptr<OperableNode> &ReturnNode::getExpression() const {
    return m_Expression;
}

void ReturnNode::setExpression(const std::shared_ptr<OperableNode> &expression) {
    m_Expression = expression;
}


/* ----------======== STRUCT-NODE ========---------- */

StructNode::StructNode() : m_Variables({}), m_Name({}) {
    setKind(ASTNode::STRUCT);
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
          m_TargetStruct({}), m_TypeToken({}),
          m_PointerLevel(0), m_Bits(0) {
    setKind(ASTNode::TYPE);
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
          m_Parameters({}), m_Block({}),
          m_Name({}), m_InlinedFunctionCall({}),
          m_EntryBlock(nullptr) {
    setKind(ASTNode::FUNCTION);
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


/* ----------======== STRUCT-CREATE-NODE ========---------- */

StructCreateNode::StructCreateNode()
        : m_StartIdentifier({}), m_EndIdentifier({}),
          m_Arguments({}) {
    setKind(ASTNode::STRUCT_CREATE);
}

bool StructCreateNode::getFilledExpressions(const std::shared_ptr<StructNode> &structNode,
                                            std::vector<std::shared_ptr<OperableNode>> &expressions) {
    for (const auto &variable : structNode->getVariables()) {
        std::shared_ptr<ArgumentNode> foundNode;
        for (const auto &argument : m_Arguments) {
            if (variable->getName()->getContent() == argument->getName()->getContent()) {
                foundNode = argument;
                break;
            }
        }

        if (foundNode == nullptr) {
            expressions.emplace_back(variable->getExpression());
        } else {
            expressions.emplace_back(foundNode->getExpression());
        }
    }

    return false;
}

void StructCreateNode::addArgument(const std::shared_ptr<ArgumentNode> &argumentNode) {
    m_Arguments.push_back(argumentNode);
}

const std::shared_ptr<IdentifierNode> &StructCreateNode::getStartIdentifier() const {
    return m_StartIdentifier;
}

void StructCreateNode::setStartIdentifier(const std::shared_ptr<IdentifierNode> &startIdentifier) {
    m_StartIdentifier = startIdentifier;
}

const std::shared_ptr<IdentifierNode> &StructCreateNode::getEndIdentifier() const {
    return m_EndIdentifier;
}

void StructCreateNode::setEndIdentifier(const std::shared_ptr<IdentifierNode> &endIdentifier) {
    m_EndIdentifier = endIdentifier;
}

const std::vector<std::shared_ptr<ArgumentNode>> &StructCreateNode::getArguments() const {
    return m_Arguments;
}


/* ----------======== FUNCTION-CALL-NODE ========---------- */

FunctionCallNode::FunctionCallNode() : m_Arguments({}) {
    setKind(ASTNode::FUNCTION_CALL);
}

FunctionCallNode::FunctionCallNode(const IdentifierNode &other) : IdentifierNode(other) {
    setKind(ASTNode::FUNCTION_CALL);
}

bool FunctionCallNode::getSortedArguments(const std::shared_ptr<FunctionNode> &functionNode,
                                          std::vector<std::shared_ptr<ArgumentNode>> &sortedArguments) {
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

void FunctionCallNode::addArgument(const std::shared_ptr<ArgumentNode> &argumentNode) {
    m_Arguments.push_back(argumentNode);
}

const std::vector<std::shared_ptr<ArgumentNode>> &FunctionCallNode::getArguments() const {
    return m_Arguments;
}