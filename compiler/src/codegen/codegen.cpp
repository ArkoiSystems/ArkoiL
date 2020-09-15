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
          m_Structs({}), m_Builder(nullptr),
          m_Context(nullptr), m_Blocks({}),
          m_Module(nullptr) {}

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

    m_Context = LLVMContextCreate();
    m_Module = LLVMModuleCreateWithNameInContext(moduleName.c_str(), m_Context);
    m_Builder = LLVMCreateBuilderInContext(m_Context);

    for (const auto &node : rootNode->getNodes())
        CodeGen::visit(node);
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<FunctionNode> &functionNode) {
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
        std::vector<LLVMTypeRef> functionParameters;
        for (auto const &parameter : functionNode->getParameters())
            functionParameters.push_back(CodeGen::visit(parameter->getType()));

        auto functionType = LLVMFunctionType(CodeGen::visit(functionNode->getType()),
                                             functionParameters.data(),
                                             functionParameters.size(),
                                             functionNode->isVariadic());

        if (functionNode->isNative()) {
            auto functionRef = LLVMAddFunction(m_Module, functionNode->getName()->getContent().c_str(),
                                               functionType);
            m_Functions.emplace(functionNode, functionRef);
            return functionRef;
        }

        auto functionRef = LLVMAddFunction(m_Module, functionNode->getName()->getContent().c_str(),
                                           functionType);
        m_Functions.emplace(functionNode, functionRef);
        CodeGen::visit(functionNode->getBlock());
        return functionRef;
    }
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<ArgumentNode> &argumentNode) {
    return CodeGen::visit(std::static_pointer_cast<TypedNode>(argumentNode->getExpression()));
}

LLVMTypeRef CodeGen::visit(const std::shared_ptr<TypeNode> &typeNode) {
    LLVMTypeRef type;
    if (typeNode->isNumeric() && typeNode->isFloating()) {
        type = typeNode->getBits() == 32 ? LLVMFloatTypeInContext(m_Context)
                                         : LLVMDoubleTypeInContext(m_Context);
    } else if (typeNode->isNumeric() && !typeNode->isFloating()) {
        type = LLVMIntTypeInContext(m_Context, typeNode->getBits());
    } else if (typeNode->getTargetStruct() != nullptr) {
        type = CodeGen::visit(typeNode->getTargetStruct());
    } else if (typeNode->getBits() == 0) {
        type = LLVMVoidTypeInContext(m_Context);
    } else {
        std::cout << "CodeGen: Unsupported type node. " << typeNode << std::endl;
        exit(EXIT_FAILURE);
    }

    for (auto index = 0; index < typeNode->getPointerLevel(); index++)
        type = LLVMPointerType(type, 0);
    return type;
}

LLVMTypeRef CodeGen::visit(const std::shared_ptr<StructNode> &structNode) {
    auto foundIterator = m_Structs.find(structNode);
    if (foundIterator != m_Structs.end())
        return foundIterator->second;

    auto structRef = LLVMStructCreateNamed(m_Context, structNode->getName()->getContent().c_str());
    m_Structs.emplace(structNode, structRef);

    std::vector<LLVMTypeRef> types;
    for (auto const &variable : structNode->getVariables())
        types.push_back(CodeGen::visit(variable->getType()));
    LLVMStructSetBody(structRef, types.data(), types.size(), 0);

    return structRef;
}

// TODO: Mehrfacher aufruf von inlined functions funktioniert nicht, da alles abgespeichert wird (m_Blocks
//       etc)
// TODO: Varargs bei inlined functions geht nisch
LLVMValueRef CodeGen::visit(const std::shared_ptr<ParameterNode> &parameterNode) {
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

    auto functionRef = CodeGen::visit(functionNode);

    LLVMValueRef parameter = nullptr;
    for (auto index = 0; index < functionNode->getParameters().size(); index++) {
        auto targetParameter = functionNode->getParameters().at(index);
        if (targetParameter->getName()->getContent() == parameterNode->getName()->getContent()) {
            parameter = LLVMGetParam(functionRef, index);
            break;
        }
    }

    if (parameter == nullptr) {
        THROW_NODE_ERROR(parameterNode, "No target value found for this parameter.")
        exit(EXIT_FAILURE);
    }

    auto parameterVariable = LLVMBuildAlloca(m_Builder, CodeGen::visit(parameterNode->getType()), "");
    LLVMBuildStore(m_Builder, parameter, parameterVariable);
    m_Parameters.emplace(parameterNode, parameterVariable);

    return parameterVariable;
}

LLVMBasicBlockRef CodeGen::visit(const std::shared_ptr<BlockNode> &blockNode) {
    auto foundIterator = m_Blocks.find(blockNode);
    if (foundIterator != m_Blocks.end())
        return std::get<0>(foundIterator->second);

    auto functionNode = blockNode->getParentNode<FunctionNode>();
    auto functionRef = CodeGen::visit(functionNode);

    LLVMBasicBlockRef startBlock = functionNode->hasAnnotation("inlined")
                                   ? m_CurrentBlock
                                   : LLVMAppendBasicBlockInContext(m_Context, functionRef,
                                                                   functionNode == blockNode->getParentNode()
                                                                   ? "entry" : "");

    LLVMBasicBlockRef returnBlock = nullptr;
    LLVMValueRef returnVariable = nullptr;
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
            returnVariable = LLVMBuildAlloca(m_Builder, CodeGen::visit(functionNode->getType()),
                                             (namePrefix + "var_ret").c_str());

        returnBlock = LLVMAppendBasicBlockInContext(m_Context, functionRef,
                                                    (namePrefix + "return").c_str());

        if (!functionNode->hasAnnotation("inlined")) {
            CodeGen::setPositionAtEnd(returnBlock);

            if (functionNode->getType()->getBits() != 0 ||
                functionNode->getType()->getTargetStruct() != nullptr) {
                auto loadedVariable = LLVMBuildLoad(m_Builder, returnVariable, "loaded_ret");
                LLVMBuildRet(m_Builder, loadedVariable);
            } else
                LLVMBuildRetVoid(m_Builder);

            CodeGen::setPositionAtEnd(startBlock);
        }
    }

    auto tuple = std::make_tuple(startBlock, returnVariable, returnBlock);
    m_Blocks.emplace(blockNode, tuple);

    for (const auto &node : blockNode->getNodes())
        CodeGen::visit(node);

    if (!hasReturn && functionNode == blockNode->getParentNode())
        LLVMBuildBr(m_Builder, returnBlock);

    if (functionNode == blockNode->getParentNode())
        LLVMMoveBasicBlockAfter(returnBlock, LLVMGetLastBasicBlock(functionRef));

    if (lastBlock != nullptr)
        setPositionAtEnd(lastBlock);

    if (functionNode->hasAnnotation("inlined"))
        setPositionAtEnd(returnBlock);

    return startBlock;
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<ReturnNode> &returnNode) {
    auto functionNode = returnNode->getParentNode<FunctionNode>();
    auto blockData = m_Blocks.find(returnNode->getParentNode<BlockNode>())->second;

    if (functionNode->getType()->getBits() == 0) {
        LLVMBuildBr(m_Builder, std::get<2>(blockData));

        return LLVMGetUndef(LLVMVoidType());
    } else {
        auto expression = CodeGen::visit(
                std::static_pointer_cast<TypedNode>(returnNode->getExpression()));
        auto returnVariable = std::get<1>(blockData);

        LLVMBuildStore(m_Builder, expression, returnVariable);
        LLVMBuildBr(m_Builder, std::get<2>(blockData));

        return expression;
    }
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<AssignmentNode> &assignmentNode) {
    auto variableRef = CodeGen::visit(assignmentNode->getStartIdentifier());
    auto expression = CodeGen::visit(std::static_pointer_cast<TypedNode>(assignmentNode->getExpression()));

    LLVMBuildStore(m_Builder, expression, variableRef);
    return expression;
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<NumberNode> &numberNode) {
    if (!numberNode->getType()->isFloating()) {
        auto value = std::stoi(numberNode->getNumber()->getContent());
        return LLVMConstInt(CodeGen::visit(numberNode->getType()), value, numberNode->getType()->isSigned());
    }

    return LLVMConstRealOfString(CodeGen::visit(numberNode->getType()),
                                 numberNode->getNumber()->getContent().c_str());
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<StringNode> &stringNode) {
    auto stringConstant = LLVMConstString(stringNode->getString()->getContent().c_str(),
                                          stringNode->getString()->getContent().length(),
                                          false);

    auto stringVariable = LLVMAddGlobal(m_Module, LLVMTypeOf(stringConstant), "");
    LLVMSetLinkage(stringVariable, LLVMPrivateLinkage);
    LLVMSetUnnamedAddr(stringVariable, LLVMGlobalUnnamedAddr);
    LLVMSetInitializer(stringVariable, stringConstant);
    LLVMSetAlignment(stringVariable, 1);

    return LLVMConstInBoundsGEP(stringVariable, &(new LLVMValueRef[2]{
            LLVMConstInt(LLVMIntTypeInContext(m_Context, 32), 0, true),
            LLVMConstInt(LLVMIntTypeInContext(m_Context, 32), 0, true),
    })[0], 2);
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<UnaryNode> &unaryNode) {
    auto expression = CodeGen::visit(std::static_pointer_cast<TypedNode>(unaryNode->getExpression()));
    if (unaryNode->getOperatorKind() == UnaryNode::NEGATE)
        return LLVMBuildNeg(m_Builder, expression, "");

    std::cout << "CodeGen: Unsupported unary node. " << unaryNode->getKind() << std::endl;
    exit(EXIT_FAILURE);
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode) {
    return CodeGen::visit(std::static_pointer_cast<TypedNode>(parenthesizedNode->getExpression()));
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<IdentifierNode> &identifierNode) {
    auto typedTarget = std::dynamic_pointer_cast<TypedNode>(identifierNode->getTargetNode());
    if (typedTarget == nullptr) {
        THROW_NODE_ERROR(identifierNode->getTargetNode(), "Can't use a non-typed node as an identifier.")
        exit(EXIT_FAILURE);
    }

    auto targetValue = CodeGen::visit(typedTarget);
    if (identifierNode->isDereference()) {
        targetValue = LLVMBuildLoad(m_Builder, targetValue, "");
    } else if (identifierNode->isPointer()) {
        targetValue = LLVMBuildGEP(m_Builder, targetValue, new LLVMValueRef[0], 0, "");
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
        targetValue = LLVMBuildStructGEP(m_Builder, targetValue, variableIndex, "");

        if (currentIdentifier->isDereference()) {
            targetValue = LLVMBuildLoad(m_Builder, targetValue, "");
        } else if (currentIdentifier->isPointer()) {
            targetValue = LLVMBuildGEP(m_Builder, targetValue, nullptr, 0, "");
        }

        targetStruct = typedTarget->getType()->getTargetStruct();
    }

    if (identifierNode->getParentNode()->getKind() != ASTNode::ASSIGNMENT && !currentIdentifier->isPointer())
        return LLVMBuildLoad(m_Builder, targetValue, "");

    return targetValue;
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<BinaryNode> &binaryNode) {
    if (binaryNode->getOperatorKind() == BinaryNode::BIT_CAST) {
        auto lhsValue = CodeGen::visit(std::static_pointer_cast<TypedNode>(binaryNode->getLHS()));
        auto rhsValue = CodeGen::visit(std::static_pointer_cast<TypeNode>(binaryNode->getRHS()));
        return LLVMBuildBitCast(m_Builder, lhsValue, rhsValue, "");
    } else {
        auto lhsValue = CodeGen::visit(std::static_pointer_cast<TypedNode>(binaryNode->getLHS()));
        auto rhsValue = CodeGen::visit(std::static_pointer_cast<TypedNode>(binaryNode->getRHS()));

        auto floatingPoint =
                binaryNode->getLHS()->getType()->isFloating() ||
                binaryNode->getRHS()->getType()->isFloating();
        auto isSigned =
                binaryNode->getLHS()->getType()->isSigned() || binaryNode->getRHS()->getType()->isSigned();

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

LLVMValueRef CodeGen::visit(const std::shared_ptr<FunctionCallNode> &functionCallNode) {
    auto functionNode = std::static_pointer_cast<FunctionNode>(functionCallNode->getTargetNode());
    if (functionNode->hasAnnotation("inlined"))
        return CodeGen::visit(functionNode);

    auto functionRef = CodeGen::visit(functionNode);

    auto sortedArguments(functionCallNode->getArguments());
    functionCallNode->getSortedArguments(functionNode, sortedArguments);

    std::vector<LLVMValueRef> functionArguments;
    for (auto index = 0; index < sortedArguments.size(); index++) {
        auto expression = CodeGen::visit(sortedArguments.at(index));
        functionArguments.insert(functionArguments.begin() + index, expression);
    }

    return LLVMBuildCall(m_Builder, functionRef, functionArguments.data(),
                         functionArguments.size(), "");
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<StructCreateNode> &structCreateNode) {
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

    auto structVariable = LLVMBuildAlloca(m_Builder, structRef, "");
    for (auto index = 0; index < structNode->getVariables().size(); index++) {
        auto variable = structNode->getVariables()[index];
        auto variableGEP = LLVMBuildStructGEP(m_Builder, structVariable, index, "");

        LLVMBuildStore(m_Builder, CodeGen::visit(variable), variableGEP);
    }

    m_LastExpressions.erase(structCreateNode);
    if (oldExpressionsExist) {
        m_LastExpressions.emplace(structCreateNode, oldExpressions->second);

        for (auto index = 0; index < structNode->getVariables().size(); index++)
            structNode->getVariables()[index]->setExpression(oldExpressions->second[index]);
    }

    if (structCreateNode->getParentNode<VariableNode>() != nullptr)
        return structVariable;
    return LLVMBuildLoad(m_Builder, structVariable, "");
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<VariableNode> &variableNode) {
    auto foundIterator = m_Variables.find(variableNode);
    if (foundIterator != m_Variables.end())
        return foundIterator->second;

    if (!variableNode->isLocal() && variableNode->getParentNode()->getKind() != ASTNode::STRUCT) {
        std::cerr << "Not yet implemented." << std::endl;
        exit(1);
    }

    bool createVariable = true;
    LLVMValueRef valueRef;
    if (variableNode->getExpression() != nullptr) {
        valueRef = CodeGen::visit(std::static_pointer_cast<TypedNode>(variableNode->getExpression()));
        createVariable = LLVMGetInstructionOpcode(valueRef) != LLVMAlloca;
    }

    if (createVariable) {
        auto expression = valueRef;
        valueRef = LLVMBuildAlloca(m_Builder, CodeGen::visit(variableNode->getType()), "");

        if (expression != nullptr)
            LLVMBuildStore(m_Builder, expression, valueRef);
    }

    m_Variables.emplace(variableNode, valueRef);

    return valueRef;
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<TypedNode> &typedNode) {
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
        return LLVMGetUndef(CodeGen::visit(std::static_pointer_cast<StructNode>(typedNode)));
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

void CodeGen::setPositionAtEnd(const LLVMBasicBlockRef &basicBlock) {
    LLVMPositionBuilderAtEnd(m_Builder, basicBlock);
    m_CurrentBlock = basicBlock;
}

LLVMValueRef CodeGen::makeAdd(bool floatingPoint, const LLVMValueRef &rhs,
                              const LLVMValueRef &lhs) {
    if (floatingPoint)
        return LLVMBuildFAdd(m_Builder, lhs, rhs, "");
    return LLVMBuildAdd(m_Builder, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeMul(bool floatingPoint, const LLVMValueRef &rhs,
                              const LLVMValueRef &lhs) {
    if (floatingPoint)
        return LLVMBuildFMul(m_Builder, lhs, rhs, "");
    return LLVMBuildMul(m_Builder, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeDiv(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
                              const LLVMValueRef &lhs) {
    if (floatingPoint)
        return LLVMBuildFDiv(m_Builder, lhs, rhs, "");
    if (isSigned)
        return LLVMBuildSDiv(m_Builder, lhs, rhs, "");
    return LLVMBuildUDiv(m_Builder, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeSub(bool floatingPoint, const LLVMValueRef &rhs,
                              const LLVMValueRef &lhs) {
    if (floatingPoint)
        return LLVMBuildFSub(m_Builder, lhs, rhs, "");
    return LLVMBuildSub(m_Builder, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeRem(bool isSigned, const LLVMValueRef &rhs, const LLVMValueRef &lhs) {
    if (isSigned)
        return LLVMBuildSRem(m_Builder, lhs, rhs, "");
    return LLVMBuildURem(m_Builder, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeLT(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
                             const LLVMValueRef &lhs) {
    if (floatingPoint)
        LLVMBuildFCmp(m_Builder, LLVMRealOLT, lhs, rhs, "");
    if (isSigned)
        LLVMBuildICmp(m_Builder, LLVMIntSLT, lhs, rhs, "");
    return LLVMBuildICmp(m_Builder, LLVMIntULT, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeGT(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
                             const LLVMValueRef &lhs) {
    if (floatingPoint)
        LLVMBuildFCmp(m_Builder, LLVMRealOGT, lhs, rhs, "");
    if (isSigned)
        LLVMBuildICmp(m_Builder, LLVMIntSGT, lhs, rhs, "");
    return LLVMBuildICmp(m_Builder, LLVMIntUGT, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeLE(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
                             const LLVMValueRef &lhs) {
    if (floatingPoint)
        LLVMBuildFCmp(m_Builder, LLVMRealOLE, lhs, rhs, "");
    if (isSigned)
        LLVMBuildICmp(m_Builder, LLVMIntSLE, lhs, rhs, "");
    return LLVMBuildICmp(m_Builder, LLVMIntULE, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeGE(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
                             const LLVMValueRef &lhs) {
    if (floatingPoint)
        LLVMBuildFCmp(m_Builder, LLVMRealOGE, lhs, rhs, "");
    if (isSigned)
        LLVMBuildICmp(m_Builder, LLVMIntSGE, lhs, rhs, "");
    return LLVMBuildICmp(m_Builder, LLVMIntUGE, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeEQ(bool floatingPoint, const LLVMValueRef &rhs, const LLVMValueRef &lhs) {
    if (floatingPoint)
        return LLVMBuildFCmp(m_Builder, LLVMRealOEQ, lhs, rhs, "");
    return LLVMBuildICmp(m_Builder, LLVMIntEQ, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeNE(bool floatingPoint, const LLVMValueRef &rhs, const LLVMValueRef &lhs) {
    if (floatingPoint)
        return LLVMBuildFCmp(m_Builder, LLVMRealONE, lhs, rhs, "");
    return LLVMBuildICmp(m_Builder, LLVMIntNE, lhs, rhs, "");
}

LLVMModuleRef CodeGen::getModule() const {
    return m_Module;
}
