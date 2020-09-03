#include "codegen.h"

//
// Created by timo on 8/13/20.
//

#include "../parser/astnodes.h"
#include "../compiler/error.h"

void CodeGen::visit(const std::shared_ptr<ASTNode> &node) {
    if (node->kind == AST_ROOT) {
        CodeGen::visit(std::static_pointer_cast<RootNode>(node));
    } else if (node->kind == AST_FUNCTION) {
        CodeGen::visit(std::static_pointer_cast<FunctionNode>(node));
    } else if (node->kind == AST_RETURN) {
        CodeGen::visit(std::static_pointer_cast<ReturnNode>(node));
    } else if (node->kind == AST_STRUCT) {
        CodeGen::visit(std::static_pointer_cast<StructNode>(node));
    } else if (node->kind == AST_TYPE) {
        CodeGen::visit(std::static_pointer_cast<TypeNode>(node));
    } else if (node->kind == AST_BLOCK) {
        CodeGen::visit(std::static_pointer_cast<BlockNode>(node));
    } else if (node->kind == AST_NUMBER) {
        CodeGen::visit(std::static_pointer_cast<NumberNode>(node));
    } else if (node->kind == AST_STRING) {
        CodeGen::visit(std::static_pointer_cast<StringNode>(node));
    } else if (node->kind == AST_UNARY) {
        CodeGen::visit(std::static_pointer_cast<UnaryNode>(node));
    } else if (node->kind == AST_PARAMETER) {
        CodeGen::visit(std::static_pointer_cast<ParameterNode>(node));
    } else if (node->kind == AST_ARGUMENT) {
        CodeGen::visit(std::static_pointer_cast<ArgumentNode>(node));
    } else if (node->kind == AST_IDENTIFIER) {
        auto identifierNode = std::static_pointer_cast<IdentifierNode>(node);

        std::shared_ptr<IdentifierNode> firstIdentifier = identifierNode;
        while (firstIdentifier->lastIdentifier != nullptr)
            firstIdentifier = firstIdentifier->lastIdentifier;

        CodeGen::visit(firstIdentifier);
    } else if (node->kind == AST_ASSIGNMENT) {
        CodeGen::visit(std::static_pointer_cast<AssignmentNode>(node));
    } else if (node->kind == AST_VARIABLE) {
        CodeGen::visit(std::static_pointer_cast<VariableNode>(node));
    } else if (node->kind == AST_BINARY) {
        CodeGen::visit(std::static_pointer_cast<BinaryNode>(node));
    } else if (node->kind == AST_FUNCTION_CALL) {
        CodeGen::visit(std::static_pointer_cast<FunctionCallNode>(node));
    } else if (node->kind == AST_PARENTHESIZED) {
        CodeGen::visit(std::static_pointer_cast<ParenthesizedNode>(node));
    } else if (node->kind == AST_STRUCT_CREATE) {
        CodeGen::visit(std::static_pointer_cast<StructCreateNode>(node));
    } else if (node->kind != AST_IMPORT) {
        std::cout << "CodeGen: Unsupported node. " << node->kind << std::endl;
    }
}

void CodeGen::visit(const std::shared_ptr<RootNode> &rootNode) {
    auto moduleName = rootNode->sourcePath;
    moduleName = moduleName.substr(moduleName.rfind('/') + 1, moduleName.length());

    context = LLVMContextCreate();
    module = LLVMModuleCreateWithNameInContext(moduleName.c_str(), context);
    builder = LLVMCreateBuilderInContext(context);

    for (const auto &node : rootNode->nodes)
        CodeGen::visit(node);

    std::cout << "~~~~~~~~~~~~~~~~~~" << std::endl << std::endl;
    auto moduleCode = LLVMPrintModuleToString(module);
    std::cout << moduleName << ": " << std::endl << moduleCode << std::endl;
    LLVMDisposeMessage(moduleCode);

    LLVMDisposeModule(module);
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<FunctionNode> &functionNode) {
    auto foundIterator = functions.find(functionNode);
    if (foundIterator != functions.end())
        return foundIterator->second;

    std::vector<LLVMTypeRef> functionParameters;
    for (auto const &parameter : functionNode->parameters)
        functionParameters.push_back(CodeGen::visit(parameter->type));

    auto functionType = LLVMFunctionType(CodeGen::visit(functionNode->type),
                                         functionParameters.data(),
                                         functionParameters.size(),
                                         functionNode->isVariadic);

    if (functionNode->isNative || functionNode->isIntrinsic) {
        auto functionRef = LLVMAddFunction(module, functionNode->name->content.c_str(),
                                           functionType);
        functions.emplace(functionNode, functionRef);
        return functionRef;
    }

    auto functionRef = LLVMAddFunction(module, functionNode->name->content.c_str(), functionType);
    functions.emplace(functionNode, functionRef);
    CodeGen::visit(functionNode->block);
    return functionRef;
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<ArgumentNode> &argumentNode) {
    return CodeGen::visit(std::static_pointer_cast<TypedNode>(argumentNode->expression));
}

LLVMTypeRef CodeGen::visit(const std::shared_ptr<TypeNode> &typeNode) {
    LLVMTypeRef type;
    if (typeNode->isNumeric() && typeNode->isFloating) {
        type = typeNode->bits == 32 ? LLVMFloatTypeInContext(context)
                                    : LLVMDoubleTypeInContext(context);
    } else if (typeNode->isNumeric() && !typeNode->isFloating) {
        type = LLVMIntTypeInContext(context, typeNode->bits);
    } else if (typeNode->targetStruct != nullptr) {
        type = CodeGen::visit(typeNode->targetStruct);
    } else if (typeNode->bits == 0) {
        type = LLVMVoidTypeInContext(context);
    } else {
        std::cout << "CodeGen: Unsupported type node. " << typeNode << std::endl;
        exit(EXIT_FAILURE);
    }

    for (auto index = 0; index < typeNode->pointerLevel; index++)
        type = LLVMPointerType(type, 0);
    return type;
}

LLVMTypeRef CodeGen::visit(const std::shared_ptr<StructNode> &structNode) {
    auto foundIterator = structs.find(structNode);
    if (foundIterator != structs.end())
        return foundIterator->second;

    auto structRef = LLVMStructCreateNamed(context, structNode->name->content.c_str());
    structs.emplace(structNode, structRef);

    std::vector<LLVMTypeRef> types;
    for (auto const &variable : structNode->variables)
        types.push_back(CodeGen::visit(variable->type));
    LLVMStructSetBody(structRef, types.data(), types.size(), 0);

    return structRef;
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<ParameterNode> &parameterNode) {
    auto functionNode = parameterNode->getParent<FunctionNode>();
    auto functionRef = CodeGen::visit(functionNode);

    for (auto index = 0; index < functionNode->parameters.size(); index++) {
        auto targetParameter = functionNode->parameters.at(index);
        if (targetParameter->name->content == parameterNode->name->content)
            return LLVMGetParam(functionRef, index);
    }

    std::cout << "CodeGen: Couldn't find the parameter." << std::endl;
    exit(EXIT_FAILURE);
}

LLVMBasicBlockRef CodeGen::visit(const std::shared_ptr<BlockNode> &blockNode) {
    auto foundIterator = blocks.find(blockNode);
    if (foundIterator != blocks.end())
        return std::get<0>(foundIterator->second);

    auto functionNode = blockNode->getParent<FunctionNode>();
    auto functionRef = CodeGen::visit(functionNode);

    LLVMBasicBlockRef startBlock = LLVMAppendBasicBlockInContext(context, functionRef,
                                                                 functionNode == blockNode->parent
                                                                 ? "entry" : "");
    LLVMBasicBlockRef returnBlock = nullptr;
    LLVMValueRef returnVariable = nullptr;
    auto lastBlock = currentBlock;

    auto hasReturn = false;
    for (const auto &node : blockNode->nodes) {
        if (node->kind != AST_RETURN)
            continue;
        hasReturn = true;
        break;
    }

    CodeGen::setPositionAtEnd(startBlock);

    if (functionNode == blockNode->parent) {
        if (functionNode->type->bits != 0 || functionNode->type->targetStruct != nullptr)
            returnVariable = LLVMBuildAlloca(builder, CodeGen::visit(functionNode->type),
                                             "var_ret");

        returnBlock = LLVMAppendBasicBlockInContext(context, functionRef, "return");
        CodeGen::setPositionAtEnd(returnBlock);

        if (functionNode->type->bits != 0 || functionNode->type->targetStruct != nullptr) {
            auto loadedVariable = LLVMBuildLoad(builder, returnVariable, "loaded_ret");
            LLVMBuildRet(builder, loadedVariable);
        } else
            LLVMBuildRetVoid(builder);

        CodeGen::setPositionAtEnd(startBlock);
    }

    auto tuple = std::make_tuple(startBlock, returnVariable, returnBlock);
    blocks.emplace(blockNode, tuple);

    for (const auto &node : blockNode->nodes)
        CodeGen::visit(node);

    if (!hasReturn && functionNode == blockNode->parent)
        LLVMBuildBr(builder, returnBlock);

    if (functionNode == blockNode->parent)
        LLVMMoveBasicBlockAfter(returnBlock, LLVMGetLastBasicBlock(functionRef));

    if (lastBlock != nullptr)
        setPositionAtEnd(lastBlock);

    return startBlock;
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<ReturnNode> &returnNode) {
    auto functionNode = returnNode->getParent<FunctionNode>();
    auto blockData = blocks.find(returnNode->getParent<BlockNode>())->second;

    if (functionNode->type->bits == 0) {
        LLVMBuildBr(builder, std::get<2>(blockData));

        return LLVMGetUndef(LLVMVoidType());
    } else {
        auto expression = CodeGen::visit(
                std::static_pointer_cast<TypedNode>(returnNode->expression));
        auto returnVariable = std::get<1>(blockData);

        LLVMBuildStore(builder, expression, returnVariable);
        LLVMBuildBr(builder, std::get<2>(blockData));

        return expression;
    }
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<AssignmentNode> &assignmentNode) {
    auto variableRef = CodeGen::visit(assignmentNode->startIdentifier);
    auto expression = CodeGen::visit(std::static_pointer_cast<TypedNode>(
            assignmentNode->expression));

    LLVMBuildStore(builder, expression, variableRef);
    return expression;
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<NumberNode> &numberNode) {
    if (!numberNode->type->isFloating) {
        auto value = std::stoi(numberNode->number->content);
        return LLVMConstInt(CodeGen::visit(numberNode->type), value, numberNode->type->isSigned);
    }

    return LLVMConstRealOfString(CodeGen::visit(numberNode->type),
                                 numberNode->number->content.c_str());
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<StringNode> &stringNode) {
    auto stringConstant = LLVMConstString(stringNode->string->content.c_str(),
                                          stringNode->string->content.length(),
                                          false);

    auto stringVariable = LLVMAddGlobal(module, LLVMTypeOf(stringConstant), "");
    LLVMSetLinkage(stringVariable, LLVMPrivateLinkage);
    LLVMSetUnnamedAddr(stringVariable, LLVMGlobalUnnamedAddr);
    LLVMSetInitializer(stringVariable, stringConstant);
    LLVMSetAlignment(stringVariable, 1);

    return LLVMConstInBoundsGEP(stringVariable, &(new LLVMValueRef[2]{
            LLVMConstInt(LLVMIntTypeInContext(context, 32), 0, true),
            LLVMConstInt(LLVMIntTypeInContext(context, 32), 0, true),
    })[0], 2);
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<UnaryNode> &unaryNode) {
    auto expression = CodeGen::visit(std::static_pointer_cast<TypedNode>(unaryNode->operable));
    if (unaryNode->operatorKind == NEGATE)
        return LLVMBuildNeg(builder, expression, "");

    std::cout << "CodeGen: Unsupported unary node. " << unaryNode->kind << std::endl;
    exit(EXIT_FAILURE);
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode) {
    return CodeGen::visit(std::static_pointer_cast<TypedNode>(parenthesizedNode->expression));
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<IdentifierNode> &identifierNode) {
    auto typedTarget = std::dynamic_pointer_cast<TypedNode>(identifierNode->targetNode);
    if (typedTarget == nullptr) {
        THROW_NODE_ERROR(identifierNode->targetNode, "Can't use a non-typed node as an identifier.")
        exit(EXIT_FAILURE);
    }

    auto targetValue = CodeGen::visit(typedTarget);
    if (typedTarget->kind == AST_PARAMETER) {
        auto parameterNode = std::static_pointer_cast<ParameterNode>(typedTarget);
        auto foundIterator = parameters.find(parameterNode);
        if (foundIterator != parameters.end()) {
            targetValue = foundIterator->second;
        } else {
            auto parameterVar = LLVMBuildAlloca(builder, CodeGen::visit(typedTarget->type), "");
            LLVMBuildStore(builder, targetValue, parameterVar);
            targetValue = parameterVar;

            parameters.emplace(parameterNode, targetValue);
        }
    }

    if (identifierNode->isDereference) {
        targetValue = LLVMBuildLoad(builder, targetValue, "");
    } else if (identifierNode->isPointer) {
        targetValue = LLVMBuildGEP(builder, targetValue, new LLVMValueRef[0], 0, "");
    }

    auto targetStruct = identifierNode->type->targetStruct;
    auto currentIdentifier = identifierNode;
    while (currentIdentifier->nextIdentifier != nullptr) {
        currentIdentifier = currentIdentifier->nextIdentifier;

        auto variableNode = std::dynamic_pointer_cast<VariableNode>(currentIdentifier->targetNode);
        typedTarget = variableNode;

        if (variableNode == nullptr) {
            THROW_NODE_ERROR(identifierNode->targetNode,
                             "Can't use a non-variable node as an sub identifier.")
            exit(EXIT_FAILURE);
        }

        auto variableIndex = Utils::indexOf(targetStruct->variables, variableNode).second;
        targetValue = LLVMBuildStructGEP(builder, targetValue, variableIndex, "");

        if (currentIdentifier->isDereference) {
            targetValue = LLVMBuildLoad(builder, targetValue, "");
        } else if (currentIdentifier->isPointer) {
            targetValue = LLVMBuildGEP(builder, targetValue, nullptr, 0, "");
        }

        targetStruct = typedTarget->type->targetStruct;
    }

    if (identifierNode->parent->kind != AST_ASSIGNMENT && !currentIdentifier->isPointer)
        return LLVMBuildLoad(builder, targetValue, "");

    return targetValue;
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<BinaryNode> &binaryNode) {
    if(binaryNode->operatorKind == BIT_CAST) {
        auto lhsValue = CodeGen::visit(std::static_pointer_cast<TypedNode>(binaryNode->lhs));
        auto rhsValue = CodeGen::visit(std::static_pointer_cast<TypeNode>(binaryNode->rhs));
        return LLVMBuildBitCast(builder, lhsValue, rhsValue, "");
    } else {
        auto lhsValue = CodeGen::visit(std::static_pointer_cast<TypedNode>(binaryNode->lhs));
        auto rhsValue = CodeGen::visit(std::static_pointer_cast<TypedNode>(binaryNode->rhs));

        auto floatingPoint = binaryNode->lhs->type->isFloating || binaryNode->rhs->type->isFloating;
        auto isSigned = binaryNode->lhs->type->isSigned || binaryNode->rhs->type->isSigned;

        switch (binaryNode->operatorKind) {
            case ADDITION:
                return CodeGen::makeAdd(floatingPoint, lhsValue, rhsValue);
            case MULTIPLICATION:
                return CodeGen::makeMul(floatingPoint, lhsValue, rhsValue);
            case DIVISION:
                return CodeGen::makeDiv(floatingPoint, isSigned, lhsValue, rhsValue);
            case SUBTRACTION:
                return CodeGen::makeSub(floatingPoint, lhsValue, rhsValue);
            case REMAINING:
                return CodeGen::makeRem(isSigned, lhsValue, rhsValue);

            case LESS_THAN:
                return CodeGen::makeLT(floatingPoint, isSigned, lhsValue, rhsValue);
            case GREATER_THAN:
                return CodeGen::makeGT(floatingPoint, isSigned, lhsValue, rhsValue);
            case LESS_EQUAL_THAN:
                return CodeGen::makeLE(floatingPoint, isSigned, lhsValue, rhsValue);
            case GREATER_EQUAL_THAN:
                return CodeGen::makeGE(floatingPoint, isSigned, lhsValue, rhsValue);

            case EQUAL:
                return CodeGen::makeEQ(floatingPoint, lhsValue, rhsValue);
            case NOT_EQUAL:
                return CodeGen::makeNE(floatingPoint, lhsValue, rhsValue);

            default:
                std::cout << "CodeGen: Unsupported binary node. " << binaryNode->kind << std::endl;
                exit(EXIT_FAILURE);
        }
    }
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<FunctionCallNode> &functionCallNode) {
    auto functionNode = std::static_pointer_cast<FunctionNode>(functionCallNode->targetNode);
    auto functionRef = CodeGen::visit(functionNode);

    auto sortedArguments(functionCallNode->arguments);
    functionCallNode->getSortedArguments(functionNode, sortedArguments);

    std::vector<LLVMValueRef> functionArguments;
    for (auto index = 0; index < sortedArguments.size(); index++) {
        auto expression = CodeGen::visit(sortedArguments.at(index));
        functionArguments.insert(functionArguments.begin() + index, expression);
    }

    return LLVMBuildCall(builder, functionRef, functionArguments.data(),
                         functionArguments.size(), "");
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<StructCreateNode> &structCreateNode) {
    auto structNode = std::static_pointer_cast<StructNode>(structCreateNode->targetNode);
    auto structRef = CodeGen::visit(structNode);

    // TODO: Make it working so that you can create multiple structs inside

    std::vector<std::shared_ptr<OperableNode>> filledExpressions;
    structCreateNode->getFilledExpressions(structNode, filledExpressions);

    std::vector<std::shared_ptr<OperableNode>> originalExpressions;
    for (auto index = 0; index < structNode->variables.size(); index++) {
        originalExpressions.push_back(structNode->variables[index]->expression);
        structNode->variables[index]->expression = filledExpressions[index];
    }

    auto structVariable = LLVMBuildAlloca(builder, structRef, "");
    for (auto index = 0; index < structNode->variables.size(); index++) {
        auto variable = structNode->variables[index];
        auto variableGEP = LLVMBuildStructGEP(builder, structVariable, index, "");

        auto expression = CodeGen::visit(variable);
        LLVMBuildStore(builder, expression, variableGEP);
    }

    for (auto index = 0; index < structNode->variables.size(); index++)
        structNode->variables[index]->expression = originalExpressions[index];

    return LLVMBuildLoad(builder, structVariable, "");
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<VariableNode> &variableNode) {
    auto foundIterator = variables.find(variableNode);
    if (foundIterator != variables.end())
        return foundIterator->second;

    if (!variableNode->isLocal && variableNode->parent->kind != AST_STRUCT) {
        std::cerr << "Not yet implemented." << std::endl;
        exit(1);
    }

    bool createVariable = true;
    LLVMValueRef valueRef;
    if (variableNode->expression != nullptr) {
        valueRef = CodeGen::visit(std::static_pointer_cast<TypedNode>(variableNode->expression));
        createVariable = LLVMGetInstructionOpcode(valueRef) != LLVMAlloca;
    }

    if(createVariable) {
        auto expression = valueRef;
        valueRef = LLVMBuildAlloca(builder, CodeGen::visit(variableNode->type), "");

        if(expression != nullptr)
            LLVMBuildStore(builder, expression, valueRef);
    }

    variables.emplace(variableNode, valueRef);

    return valueRef;
}

LLVMValueRef CodeGen::visit(const std::shared_ptr<TypedNode> &typedNode) {
    if (typedNode->kind == AST_ARGUMENT) {
        return CodeGen::visit(std::static_pointer_cast<ArgumentNode>(typedNode));
    } else if (typedNode->kind == AST_ASSIGNMENT) {
        return CodeGen::visit(std::static_pointer_cast<AssignmentNode>(typedNode));
    } else if (typedNode->kind == AST_BINARY) {
        return CodeGen::visit(std::static_pointer_cast<BinaryNode>(typedNode));
    } else if (typedNode->kind == AST_FUNCTION) {
        return CodeGen::visit(std::static_pointer_cast<FunctionNode>(typedNode));
    } else if (typedNode->kind == AST_FUNCTION_CALL) {
        return CodeGen::visit(std::static_pointer_cast<FunctionCallNode>(typedNode));
    } else if (typedNode->kind == AST_IDENTIFIER) {
        auto identifierNode = std::static_pointer_cast<IdentifierNode>(typedNode);

        std::shared_ptr<IdentifierNode> firstIdentifier = identifierNode;
        while (firstIdentifier->lastIdentifier != nullptr)
            firstIdentifier = firstIdentifier->lastIdentifier;

        return CodeGen::visit(firstIdentifier);
    } else if (typedNode->kind == AST_NUMBER) {
        return CodeGen::visit(std::static_pointer_cast<NumberNode>(typedNode));
    } else if (typedNode->kind == AST_PARAMETER) {
        return CodeGen::visit(std::static_pointer_cast<ParameterNode>(typedNode));
    } else if (typedNode->kind == AST_PARENTHESIZED) {
        return CodeGen::visit(std::static_pointer_cast<ParenthesizedNode>(typedNode));
    } else if (typedNode->kind == AST_RETURN) {
        return CodeGen::visit(std::static_pointer_cast<ReturnNode>(typedNode));
    } else if (typedNode->kind == AST_STRING) {
        return CodeGen::visit(std::static_pointer_cast<StringNode>(typedNode));
    } else if (typedNode->kind == AST_STRUCT) {
        return LLVMGetUndef(CodeGen::visit(std::static_pointer_cast<StructNode>(typedNode)));
    } else if (typedNode->kind == AST_STRUCT_CREATE) {
        return CodeGen::visit(std::static_pointer_cast<StructCreateNode>(typedNode));
    } else if (typedNode->kind == AST_UNARY) {
        return CodeGen::visit(std::static_pointer_cast<UnaryNode>(typedNode));
    } else if (typedNode->kind == AST_VARIABLE) {
        return CodeGen::visit(std::static_pointer_cast<VariableNode>(typedNode));
    } else if (typedNode->kind == AST_TYPE)
        return nullptr;

    std::cout << "CodeGen: Unsupported typed node. " << typedNode->kind << std::endl;
    exit(EXIT_FAILURE);
}

void CodeGen::setPositionAtEnd(const LLVMBasicBlockRef &basicBlock) {
    LLVMPositionBuilderAtEnd(builder, basicBlock);
    currentBlock = basicBlock;
}

LLVMValueRef CodeGen::makeAdd(bool floatingPoint, const LLVMValueRef &rhs,
                              const LLVMValueRef &lhs) {
    if (floatingPoint)
        return LLVMBuildFAdd(builder, lhs, rhs, "");
    return LLVMBuildAdd(builder, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeMul(bool floatingPoint, const LLVMValueRef &rhs,
                              const LLVMValueRef &lhs) {
    if (floatingPoint)
        return LLVMBuildFMul(builder, lhs, rhs, "");
    return LLVMBuildMul(builder, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeDiv(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
                              const LLVMValueRef &lhs) {
    if (floatingPoint)
        return LLVMBuildFDiv(builder, lhs, rhs, "");
    if (isSigned)
        return LLVMBuildSDiv(builder, lhs, rhs, "");
    return LLVMBuildUDiv(builder, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeSub(bool floatingPoint, const LLVMValueRef &rhs,
                              const LLVMValueRef &lhs) {
    if (floatingPoint)
        return LLVMBuildFSub(builder, lhs, rhs, "");
    return LLVMBuildSub(builder, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeRem(bool isSigned, const LLVMValueRef &rhs, const LLVMValueRef &lhs) {
    if (isSigned)
        return LLVMBuildSRem(builder, lhs, rhs, "");
    return LLVMBuildURem(builder, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeLT(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
                             const LLVMValueRef &lhs) {
    if (floatingPoint)
        LLVMBuildFCmp(builder, LLVMRealOLT, lhs, rhs, "");
    if (isSigned)
        LLVMBuildICmp(builder, LLVMIntSLT, lhs, rhs, "");
    return LLVMBuildICmp(builder, LLVMIntULT, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeGT(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
                             const LLVMValueRef &lhs) {
    if (floatingPoint)
        LLVMBuildFCmp(builder, LLVMRealOGT, lhs, rhs, "");
    if (isSigned)
        LLVMBuildICmp(builder, LLVMIntSGT, lhs, rhs, "");
    return LLVMBuildICmp(builder, LLVMIntUGT, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeLE(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
                             const LLVMValueRef &lhs) {
    if (floatingPoint)
        LLVMBuildFCmp(builder, LLVMRealOLE, lhs, rhs, "");
    if (isSigned)
        LLVMBuildICmp(builder, LLVMIntSLE, lhs, rhs, "");
    return LLVMBuildICmp(builder, LLVMIntULE, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeGE(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
                             const LLVMValueRef &lhs) {
    if (floatingPoint)
        LLVMBuildFCmp(builder, LLVMRealOGE, lhs, rhs, "");
    if (isSigned)
        LLVMBuildICmp(builder, LLVMIntSGE, lhs, rhs, "");
    return LLVMBuildICmp(builder, LLVMIntUGE, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeEQ(bool floatingPoint, const LLVMValueRef &rhs, const LLVMValueRef &lhs) {
    if (floatingPoint)
        return LLVMBuildFCmp(builder, LLVMRealOEQ, lhs, rhs, "");
    return LLVMBuildICmp(builder, LLVMIntEQ, lhs, rhs, "");
}

LLVMValueRef CodeGen::makeNE(bool floatingPoint, const LLVMValueRef &rhs, const LLVMValueRef &lhs) {
    if (floatingPoint)
        return LLVMBuildFCmp(builder, LLVMRealONE, lhs, rhs, "");
    return LLVMBuildICmp(builder, LLVMIntNE, lhs, rhs, "");
}