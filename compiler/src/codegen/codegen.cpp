#include "codegen.h"

//
// Created by timo on 8/13/20.
//

#include "../parser/astnodes.h"

#include <iostream>
#include <utility>

#include "../semantic/typeresolver.h"
#include "../semantic/typecheck.h"
#include "../semantic/scopecheck.h"
#include "../parser/symboltable.h"
#include "../semantic/inliner.h"
#include "../compiler/error.h"
#include "../lexer/lexer.h"
#include "../lexer/token.h"
#include "../utils/utils.h"

CodeGen::CodeGen(std::string moduleName)
        : m_CurrentBlock(nullptr), m_Builder({m_Context}),
          m_Module(nullptr), m_ModuleName(std::move(moduleName)) {}

void *CodeGen::visit(const SharedASTNode &node) {
    if (node->getKind() == ASTNode::ROOT) {
        CodeGen::visit(std::static_pointer_cast<RootNode>(node));
        return nullptr;
    } else if (node->getKind() == ASTNode::FUNCTION) {
        return CodeGen::visit(std::static_pointer_cast<FunctionNode>(node));
    } else if (node->getKind() == ASTNode::RETURN) {
        return CodeGen::visit(std::static_pointer_cast<ReturnNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT) {
        return CodeGen::visit(std::static_pointer_cast<StructNode>(node));
    } else if (node->getKind() == ASTNode::TYPE) {
        return CodeGen::visit(std::static_pointer_cast<TypeNode>(node));
    } else if (node->getKind() == ASTNode::BLOCK) {
        return CodeGen::visit(std::static_pointer_cast<BlockNode>(node));
    } else if (node->getKind() == ASTNode::NUMBER) {
        return CodeGen::visit(std::static_pointer_cast<NumberNode>(node));
    } else if (node->getKind() == ASTNode::STRING) {
        return CodeGen::visit(std::static_pointer_cast<StringNode>(node));
    } else if (node->getKind() == ASTNode::UNARY) {
        return CodeGen::visit(std::static_pointer_cast<UnaryNode>(node));
    } else if (node->getKind() == ASTNode::PARAMETER) {
        return CodeGen::visit(std::static_pointer_cast<ParameterNode>(node));
    } else if (node->getKind() == ASTNode::FUNCTION_ARGUMENT) {
        return CodeGen::visit(std::static_pointer_cast<FunctionArgumentNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT_ARGUMENT) {
        auto structArgumentNode = std::static_pointer_cast<StructArgumentNode>(node);

        auto structCreateNode = std::static_pointer_cast<StructCreateNode>(
                structArgumentNode->getParent());
        auto argumentIndex = Utils::indexOf(structCreateNode->getArguments(),
                                            structArgumentNode).second;
        auto structVariable = CodeGen::visit(structCreateNode);

        return CodeGen::visit(structArgumentNode, structVariable, argumentIndex);
    } else if (node->getKind() == ASTNode::IDENTIFIER) {
        auto identifierNode = std::static_pointer_cast<IdentifierNode>(node);

        SharedIdentifierNode firstIdentifier = identifierNode;
        while (firstIdentifier->getLastIdentifier() != nullptr)
            firstIdentifier = firstIdentifier->getLastIdentifier();

        return CodeGen::visit(firstIdentifier);
    } else if (node->getKind() == ASTNode::ASSIGNMENT) {
        return CodeGen::visit(std::static_pointer_cast<AssignmentNode>(node));
    } else if (node->getKind() == ASTNode::VARIABLE) {
        return CodeGen::visit(std::static_pointer_cast<VariableNode>(node));
    } else if (node->getKind() == ASTNode::BINARY) {
        return CodeGen::visit(std::static_pointer_cast<BinaryNode>(node));
    } else if (node->getKind() == ASTNode::FUNCTION_CALL) {
        return CodeGen::visit(std::static_pointer_cast<FunctionCallNode>(node));
    } else if (node->getKind() == ASTNode::PARENTHESIZED) {
        return CodeGen::visit(std::static_pointer_cast<ParenthesizedNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT_CREATE) {
        return CodeGen::visit(std::static_pointer_cast<StructCreateNode>(node));
    } else if (node->getKind() != ASTNode::IMPORT) {
        THROW_NODE_ERROR(node, "CodeGen: Unsupported node: " + node->getKindAsString())
        exit(EXIT_FAILURE);
    }

    return nullptr;
}

void CodeGen::visit(const SharedRootNode &rootNode) {
    m_Module = std::make_shared<llvm::Module>(m_ModuleName, m_Context);

    m_ScopedNodes.emplace_back();
    defer(m_ScopedNodes.erase(m_ScopedNodes.end()));

    for (const auto &node : rootNode->getNodes())
        CodeGen::visit(node);
}

llvm::Value *CodeGen::visit(const SharedFunctionNode &functionNode) {
    if (functionNode->hasAnnotation("inlined")) {
        std::cout << "This should not have happened. "
                     "Report this bug with a reconstruction of it." << std::endl;
        abort();
    }

    auto functionIterator = m_ScopedNodes[0].find(functionNode);
    if (functionIterator != m_ScopedNodes[0].end()) {
        if (!std::holds_alternative<llvm::Function *>(functionIterator->second)) {
            std::cout << "This should not have happened. "
                         "Report this bug with a reconstruction of it." << std::endl;
            abort();
        }

        return std::get<llvm::Function *>(functionIterator->second);
    }

    m_ScopedNodes.emplace_back();
    defer(m_ScopedNodes.erase(m_ScopedNodes.end()));

    std::vector<llvm::Type *> functionParameters;
    auto returnType = CodeGen::visit(functionNode->getType());
    if (functionNode->getType()->getTargetStruct() != nullptr) {
        functionParameters.emplace_back(returnType->getPointerTo());
        returnType = llvm::Type::getVoidTy(m_Context);
    }

    for (auto const &parameter : functionNode->getParameters())
        functionParameters.emplace_back(CodeGen::visit(parameter->getType()));

    auto functionType = llvm::FunctionType::get(returnType, functionParameters,
                                                functionNode->isVariadic());
    llvm::Function *functionRef = llvm::Function::Create(functionType,
                                                         llvm::Function::ExternalLinkage,
                                                         functionNode->getName()->getContent(),
                                                         *m_Module);

    if (functionNode->getType()->getTargetStruct() != nullptr) {
        auto returnArgument = functionRef->getArg(0);
        returnArgument->addAttr(llvm::Attribute::StructRet);
        returnArgument->addAttr(llvm::Attribute::NoAlias);
    }

    m_ScopedNodes[0].emplace(functionNode, functionRef);

    if (functionNode->isNative())
        return functionRef;

    CodeGen::visit(functionNode->getBlock());
    return functionRef;
}

llvm::Type *CodeGen::visit(const SharedTypeNode &typeNode) {
    llvm::Type *type;
    if (typeNode->isNumeric() && typeNode->isFloating()) {
        type = typeNode->getBits() == 32 ? llvm::Type::getFloatTy(m_Context)
                                         : llvm::Type::getDoubleTy(m_Context);
    } else if (typeNode->isNumeric() && !typeNode->isFloating()) {
        type = llvm::Type::getIntNTy(m_Context, typeNode->getBits());
    } else if (typeNode->getTargetStruct() != nullptr) {
        type = CodeGen::visit(typeNode->getTargetStruct());
    } else if (typeNode->getBits() == 0) {
        type = llvm::Type::getVoidTy(m_Context);
    } else {
        THROW_NODE_ERROR(typeNode, "CodeGen: Unsupported type node: " + typeNode->getKindAsString())
        exit(EXIT_FAILURE);
    }

    for (auto index = 0; index < typeNode->getPointerLevel(); index++)
        type = type->getPointerTo();
    return type;
}

llvm::Type *CodeGen::visit(const SharedStructNode &structNode) {
    auto structIterator = m_ScopedNodes[0].find(structNode);
    if (structIterator != m_ScopedNodes[0].end()) {
        if (!std::holds_alternative<llvm::StructType *>(structIterator->second)) {
            std::cout << "This should not have happened. "
                         "Report this bug with a reconstruction of it." << std::endl;
            abort();
        }

        return std::get<llvm::StructType *>(structIterator->second);
    }

    auto structRef = llvm::StructType::create(m_Context, structNode->getName()->getContent());
    m_ScopedNodes[0].emplace(structNode, structRef);

    std::vector<llvm::Type *> types;
    for (auto const &variable : structNode->getVariables())
        types.emplace_back(CodeGen::visit(variable->getType()));
    structRef->setBody(types, false);

    return structRef;
}

// TODO: Issue 4
// TODO: Issue 6
llvm::Value *CodeGen::visit(const SharedParameterNode &parameterNode) {
    auto parameterIterator = m_ScopedNodes.back().find(parameterNode);
    if (parameterIterator != m_ScopedNodes.back().end()) {
        if (!std::holds_alternative<llvm::Value *>(parameterIterator->second)) {
            std::cout << "This should not have happened. "
                         "Report this bug with a reconstruction of it." << std::endl;
            abort();
        }

        return std::get<llvm::Value *>(parameterIterator->second);
    }

    auto functionNode = parameterNode->findNodeOfParents<FunctionNode>();
    auto functionRef = reinterpret_cast<llvm::Function *>(CodeGen::visit(functionNode));

    llvm::Value *parameter = nullptr;
    for (auto index = 0; index < functionNode->getParameters().size(); index++) {
        auto targetParameter = functionNode->getParameters().at(index);
        if (targetParameter->getName()->getContent() == parameterNode->getName()->getContent()) {
            parameter = functionRef->getArg(index);
            break;
        }
    }

    if (parameter == nullptr) {
        THROW_NODE_ERROR(parameterNode, "No target value found for this parameter.")
        exit(EXIT_FAILURE);
    }

    auto parameterVariable = m_Builder.CreateAlloca(CodeGen::visit(parameterNode->getType()));
    m_ScopedNodes.back().emplace(parameterNode, parameterVariable);

    m_Builder.CreateStore(parameter, parameterVariable);

    return parameterVariable;
}

llvm::BasicBlock *CodeGen::visit(const SharedBlockNode &blockNode) {
    auto blockIterator = m_ScopedNodes.back().find(blockNode);
    if (blockIterator != m_ScopedNodes.back().end()) {
        if (!std::holds_alternative<BlockDetails>(blockIterator->second)) {
            std::cout << "This should not have happened. "
                         "Report this bug with a reconstruction of it." << std::endl;
            abort();
        }

        return std::get<0>(std::get<BlockDetails>(blockIterator->second));
    }

    auto functionNode = std::static_pointer_cast<FunctionNode>(blockNode->getParent());
    auto functionRef = reinterpret_cast<llvm::Function *>(CodeGen::visit(functionNode));

    auto isEntryBlock = functionNode == blockNode->getParent();
    auto startBlock = llvm::BasicBlock::Create(m_Context, isEntryBlock ? "entry" : "", functionRef);

    llvm::BasicBlock *returnBlock = nullptr;
    llvm::Value *returnVariable = nullptr;
    auto lastBlock = m_CurrentBlock;

    auto hasReturn = false;
    for (const auto &node : blockNode->getNodes()) {
        if (node->getKind() != ASTNode::RETURN)
            continue;
        hasReturn = true;
        break;
    }

    CodeGen::setPositionAtEnd(startBlock);

    if (functionNode == blockNode->getParent()) {
        if (!functionNode->getType()->isVoid() &&
            functionNode->getType()->getTargetStruct() == nullptr)
            returnVariable = m_Builder.CreateAlloca(CodeGen::visit(functionNode->getType()),
                                                    nullptr, "var_ret");

        returnBlock = llvm::BasicBlock::Create(m_Context, "return", functionRef);

        CodeGen::setPositionAtEnd(returnBlock);

        if (!functionNode->getType()->isVoid() &&
            functionNode->getType()->getTargetStruct() == nullptr) {
            auto loadedVariable = m_Builder.CreateLoad(returnVariable, "loaded_ret");
            m_Builder.CreateRet(loadedVariable);
        } else
            m_Builder.CreateRetVoid();

        CodeGen::setPositionAtEnd(startBlock);
    }

    auto tuple = std::make_tuple(startBlock, returnVariable, returnBlock);
    m_ScopedNodes.back().emplace(blockNode, tuple);

    for (const auto &node : blockNode->getNodes())
        CodeGen::visit(node);

    if (!hasReturn && functionNode == blockNode->getParent())
        m_Builder.CreateBr(returnBlock);

    if (functionNode == blockNode->getParent()) {
        auto lastBasicBlock = functionRef->end() == functionRef->begin()
                              ? nullptr
                              : &*--functionRef->end();
        returnBlock->moveAfter(lastBasicBlock);
    }

    if (lastBlock != nullptr)
        setPositionAtEnd(lastBlock);

    return startBlock;
}

llvm::Value *CodeGen::visit(const SharedReturnNode &returnNode) {
    auto functionNode = returnNode->findNodeOfParents<FunctionNode>();

    auto blockIterator = m_ScopedNodes.back().find(returnNode->findNodeOfParents<BlockNode>());
    if (blockIterator == m_ScopedNodes.back().end()) {
        THROW_NODE_ERROR(functionNode->getBlock(), "Couldn't find the block details for the return "
                                                   "statement.")
        exit(EXIT_FAILURE);
    }

    if (!std::holds_alternative<BlockDetails>(blockIterator->second)) {
        std::cout << "This should not have happened. "
                     "Report this bug with a reconstruction of it." << std::endl;
        abort();
    }

    auto blockDetails = std::get<BlockDetails>(blockIterator->second);
    if (functionNode->getType()->getBits() == 0 &&
        functionNode->getType()->getTargetStruct() == nullptr) {
        m_Builder.CreateBr(std::get<2>(blockDetails));

        return llvm::UndefValue::get(llvm::Type::getVoidTy(m_Context));
    } else {
        auto expression = static_cast<llvm::Value *>(CodeGen::visit(returnNode->getExpression()));
        if (returnNode->getType()->getTargetStruct() == nullptr) {
            auto returnVariable = std::get<1>(blockDetails);
            m_Builder.CreateStore(expression, returnVariable);
        }

        m_Builder.CreateBr(std::get<2>(blockDetails));
        return expression;
    }
}

llvm::Value *CodeGen::visit(const SharedAssignmentNode &assignmentNode) {
    auto variableRef = CodeGen::visit(assignmentNode->getStartIdentifier());
    auto expression = static_cast<llvm::Value *>(CodeGen::visit(assignmentNode->getExpression()));

    m_Builder.CreateStore(expression, variableRef);
    return expression;
}

llvm::Value *CodeGen::visit(const SharedNumberNode &numberNode) {
    if (!numberNode->getType()->isFloating()) {
        auto value = std::stoi(numberNode->getNumber()->getContent());
        return llvm::ConstantInt::get(CodeGen::visit(numberNode->getType()), value,
                                      numberNode->getType()->isSigned());
    }

    return llvm::ConstantFP::get(CodeGen::visit(numberNode->getType()),
                                 numberNode->getNumber()->getContent());
}

llvm::Value *CodeGen::visit(const SharedStringNode &stringNode) {
    auto stringConstant = llvm::ConstantDataArray::getString(
            m_Context, stringNode->getString()->getContent());

    auto stringVariable = new llvm::GlobalVariable(*m_Module, stringConstant->getType(), false,
                                                   llvm::GlobalVariable::PrivateLinkage,
                                                   stringConstant);

    std::vector<llvm::Constant *> indices;
    indices.emplace_back(llvm::ConstantInt::get(llvm::Type::getIntNTy(m_Context, 32), 0, true));
    indices.emplace_back(llvm::ConstantInt::get(llvm::Type::getIntNTy(m_Context, 32), 0, true));
    return llvm::ConstantExpr::getInBoundsGetElementPtr(stringConstant->getType(), stringVariable,
                                                        indices);
}

llvm::Value *CodeGen::visit(const SharedUnaryNode &unaryNode) {
    auto expression = static_cast<llvm::Value *>(CodeGen::visit(unaryNode->getExpression()));
    if (unaryNode->getOperatorKind() == UnaryNode::NEGATE) {
        if (unaryNode->getType()->isFloating())
            return m_Builder.CreateFNeg(expression);
        return m_Builder.CreateNeg(expression);
    }

    THROW_NODE_ERROR(unaryNode, "CodeGen: Unsupported unary node: " +
                                unaryNode->getOperatorKindAsString())
    exit(EXIT_FAILURE);
}

llvm::Value *CodeGen::visit(const SharedParenthesizedNode &parenthesizedNode) {
    return static_cast<llvm::Value *>(CodeGen::visit(parenthesizedNode->getExpression()));
}

llvm::Value *CodeGen::visit(const SharedIdentifierNode &identifierNode) {
    auto typedTarget = std::dynamic_pointer_cast<TypedNode>(identifierNode->getTargetNode());
    if (typedTarget == nullptr) {
        THROW_NODE_ERROR(identifierNode->getTargetNode(), "Can't use a non-typed node as an "
                                                          "identifier.")
        exit(EXIT_FAILURE);
    }

    if (identifierNode->getKind() == ASTNode::FUNCTION_CALL)
        typedTarget = std::static_pointer_cast<FunctionCallNode>(identifierNode);

    auto targetValue = static_cast<llvm::Value *>(CodeGen::visit(typedTarget));
    if (identifierNode->getKind() == ASTNode::FUNCTION_CALL
        && identifierNode->getNextIdentifier() != nullptr) {
        auto tempVariable = m_Builder.CreateAlloca(CodeGen::visit(identifierNode->getType()));
        m_Builder.CreateStore(targetValue, tempVariable);
        targetValue = tempVariable;
    }

    if (identifierNode->isDereference()) {
        targetValue = m_Builder.CreateLoad(targetValue);
    } else if (identifierNode->isPointer()) {
        auto instruction = reinterpret_cast<llvm::Instruction *>(targetValue);
        if (instruction->getOpcode() != llvm::Instruction::GetElementPtr)
            targetValue = m_Builder.CreateGEP(targetValue, std::vector<llvm::Value *>(0));
    }

    auto targetStruct = identifierNode->getType()->getTargetStruct();
    auto currentIdentifier = identifierNode;
    while (currentIdentifier->getNextIdentifier() != nullptr) {
        currentIdentifier = currentIdentifier->getNextIdentifier();

        auto variableNode = std::dynamic_pointer_cast<VariableNode>(
                currentIdentifier->getTargetNode());
        typedTarget = variableNode;

        if (variableNode == nullptr) {
            THROW_NODE_ERROR(identifierNode->getTargetNode(), "Can't use a non-variable node as an "
                                                              "sub identifier.")
            exit(EXIT_FAILURE);
        }

        auto variableIndex = Utils::indexOf(targetStruct->getVariables(), variableNode).second;
        targetValue = m_Builder.CreateStructGEP(targetValue, variableIndex);

        if (currentIdentifier->isDereference()) {
            targetValue = m_Builder.CreateLoad(targetValue);
        } else if (currentIdentifier->isPointer()) {
            auto instruction = reinterpret_cast<llvm::Instruction *>(targetValue);
            if (instruction->getOpcode() != llvm::Instruction::GetElementPtr)
                targetValue = m_Builder.CreateGEP(targetValue, std::vector<llvm::Value *>(0));
        }

        targetStruct = typedTarget->getType()->getTargetStruct();
    }

    auto assignmentNode = std::dynamic_pointer_cast<AssignmentNode>(identifierNode->getParent());
    auto isAssignmentExpression = assignmentNode
                                  && assignmentNode->getExpression() == identifierNode;

    if((!assignmentNode || isAssignmentExpression) && !currentIdentifier->isPointer())
        return m_Builder.CreateLoad(targetValue);

    return targetValue;
}

llvm::Value *CodeGen::visit(const SharedBinaryNode &binaryNode) {
    if (binaryNode->getOperatorKind() == BinaryNode::BIT_CAST) {
        auto lhsValue = static_cast<llvm::Value *>(CodeGen::visit(binaryNode->getLHS()));
        auto rhsValue = static_cast<llvm::Type *>(CodeGen::visit(binaryNode->getRHS()));
        return m_Builder.CreateBitCast(lhsValue, rhsValue);
    } else {
        auto lhsValue = static_cast<llvm::Value *>(CodeGen::visit(binaryNode->getLHS()));
        auto rhsValue = static_cast<llvm::Value *>(CodeGen::visit(binaryNode->getRHS()));

        auto isFloating = binaryNode->getLHS()->getType()->isFloating() ||
                          binaryNode->getRHS()->getType()->isFloating();
        auto isSigned = binaryNode->getLHS()->getType()->isSigned() ||
                        binaryNode->getRHS()->getType()->isSigned();

        switch (binaryNode->getOperatorKind()) {
            case BinaryNode::ADDITION:
                return CodeGen::makeAdd(isFloating, lhsValue, rhsValue);
            case BinaryNode::MULTIPLICATION:
                return CodeGen::makeMul(isFloating, lhsValue, rhsValue);
            case BinaryNode::DIVISION:
                return CodeGen::makeDiv(isFloating, isSigned, lhsValue, rhsValue);
            case BinaryNode::SUBTRACTION:
                return CodeGen::makeSub(isFloating, lhsValue, rhsValue);
            case BinaryNode::REMAINING:
                return CodeGen::makeRem(isFloating, isSigned, lhsValue, rhsValue);

            case BinaryNode::LESS_THAN:
                return CodeGen::makeLT(isFloating, isSigned, lhsValue, rhsValue);
            case BinaryNode::GREATER_THAN:
                return CodeGen::makeGT(isFloating, isSigned, lhsValue, rhsValue);
            case BinaryNode::LESS_EQUAL_THAN:
                return CodeGen::makeLE(isFloating, isSigned, lhsValue, rhsValue);
            case BinaryNode::GREATER_EQUAL_THAN:
                return CodeGen::makeGE(isFloating, isSigned, lhsValue, rhsValue);

            case BinaryNode::EQUAL:
                return CodeGen::makeEQ(isFloating, lhsValue, rhsValue);
            case BinaryNode::NOT_EQUAL:
                return CodeGen::makeNE(isFloating, lhsValue, rhsValue);

            default:
                THROW_NODE_ERROR(binaryNode, "Unsupported binary node: " +
                                             binaryNode->getOperatorKindAsString())
                exit(EXIT_FAILURE);
        }
    }
}

llvm::Value *CodeGen::visit(const SharedFunctionCallNode &functionCallNode) {
    auto functionNode = std::static_pointer_cast<FunctionNode>(functionCallNode->getTargetNode());
    auto functionRef = CodeGen::visit(functionNode);

    std::vector<llvm::Value *> functionArguments;
    llvm::Value *returnValue;
    if (functionNode->getType()->getTargetStruct() != nullptr) {
        returnValue = m_Builder.CreateAlloca(CodeGen::visit(functionNode->getType()));

        functionArguments.push_back(returnValue);
    }

    auto sortedArguments(functionCallNode->getArguments());
    functionCallNode->getSortedArguments(functionNode, sortedArguments);

    for (const auto &sortedArgument : sortedArguments) {
        auto expression = CodeGen::visit(sortedArgument);
        functionArguments.push_back(expression);
    }

    auto calledValue = m_Builder.CreateCall(functionRef, functionArguments);
    returnValue = functionNode->getType()->getTargetStruct() != nullptr ? returnValue : calledValue;

    return returnValue;
}

llvm::Value *CodeGen::visit(const SharedFunctionArgumentNode &functionArgumentNode) {
    return static_cast<llvm::Value *>(CodeGen::visit(functionArgumentNode->getExpression()));
}

llvm::Value *CodeGen::visit(const SharedStructCreateNode &structCreateNode) {
    auto structCreateIterator = m_ScopedNodes.back().find(structCreateNode);
    if (structCreateIterator != m_ScopedNodes.back().end()) {
        if (!std::holds_alternative<llvm::Value *>(structCreateIterator->second)) {
            std::cout << "This should not have happened. "
                         "Report this bug with a reconstruction of it." << std::endl;
            abort();
        }

        return std::get<llvm::Value *>(structCreateIterator->second);
    }

    auto structNode = std::static_pointer_cast<StructNode>(structCreateNode->getTargetNode());
    auto structRef = CodeGen::visit(structNode);
    auto isReturned = structCreateNode->findNodeOfParents<ReturnNode>();

    llvm::Value *structValue;
    if (isReturned) {
        auto functionNode = structCreateNode->findNodeOfParents<FunctionNode>();
        auto functionRef = reinterpret_cast<llvm::Function *>(CodeGen::visit(functionNode));

        structValue = functionRef->getArg(0);
    } else structValue = m_Builder.CreateAlloca(structRef);

    m_ScopedNodes.back().emplace(structCreateNode, structValue);

    for (auto index = 0; index < structCreateNode->getArguments().size(); index++) {
        auto structArgumentNode = structCreateNode->getArgument(index);
        CodeGen::visit(structArgumentNode, structValue, index);
    }

    if (structCreateNode->findNodeOfParents<VariableNode>() != nullptr)
        return structValue;
    return isReturned ? nullptr : m_Builder.CreateLoad(structValue);
}

// TODO: Issue 13
llvm::Value *CodeGen::visit(const SharedStructArgumentNode &structArgumentNode,
                            llvm::Value *structVariable,
                            int argumentIndex) {
    auto structArgumentIterator = m_ScopedNodes.back().find(structArgumentNode);
    if (structArgumentIterator != m_ScopedNodes.back().end()) {
        if (!std::holds_alternative<std::nullptr_t>(structArgumentIterator->second)) {
            std::cout << "This should not have happened. "
                         "Report this bug with a reconstruction of it." << std::endl;
            abort();
        }

        auto variableGEP = m_Builder.CreateStructGEP(structVariable, argumentIndex);
        return variableGEP;
    }

    m_ScopedNodes.back().emplace(structArgumentNode, nullptr);

    if (structArgumentNode->getExpression() == nullptr &&
        structArgumentNode->getType()->getTargetStruct() != nullptr) {
        auto structNode = structArgumentNode->getType()->getTargetStruct();
        auto structCreateNode = createStructCreate(structNode, structArgumentNode);

        auto currentStructNode = std::static_pointer_cast<StructNode>(
                structArgumentNode->findNodeOfParents<StructCreateNode>()->getTargetNode());
        if (!shouldGenerateGEP(currentStructNode, argumentIndex))
            return nullptr;

        auto variableGEP = m_Builder.CreateStructGEP(structVariable, argumentIndex);
        for (auto index = 0; index < structCreateNode->getArguments().size(); index++) {
            auto childArgumentNode = structCreateNode->getArgument(index);
            CodeGen::visit(childArgumentNode, variableGEP, index);
        }

        return nullptr;
    }

    if (structArgumentNode->getExpression() == nullptr)
        return nullptr;

    if (structArgumentNode->getExpression()->getKind() == ASTNode::STRUCT_CREATE) {
        auto variableGEP = m_Builder.CreateStructGEP(structVariable, argumentIndex);
        auto structCreateNode = std::static_pointer_cast<StructCreateNode>(
                structArgumentNode->getExpression());
        for (auto index = 0; index < structCreateNode->getArguments().size(); index++) {
            auto childArgumentNode = structCreateNode->getArgument(index);
            CodeGen::visit(childArgumentNode, variableGEP, index);
        }
    } else {
        auto variableGEP = m_Builder.CreateStructGEP(structVariable, argumentIndex);
        auto expression = static_cast<llvm::Value *>(CodeGen::visit(
                structArgumentNode->getExpression()));
        m_Builder.CreateStore(expression, variableGEP);
    }

    return nullptr;
}

llvm::Value *CodeGen::visit(const SharedVariableNode &variableNode) {
    if (variableNode->isGlobal()) {
        auto variableIterator = m_ScopedNodes[0].find(variableNode);
        if (variableIterator != m_ScopedNodes[0].end()) {
            if (!std::holds_alternative<llvm::Value *>(variableIterator->second)) {
                std::cout << "This should not have happened. "
                             "Report this bug with a reconstruction of it." << std::endl;
                abort();
            }

            return m_Builder.CreateLoad(std::get<llvm::Value *>(variableIterator->second));
        }

        auto globalVariable = new llvm::GlobalVariable(*m_Module,
                                                       CodeGen::visit(variableNode->getType()),
                                                       false, llvm::GlobalVariable::PrivateLinkage,
                                                       nullptr);
        m_ScopedNodes[0].emplace(variableNode, globalVariable);

        // TODO: Issue 1
        THROW_NODE_ERROR(variableNode, "Expressions for global variables are not implemented yet.")
        return m_Builder.CreateLoad(globalVariable);
    } else if (variableNode->isLocal()) {
        auto variableIterator = m_ScopedNodes.back().find(variableNode);
        if (variableIterator != m_ScopedNodes.back().end()) {
            if (!std::holds_alternative<llvm::Value *>(variableIterator->second)) {
                std::cout << "This should not have happened. "
                             "Report this bug with a reconstruction of it." << std::endl;
                abort();
            }

            return std::get<llvm::Value *>(variableIterator->second);
        }

        llvm::Value *valueRef = nullptr;

        // TODO: Add the accessed check later if no function call happens in the expression (or
        //  other operations which could change some data in the program).
        bool createVariable = true;
        if (variableNode->getExpression() != nullptr) {
            valueRef = static_cast<llvm::Value *>(CodeGen::visit(variableNode->getExpression()));

            auto instruction = reinterpret_cast<llvm::Instruction *>(valueRef);
            createVariable = instruction->getOpcode() != llvm::Instruction::Alloca;
        }

        if (createVariable) {
            auto expression = valueRef;
            if (expression == nullptr && variableNode->getType()->getTargetStruct() != nullptr) {
                auto structCreate = createStructCreate(variableNode->getType()->getTargetStruct(),
                                                       variableNode);
                valueRef = CodeGen::visit(structCreate);
            } else {
                valueRef = m_Builder.CreateAlloca(CodeGen::visit(variableNode->getType()));
            }

            if (expression != nullptr)
                m_Builder.CreateStore(expression, valueRef);
        }

        m_ScopedNodes.back().emplace(variableNode, valueRef);

        return valueRef;
    }

    THROW_NODE_ERROR(variableNode, "Couldn't create this variable because it isn't local/global.")
    exit(EXIT_FAILURE);
}

llvm::Value *CodeGen::makeAdd(bool isFloating, llvm::Value *rhs, llvm::Value *lhs) {
    if (isFloating)
        return m_Builder.CreateFAdd(lhs, rhs);
    return m_Builder.CreateAdd(lhs, rhs);
}

llvm::Value *CodeGen::makeMul(bool isFloating, llvm::Value *rhs, llvm::Value *lhs) {
    if (isFloating)
        return m_Builder.CreateFMul(lhs, rhs);
    return m_Builder.CreateMul(lhs, rhs);
}

llvm::Value *CodeGen::makeDiv(bool isFloating, bool isSigned, llvm::Value *rhs, llvm::Value *lhs) {
    if (isFloating)
        return m_Builder.CreateFDiv(lhs, rhs);
    if (isSigned)
        return m_Builder.CreateSDiv(lhs, rhs);
    return m_Builder.CreateUDiv(lhs, rhs);
}

llvm::Value *CodeGen::makeSub(bool isFloating, llvm::Value *rhs, llvm::Value *lhs) {
    if (isFloating)
        return m_Builder.CreateFSub(lhs, rhs);
    return m_Builder.CreateSub(lhs, rhs);
}

llvm::Value *CodeGen::makeRem(bool isFloating, bool isSigned, llvm::Value *rhs, llvm::Value *lhs) {
    if (isFloating)
        return m_Builder.CreateFRem(lhs, rhs);
    if (isSigned)
        return m_Builder.CreateSRem(lhs, rhs);
    return m_Builder.CreateURem(lhs, rhs);
}

llvm::Value *CodeGen::makeLT(bool isFloating, bool isSigned, llvm::Value *rhs, llvm::Value *lhs) {
    if (isFloating)
        return m_Builder.CreateFCmpOLT(lhs, rhs);
    if (isSigned)
        return m_Builder.CreateICmpSLT(lhs, rhs);
    return m_Builder.CreateICmpULT(lhs, rhs);
}

llvm::Value *CodeGen::makeGT(bool isFloating, bool isSigned, llvm::Value *rhs, llvm::Value *lhs) {
    if (isFloating)
        return m_Builder.CreateFCmpOGT(lhs, rhs);
    if (isSigned)
        return m_Builder.CreateICmpSGT(lhs, rhs);
    return m_Builder.CreateICmpUGT(lhs, rhs);
}

llvm::Value *CodeGen::makeLE(bool isFloating, bool isSigned, llvm::Value *rhs, llvm::Value *lhs) {
    if (isFloating)
        return m_Builder.CreateFCmpOLE(lhs, rhs);
    if (isSigned)
        return m_Builder.CreateICmpSLE(lhs, rhs);
    return m_Builder.CreateICmpULE(lhs, rhs);
}

llvm::Value *CodeGen::makeGE(bool isFloating, bool isSigned, llvm::Value *rhs, llvm::Value *lhs) {
    if (isFloating)
        return m_Builder.CreateFCmpOGE(lhs, rhs);
    if (isSigned)
        return m_Builder.CreateICmpSGE(lhs, rhs);
    return m_Builder.CreateICmpUGE(lhs, rhs);
}

llvm::Value *CodeGen::makeEQ(bool isFloating, llvm::Value *rhs, llvm::Value *lhs) {
    if (isFloating)
        return m_Builder.CreateFCmpOEQ(lhs, rhs);
    return m_Builder.CreateICmpEQ(lhs, rhs);
}

llvm::Value *CodeGen::makeNE(bool isFloating, llvm::Value *rhs, llvm::Value *lhs) {
    if (isFloating)
        return m_Builder.CreateFCmpONE(lhs, rhs);
    return m_Builder.CreateICmpNE(lhs, rhs);
}

void CodeGen::setPositionAtEnd(llvm::BasicBlock *basicBlock) {
    m_Builder.SetInsertPoint(basicBlock);
    m_CurrentBlock = basicBlock;
}

std::string CodeGen::dumpModule() {
    std::string dumpedCode;
    llvm::raw_string_ostream output(dumpedCode);
    output << *m_Module;
    output.flush();

    Utils::ltrim(dumpedCode);
    Utils::rtrim(dumpedCode);

    return dumpedCode;
}

bool CodeGen::shouldGenerateGEP(const SharedStructNode &structNode, int variableIndex) {
    auto variableNode = structNode->getVariables()[variableIndex];
    if (variableNode->getExpression() == nullptr &&
        variableNode->getType()->getTargetStruct() == nullptr)
        return false;
    if (variableNode->getExpression() != nullptr)
        return true;

    auto targetStruct = variableNode->getType()->getTargetStruct();
    for (auto index = 0; index < targetStruct->getVariables().size(); index++) {
        if (shouldGenerateGEP(targetStruct, index))
            return true;
    }

    return false;
}

SharedStructCreateNode
CodeGen::createStructCreate(const SharedStructNode &targetNode,
                            const SharedASTNode &parentNode) {
    auto identifierNode = std::make_shared<IdentifierNode>();
    identifierNode->setStartToken(parentNode->getStartToken());
    identifierNode->setEndToken(parentNode->getEndToken());
    identifierNode->setScope(parentNode->getScope());
    identifierNode->setParent(parentNode);
    identifierNode->setIdentifier(std::make_shared<Token>(*targetNode->getName()));

    auto structCreateNode = std::make_shared<StructCreateNode>();
    structCreateNode->setStartToken(parentNode->getStartToken());
    structCreateNode->setScope(std::make_shared<SymbolTable>(parentNode->getScope()));
    structCreateNode->setIdentifier(identifierNode);
    structCreateNode->setParent(parentNode);
    structCreateNode->setUnnamed(true);
    structCreateNode->setEndToken(parentNode->getEndToken());

    for (auto index = 0ul; index < targetNode->getVariables().size(); index++) {
        auto variable = targetNode->getVariables().at(index);
        if (variable->getName()->getContent() == "_")
            continue;

        auto argument = std::make_shared<StructArgumentNode>();
        argument->setStartToken(variable->getStartToken());
        argument->setParent(structCreateNode);
        argument->setScope(structCreateNode->getScope());
        argument->setName(variable->getName());
        argument->setDontCopy(true);

        if (variable->getExpression() != nullptr)
            argument->setExpression(SharedOperableNode(variable->getExpression()->clone(
                    argument, argument->getScope())));

        argument->setEndToken(variable->getEndToken());
        argument->getScope()->insert(argument->getName()->getContent(), argument);
        argument->setType(variable->getType());

        structCreateNode->insertArgument(std::min(structCreateNode->getArguments().size(), index),
                                         argument);
    }

    TypeResolver::visit(structCreateNode);
    Inliner::visit(structCreateNode);
    TypeCheck::visit(structCreateNode);
    ScopeCheck::visit(structCreateNode);

    return structCreateNode;
}

std::string CodeGen::dumpValue(llvm::Value *value) {
    std::string dumpedCode;
    llvm::raw_string_ostream output(dumpedCode);
    output << *value;
    output.flush();

    Utils::ltrim(dumpedCode);
    Utils::rtrim(dumpedCode);

    return dumpedCode;
}

std::shared_ptr<llvm::Module> CodeGen::getModule() const {
    return m_Module;
}