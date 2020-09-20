#include "codegen.h"

//
// Created by timo on 8/13/20.
//

#include "../parser/astnodes.h"

#include <iostream>

#include "../compiler/error.h"
#include "../lexer/lexer.h"
#include "../lexer/token.h"
#include "../utils.h"

CodeGen::CodeGen()
        : m_CurrentBlock(nullptr), m_Parameters({}),
          m_Variables({}), m_Functions({}),
          m_Structs({}), m_Builder({m_Context}),
          m_Blocks({}), m_Module(nullptr) {}

void CodeGen::visit(const std::shared_ptr<ASTNode> &node) {
    if (node->getKind() == ASTNode::ROOT) {
        CodeGen::visit(std::static_pointer_cast<RootNode>(node));
    } else if (node->getKind() == ASTNode::FUNCTION) {
        CodeGen::visit(std::static_pointer_cast<FunctionNode>(node));
    } else if (node->getKind() == ASTNode::RETURN) {
        CodeGen::visit(std::static_pointer_cast<ReturnNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT) {
        CodeGen::visit(std::static_pointer_cast<StructNode>(node));
    } else if (node->getKind() == ASTNode::TYPE) {
        CodeGen::visit(std::static_pointer_cast<TypeNode>(node));
    } else if (node->getKind() == ASTNode::BLOCK) {
        CodeGen::visit(std::static_pointer_cast<BlockNode>(node));
    } else if (node->getKind() == ASTNode::NUMBER) {
        CodeGen::visit(std::static_pointer_cast<NumberNode>(node));
    } else if (node->getKind() == ASTNode::STRING) {
        CodeGen::visit(std::static_pointer_cast<StringNode>(node));
    } else if (node->getKind() == ASTNode::UNARY) {
        CodeGen::visit(std::static_pointer_cast<UnaryNode>(node));
    } else if (node->getKind() == ASTNode::PARAMETER) {
        CodeGen::visit(std::static_pointer_cast<ParameterNode>(node));
    } else if (node->getKind() == ASTNode::ARGUMENT) {
        CodeGen::visit(std::static_pointer_cast<ArgumentNode>(node));
    } else if (node->getKind() == ASTNode::IDENTIFIER) {
        auto identifierNode = std::static_pointer_cast<IdentifierNode>(node);

        std::shared_ptr<IdentifierNode> firstIdentifier = identifierNode;
        while (firstIdentifier->getLastIdentifier() != nullptr)
            firstIdentifier = firstIdentifier->getLastIdentifier();

        CodeGen::visit(firstIdentifier);
    } else if (node->getKind() == ASTNode::ASSIGNMENT) {
        CodeGen::visit(std::static_pointer_cast<AssignmentNode>(node));
    } else if (node->getKind() == ASTNode::VARIABLE) {
        CodeGen::visit(std::static_pointer_cast<VariableNode>(node));
    } else if (node->getKind() == ASTNode::BINARY) {
        CodeGen::visit(std::static_pointer_cast<BinaryNode>(node));
    } else if (node->getKind() == ASTNode::FUNCTION_CALL) {
        CodeGen::visit(std::static_pointer_cast<FunctionCallNode>(node));
    } else if (node->getKind() == ASTNode::PARENTHESIZED) {
        CodeGen::visit(std::static_pointer_cast<ParenthesizedNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT_CREATE) {
        CodeGen::visit(std::static_pointer_cast<StructCreateNode>(node));
    } else if (node->getKind() != ASTNode::IMPORT) {
        std::cout << "CodeGen: Unsupported node. " << node->getKind() << std::endl;
    }
}

void CodeGen::visit(const std::shared_ptr<RootNode> &rootNode) {
    auto moduleName = rootNode->getSourcePath();
    moduleName = moduleName.substr(moduleName.rfind('/') + 1, moduleName.length());

    m_Module = std::make_shared<llvm::Module>(moduleName, m_Context);

    for (const auto &node : rootNode->getNodes())
        CodeGen::visit(node);
}

llvm::Value *CodeGen::visit(const std::shared_ptr<FunctionNode> &functionNode) {
    auto foundIterator = m_Functions.find(functionNode);
    if (foundIterator != m_Functions.end())
        return foundIterator->second;

    if (functionNode->hasAnnotation("inlined")) {
        auto callFunctionNode = functionNode->getInlinedFunctionCall()->getParentNode<FunctionNode>();
        m_Functions.emplace(functionNode, CodeGen::visit(callFunctionNode));

        CodeGen::visit(functionNode->getBlock());

        auto callFunctionBlock = m_Blocks.find(functionNode->getBlock());
        if (callFunctionBlock == m_Blocks.end()) {
            THROW_NODE_ERROR(functionNode->getBlock(),
                             "Couldn't find the return variable for the inlined function call.")
            exit(EXIT_FAILURE);
        }

        return std::get<1>(callFunctionBlock->second);
    } else {
        std::vector<llvm::Type *> functionParameters;
        for (auto const &parameter : functionNode->getParameters())
            functionParameters.push_back(CodeGen::visit(parameter->getType()));

        auto functionType = llvm::FunctionType::get(CodeGen::visit(functionNode->getType()),
                                                    functionParameters,
                                                    functionNode->isVariadic());
        auto functionRef = llvm::Function::Create(functionType,
                                                  llvm::Function::ExternalLinkage,
                                                  functionNode->getName()->getContent(),
                                                  *m_Module);
        m_Functions.emplace(functionNode, functionRef);

        if (functionNode->isNative())
            return functionRef;

        CodeGen::visit(functionNode->getBlock());
        return functionRef;
    }
}

llvm::Value *CodeGen::visit(const std::shared_ptr<ArgumentNode> &argumentNode) {
    return CodeGen::visit(std::static_pointer_cast<TypedNode>(argumentNode->getExpression()));
}

llvm::Type *CodeGen::visit(const std::shared_ptr<TypeNode> &typeNode) {
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
        std::cout << "CodeGen: Unsupported type node. " << typeNode << std::endl;
        exit(EXIT_FAILURE);
    }

    for (auto index = 0; index < typeNode->getPointerLevel(); index++)
        type = type->getPointerTo();
    return type;
}

llvm::Type *CodeGen::visit(const std::shared_ptr<StructNode> &structNode) {
    auto foundIterator = m_Structs.find(structNode);
    if (foundIterator != m_Structs.end())
        return foundIterator->second;

    auto structRef = llvm::StructType::create(m_Context, structNode->getName()->getContent());
    m_Structs.emplace(structNode, structRef);

    std::vector<llvm::Type *> types;
    for (auto const &variable : structNode->getVariables())
        types.push_back(CodeGen::visit(variable->getType()));
    structRef->setBody(types, false);

    return structRef;
}

// TODO: Mehrfacher aufruf von inlined functions funktioniert nicht, da alles abgespeichert wird (m_Blocks
//       etc)
// TODO: Varargs bei inlined functions geht nisch
llvm::Value *CodeGen::visit(const std::shared_ptr<ParameterNode> &parameterNode) {
    auto foundIterator = m_Parameters.find(parameterNode);
    if (foundIterator != m_Parameters.end())
        return foundIterator->second;

    auto functionNode = parameterNode->getParentNode<FunctionNode>();
    if (functionNode->hasAnnotation("inlined")) {
        for (auto index = 0; index < functionNode->getParameters().size(); index++) {
            auto targetParameter = functionNode->getParameters().at(index);
            if (targetParameter->getName()->getContent() == parameterNode->getName()->getContent()) {
                auto expression = CodeGen::visit(std::static_pointer_cast<TypedNode>(
                        functionNode->getInlinedFunctionCall()->getArguments().at(index)->getExpression()));
                m_Parameters.emplace(parameterNode, expression);
                return expression;
            }
        }

        THROW_NODE_ERROR(parameterNode, "Couldn't find the argument for the inlined function call.")
        exit(EXIT_FAILURE);
    }

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
    m_Builder.CreateStore(parameter, parameterVariable);
    m_Parameters.emplace(parameterNode, parameterVariable);

    return parameterVariable;
}

llvm::BasicBlock *CodeGen::visit(const std::shared_ptr<BlockNode> &blockNode) {
    auto foundIterator = m_Blocks.find(blockNode);
    if (foundIterator != m_Blocks.end())
        return std::get<0>(foundIterator->second);

    auto functionNode = blockNode->getParentNode<FunctionNode>();
    auto functionRef = reinterpret_cast<llvm::Function *>(CodeGen::visit(functionNode));

    auto isEntryBlock = functionNode == blockNode->getParentNode();
    auto startBlock = functionNode->hasAnnotation("inlined")
                      ? m_CurrentBlock
                      : llvm::BasicBlock::Create(m_Context,
                                                 isEntryBlock ? "entry" : "",
                                                 functionRef);
    if (isEntryBlock)
        functionNode->setEntryBlock(startBlock);

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

    if (functionNode == blockNode->getParentNode()) {
        std::string namePrefix = functionNode->hasAnnotation("inlined") ? "iln_" : "";
        if (functionNode->getType()->getBits() != 0 || functionNode->getType()->getTargetStruct() != nullptr)
            returnVariable = m_Builder.CreateAlloca(CodeGen::visit(functionNode->getType()),
                                                    nullptr,
                                                    namePrefix + "var_ret");

        returnBlock = llvm::BasicBlock::Create(m_Context, namePrefix + "return", functionRef);

        if (!functionNode->hasAnnotation("inlined")) {
            CodeGen::setPositionAtEnd(returnBlock);

            if (functionNode->getType()->getBits() != 0 ||
                functionNode->getType()->getTargetStruct() != nullptr) {
                auto loadedVariable = m_Builder.CreateLoad(returnVariable, "loaded_ret");
                m_Builder.CreateRet(loadedVariable);
            } else
                m_Builder.CreateRetVoid();

            CodeGen::setPositionAtEnd(startBlock);
        }
    }

    auto tuple = std::make_tuple(startBlock, returnVariable, returnBlock);
    m_Blocks.emplace(blockNode, tuple);

    for (const auto &node : blockNode->getNodes())
        CodeGen::visit(node);

    if (!hasReturn && functionNode == blockNode->getParentNode())
        m_Builder.CreateBr(returnBlock);

    if (functionNode == blockNode->getParentNode()) {
        auto lastBasicBlock = functionRef->end() == functionRef->begin()
                              ? nullptr
                              : &*--functionRef->end();
        returnBlock->moveAfter(lastBasicBlock);
    }

    if (lastBlock != nullptr)
        setPositionAtEnd(lastBlock);

    if (functionNode->hasAnnotation("inlined"))
        setPositionAtEnd(returnBlock);

    return startBlock;
}

llvm::Value *CodeGen::visit(const std::shared_ptr<ReturnNode> &returnNode) {
    auto functionNode = returnNode->getParentNode<FunctionNode>();
    auto blockData = m_Blocks.find(returnNode->getParentNode<BlockNode>())->second;

    if (functionNode->getType()->getBits() == 0 && functionNode->getType()->getTargetStruct() == nullptr) {
        m_Builder.CreateBr(std::get<2>(blockData));

        return llvm::UndefValue::get(llvm::Type::getVoidTy(m_Context));
    } else {
        auto expression = CodeGen::visit(std::static_pointer_cast<TypedNode>(returnNode->getExpression()));
        auto returnVariable = std::get<1>(blockData);

        m_Builder.CreateStore(expression, returnVariable);
        m_Builder.CreateBr(std::get<2>(blockData));

        return expression;
    }
}

llvm::Value *CodeGen::visit(const std::shared_ptr<AssignmentNode> &assignmentNode) {
    auto variableRef = CodeGen::visit(assignmentNode->getStartIdentifier());
    auto expression = CodeGen::visit(std::static_pointer_cast<TypedNode>(assignmentNode->getExpression()));

    m_Builder.CreateStore(expression, variableRef);
    return expression;
}

llvm::Value *CodeGen::visit(const std::shared_ptr<NumberNode> &numberNode) {
    if (!numberNode->getType()->isFloating()) {
        auto value = std::stoi(numberNode->getNumber()->getContent());
        return llvm::ConstantInt::get(CodeGen::visit(numberNode->getType()),
                                      value,
                                      numberNode->getType()->isSigned());
    }

    return llvm::ConstantFP::get(CodeGen::visit(numberNode->getType()),
                                 numberNode->getNumber()->getContent());
}

llvm::Value *CodeGen::visit(const std::shared_ptr<StringNode> &stringNode) {
    auto stringConstant = llvm::ConstantDataArray::getString(m_Context,
                                                             stringNode->getString()->getContent());

    auto stringVariable = new llvm::GlobalVariable(*m_Module,
                                                   stringConstant->getType(),
                                                   false,
                                                   llvm::GlobalVariable::PrivateLinkage,
                                                   stringConstant);

    std::vector<llvm::Constant *> indices;
    indices.push_back(llvm::ConstantInt::get(llvm::Type::getIntNTy(m_Context, 32), 0, true));
    indices.push_back(llvm::ConstantInt::get(llvm::Type::getIntNTy(m_Context, 32), 0, true));

    return llvm::ConstantExpr::getInBoundsGetElementPtr(stringConstant->getType(),
                                                        stringVariable,
                                                        std::vector<llvm::Constant *>());
}

llvm::Value *CodeGen::visit(const std::shared_ptr<UnaryNode> &unaryNode) {
    auto expression = CodeGen::visit(std::static_pointer_cast<TypedNode>(unaryNode->getExpression()));
    if (unaryNode->getOperatorKind() == UnaryNode::NEGATE) {
        if (unaryNode->getType()->isFloating())
            return m_Builder.CreateFNeg(expression);
        return m_Builder.CreateNeg(expression);
    }

    std::cout << "CodeGen: Unsupported unary node. " << unaryNode->getKind() << std::endl;
    exit(EXIT_FAILURE);
}

llvm::Value *CodeGen::visit(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode) {
    return CodeGen::visit(std::static_pointer_cast<TypedNode>(parenthesizedNode->getExpression()));
}

llvm::Value *CodeGen::visit(const std::shared_ptr<IdentifierNode> &identifierNode) {
    auto typedTarget = std::dynamic_pointer_cast<TypedNode>(identifierNode->getTargetNode());
    if (typedTarget == nullptr) {
        THROW_NODE_ERROR(identifierNode->getTargetNode(), "Can't use a non-typed node as an identifier.")
        exit(EXIT_FAILURE);
    }

    auto targetValue = CodeGen::visit(typedTarget);
    if (identifierNode->isDereference()) {
        targetValue = m_Builder.CreateLoad(targetValue);
    } else if (identifierNode->isPointer()) {
        m_Builder.CreateGEP(targetValue, std::vector<llvm::Value *>(0));
    }

    auto targetStruct = identifierNode->getType()->getTargetStruct();
    auto currentIdentifier = identifierNode;
    while (currentIdentifier->getNextIdentifier() != nullptr) {
        currentIdentifier = currentIdentifier->getNextIdentifier();

        auto variableNode = std::dynamic_pointer_cast<VariableNode>(currentIdentifier->getTargetNode());
        typedTarget = variableNode;

        if (variableNode == nullptr) {
            THROW_NODE_ERROR(identifierNode->getTargetNode(),
                             "Can't use a non-variable node as an sub identifier.")
            exit(EXIT_FAILURE);
        }

        auto variableIndex = Utils::indexOf(targetStruct->getVariables(), variableNode).second;
        targetValue = m_Builder.CreateStructGEP(targetValue, variableIndex);

        if (currentIdentifier->isDereference()) {
            targetValue = m_Builder.CreateLoad(targetValue);
        } else if (currentIdentifier->isPointer()) {
            m_Builder.CreateGEP(targetValue, std::vector<llvm::Value *>(0));
        }

        targetStruct = typedTarget->getType()->getTargetStruct();
    }

    if (identifierNode->getParentNode()->getKind() != ASTNode::ASSIGNMENT &&
        !currentIdentifier->isPointer()) {
        auto instruction = reinterpret_cast<llvm::Instruction *>(targetValue);
        if (instruction->getOpcode() == llvm::Instruction::Alloca)
            return m_Builder.CreateLoad(targetValue);
    }

    return targetValue;
}

llvm::Value *CodeGen::visit(const std::shared_ptr<BinaryNode> &binaryNode) {
    if (binaryNode->getOperatorKind() == BinaryNode::BIT_CAST) {
        auto lhsValue = CodeGen::visit(std::static_pointer_cast<TypedNode>(binaryNode->getLHS()));
        auto rhsValue = CodeGen::visit(std::static_pointer_cast<TypeNode>(binaryNode->getRHS()));
        return m_Builder.CreateBitCast(lhsValue, rhsValue);
    } else {
        auto lhsValue = CodeGen::visit(std::static_pointer_cast<TypedNode>(binaryNode->getLHS()));
        auto rhsValue = CodeGen::visit(std::static_pointer_cast<TypedNode>(binaryNode->getRHS()));

        auto floatingPoint = binaryNode->getLHS()->getType()->isFloating() ||
                             binaryNode->getRHS()->getType()->isFloating();
        auto isSigned = binaryNode->getLHS()->getType()->isSigned() ||
                        binaryNode->getRHS()->getType()->isSigned();

        switch (binaryNode->getOperatorKind()) {
            case BinaryNode::ADDITION:
                return CodeGen::makeAdd(floatingPoint, lhsValue, rhsValue);
            case BinaryNode::MULTIPLICATION:
                return CodeGen::makeMul(floatingPoint, lhsValue, rhsValue);
            case BinaryNode::DIVISION:
                return CodeGen::makeDiv(floatingPoint, isSigned, lhsValue, rhsValue);
            case BinaryNode::SUBTRACTION:
                return CodeGen::makeSub(floatingPoint, lhsValue, rhsValue);
            case BinaryNode::REMAINING:
                return CodeGen::makeRem(isSigned, lhsValue, rhsValue);

            case BinaryNode::LESS_THAN:
                return CodeGen::makeLT(floatingPoint, isSigned, lhsValue, rhsValue);
            case BinaryNode::GREATER_THAN:
                return CodeGen::makeGT(floatingPoint, isSigned, lhsValue, rhsValue);
            case BinaryNode::LESS_EQUAL_THAN:
                return CodeGen::makeLE(floatingPoint, isSigned, lhsValue, rhsValue);
            case BinaryNode::GREATER_EQUAL_THAN:
                return CodeGen::makeGE(floatingPoint, isSigned, lhsValue, rhsValue);

            case BinaryNode::EQUAL:
                return CodeGen::makeEQ(floatingPoint, lhsValue, rhsValue);
            case BinaryNode::NOT_EQUAL:
                return CodeGen::makeNE(floatingPoint, lhsValue, rhsValue);

            default:
                std::cout << "CodeGen: Unsupported binary node. " << binaryNode->getKind() << std::endl;
                exit(EXIT_FAILURE);
        }
    }
}

llvm::Value *CodeGen::visit(const std::shared_ptr<FunctionCallNode> &functionCallNode) {
    auto functionNode = std::static_pointer_cast<FunctionNode>(functionCallNode->getTargetNode());
    if (functionNode->hasAnnotation("inlined"))
        return CodeGen::visit(functionNode);

    auto functionRef = CodeGen::visit(functionNode);

    auto sortedArguments(functionCallNode->getArguments());
    functionCallNode->getSortedArguments(functionNode, sortedArguments);

    std::vector<llvm::Value *> functionArguments;
    for (auto index = 0; index < sortedArguments.size(); index++) {
        auto expression = CodeGen::visit(sortedArguments.at(index));
        functionArguments.insert(functionArguments.begin() + index, expression);
    }

    return m_Builder.CreateCall(functionRef, functionArguments);
}

// TODO: Doesn't work as intended, complete rework.
llvm::Value *CodeGen::visit(const std::shared_ptr<StructCreateNode> &structCreateNode) {
    auto structNode = std::static_pointer_cast<StructNode>(structCreateNode->getTargetNode());
    auto structRef = CodeGen::visit(structNode);

    std::vector<std::shared_ptr<OperableNode>> originals;
    if (m_OriginalExpressions.find(structCreateNode) == m_OriginalExpressions.end()) {
        for (const auto &variable : structNode->getVariables())
            originals.push_back(variable->getExpression());
        m_OriginalExpressions.emplace(structCreateNode, originals);
    } else originals = m_OriginalExpressions.find(structCreateNode)->second;

    auto oldExpressions = m_LastExpressions.find(structCreateNode);
    auto oldExpressionsExist = oldExpressions != m_LastExpressions.end();

    for (auto index = 0; index < structNode->getVariables().size(); index++)
        structNode->getVariables()[index]->setExpression(originals[index]);

    std::vector<std::shared_ptr<OperableNode>> filledExpressions;
    structCreateNode->getFilledExpressions(structNode, filledExpressions);

    std::vector<std::shared_ptr<OperableNode>> newExpressions;
    for (auto index = 0; index < structNode->getVariables().size(); index++) {
        newExpressions.push_back(structNode->getVariables()[index]->getExpression());
        structNode->getVariables()[index]->setExpression(filledExpressions[index]);
    }

    m_LastExpressions.emplace(structCreateNode, newExpressions);

    auto structVariable = m_Builder.CreateAlloca(structRef);
    for (auto index = 0; index < structNode->getVariables().size(); index++) {
        auto variable = structNode->getVariables()[index];

        auto variableGEP = m_Builder.CreateStructGEP(structVariable, index);
        auto expression = CodeGen::visit(variable);

        m_Builder.CreateStore(expression, variableGEP);
    }

    m_LastExpressions.erase(structCreateNode);
    if (oldExpressionsExist) {
        m_LastExpressions.emplace(structCreateNode, oldExpressions->second);

        for (auto index = 0; index < structNode->getVariables().size(); index++)
            structNode->getVariables()[index]->setExpression(oldExpressions->second[index]);
    }

    if (structCreateNode->getParentNode<VariableNode>() != nullptr)
        return structVariable;
    return m_Builder.CreateLoad(structVariable);
}

llvm::Value *CodeGen::visit(const std::shared_ptr<VariableNode> &variableNode) {
    auto foundIterator = m_Variables.find(variableNode);
    if (foundIterator != m_Variables.end())
        return foundIterator->second;

    if (!variableNode->isLocal() && variableNode->getParentNode()->getKind() != ASTNode::STRUCT) {
        std::cerr << "Not yet implemented." << std::endl;
        exit(1);
    }

    llvm::Value *valueRef = nullptr;
    bool createVariable = true;

    if (variableNode->getExpression() != nullptr) {
        valueRef = CodeGen::visit(std::static_pointer_cast<TypedNode>(variableNode->getExpression()));

        auto instruction = reinterpret_cast<llvm::Instruction *>(valueRef);
        createVariable = instruction->getOpcode() == llvm::Instruction::Alloca;
    }

    if (createVariable) {
        auto expression = valueRef;
        valueRef = m_Builder.CreateAlloca(CodeGen::visit(variableNode->getType()));

        if (expression != nullptr)
            m_Builder.CreateStore(expression, valueRef);
    }

    m_Variables.emplace(variableNode, valueRef);

    return valueRef;
}

llvm::Value *CodeGen::visit(const std::shared_ptr<TypedNode> &typedNode) {
    if (typedNode->getKind() == ASTNode::ARGUMENT) {
        return CodeGen::visit(std::static_pointer_cast<ArgumentNode>(typedNode));
    } else if (typedNode->getKind() == ASTNode::ASSIGNMENT) {
        return CodeGen::visit(std::static_pointer_cast<AssignmentNode>(typedNode));
    } else if (typedNode->getKind() == ASTNode::BINARY) {
        return CodeGen::visit(std::static_pointer_cast<BinaryNode>(typedNode));
    } else if (typedNode->getKind() == ASTNode::FUNCTION) {
        return CodeGen::visit(std::static_pointer_cast<FunctionNode>(typedNode));
    } else if (typedNode->getKind() == ASTNode::FUNCTION_CALL) {
        return CodeGen::visit(std::static_pointer_cast<FunctionCallNode>(typedNode));
    } else if (typedNode->getKind() == ASTNode::IDENTIFIER) {
        auto identifierNode = std::static_pointer_cast<IdentifierNode>(typedNode);

        std::shared_ptr<IdentifierNode> firstIdentifier = identifierNode;
        while (firstIdentifier->getLastIdentifier() != nullptr)
            firstIdentifier = firstIdentifier->getLastIdentifier();

        return CodeGen::visit(firstIdentifier);
    } else if (typedNode->getKind() == ASTNode::NUMBER) {
        return CodeGen::visit(std::static_pointer_cast<NumberNode>(typedNode));
    } else if (typedNode->getKind() == ASTNode::PARAMETER) {
        return CodeGen::visit(std::static_pointer_cast<ParameterNode>(typedNode));
    } else if (typedNode->getKind() == ASTNode::PARENTHESIZED) {
        return CodeGen::visit(std::static_pointer_cast<ParenthesizedNode>(typedNode));
    } else if (typedNode->getKind() == ASTNode::RETURN) {
        return CodeGen::visit(std::static_pointer_cast<ReturnNode>(typedNode));
    } else if (typedNode->getKind() == ASTNode::STRING) {
        return CodeGen::visit(std::static_pointer_cast<StringNode>(typedNode));
    } else if (typedNode->getKind() == ASTNode::STRUCT) {
        return llvm::UndefValue::get(CodeGen::visit(std::static_pointer_cast<StructNode>(typedNode)));
    } else if (typedNode->getKind() == ASTNode::STRUCT_CREATE) {
        return CodeGen::visit(std::static_pointer_cast<StructCreateNode>(typedNode));
    } else if (typedNode->getKind() == ASTNode::UNARY) {
        return CodeGen::visit(std::static_pointer_cast<UnaryNode>(typedNode));
    } else if (typedNode->getKind() == ASTNode::VARIABLE) {
        return CodeGen::visit(std::static_pointer_cast<VariableNode>(typedNode));
    } else if (typedNode->getKind() == ASTNode::TYPE)
        return nullptr;

    std::cout << "CodeGen: Unsupported typed node. " << typedNode->getKind() << std::endl;
    exit(EXIT_FAILURE);
}

void CodeGen::setPositionAtEnd(llvm::BasicBlock *basicBlock) {
    m_Builder.SetInsertPoint(basicBlock);
    m_CurrentBlock = basicBlock;
}

llvm::Value *CodeGen::makeAdd(bool floatingPoint, llvm::Value *rhs, llvm::Value *lhs) {
    if (floatingPoint)
        return m_Builder.CreateFAdd(lhs, rhs);
    return m_Builder.CreateAdd(lhs, rhs);
}

llvm::Value *CodeGen::makeMul(bool floatingPoint, llvm::Value *rhs, llvm::Value *lhs) {
    if (floatingPoint)
        return m_Builder.CreateFMul(lhs, rhs);
    return m_Builder.CreateMul(lhs, rhs);
}

llvm::Value *CodeGen::makeDiv(bool floatingPoint, bool isSigned, llvm::Value *rhs, llvm::Value *lhs) {
    if (floatingPoint)
        return m_Builder.CreateFDiv(lhs, rhs);
    if (isSigned)
        return m_Builder.CreateSDiv(lhs, rhs);
    return m_Builder.CreateUDiv(lhs, rhs);
}

llvm::Value *CodeGen::makeSub(bool floatingPoint, llvm::Value *rhs, llvm::Value *lhs) {
    if (floatingPoint)
        return m_Builder.CreateFSub(lhs, rhs);
    return m_Builder.CreateSub(lhs, rhs);
}

llvm::Value *CodeGen::makeRem(bool isSigned, llvm::Value *rhs, llvm::Value *lhs) {
    if (isSigned)
        return m_Builder.CreateSRem(lhs, rhs);
    return m_Builder.CreateURem(lhs, rhs);
}

llvm::Value *CodeGen::makeLT(bool floatingPoint, bool isSigned, llvm::Value *rhs, llvm::Value *lhs) {
    if (floatingPoint)
        return m_Builder.CreateFCmpOLT(lhs, rhs);
    if (isSigned)
        return m_Builder.CreateICmpSLT(lhs, rhs);
    return m_Builder.CreateICmpULT(lhs, rhs);
}

llvm::Value *CodeGen::makeGT(bool floatingPoint, bool isSigned, llvm::Value *rhs, llvm::Value *lhs) {
    if (floatingPoint)
        return m_Builder.CreateFCmpOGT(lhs, rhs);
    if (isSigned)
        return m_Builder.CreateICmpSGT(lhs, rhs);
    return m_Builder.CreateICmpUGT(lhs, rhs);
}

llvm::Value *CodeGen::makeLE(bool floatingPoint, bool isSigned, llvm::Value *rhs, llvm::Value *lhs) {
    if (floatingPoint)
        return m_Builder.CreateFCmpOLE(lhs, rhs);
    if (isSigned)
        return m_Builder.CreateICmpSLE(lhs, rhs);
    return m_Builder.CreateICmpULE(lhs, rhs);
}

llvm::Value *CodeGen::makeGE(bool floatingPoint, bool isSigned, llvm::Value *rhs, llvm::Value *lhs) {
    if (floatingPoint)
        return m_Builder.CreateFCmpOGE(lhs, rhs);
    if (isSigned)
        return m_Builder.CreateICmpSGE(lhs, rhs);
    return m_Builder.CreateICmpUGE(lhs, rhs);
}

llvm::Value *CodeGen::makeEQ(bool floatingPoint, llvm::Value *rhs, llvm::Value *lhs) {
    if (floatingPoint)
        return m_Builder.CreateFCmpOEQ(lhs, rhs);
    return m_Builder.CreateICmpEQ(lhs, rhs);
}

llvm::Value *CodeGen::makeNE(bool floatingPoint, llvm::Value *rhs, llvm::Value *lhs) {
    if (floatingPoint)
        return m_Builder.CreateFCmpONE(lhs, rhs);
    return m_Builder.CreateICmpNE(lhs, rhs);
}

std::shared_ptr<llvm::Module> CodeGen::getModule() const {
    return m_Module;
}