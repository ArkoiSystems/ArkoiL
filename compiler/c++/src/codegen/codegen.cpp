#include "codegen.h"

//
// Created by timo on 8/13/20.
//

#include "../parser/astnodes.h"
#include "../compiler/error.h"

void CodeGen::visitNode(const std::shared_ptr<ASTNode> &node) {
    if (node->kind == AST_ROOT) {
        CodeGen::visitRoot(std::static_pointer_cast<RootNode>(node));
    } else if (node->kind == AST_FUNCTION) {
        CodeGen::visitFunction(std::static_pointer_cast<FunctionNode>(node));
    } else if (node->kind == AST_RETURN) {
        CodeGen::visitReturn(std::static_pointer_cast<ReturnNode>(node));
    } else if (node->kind == AST_STRUCT) {
        CodeGen::visitStruct(std::static_pointer_cast<StructNode>(node));
    } else if (node->kind == AST_TYPE) {
        CodeGen::visitType(std::static_pointer_cast<TypeNode>(node));
    } else if (node->kind == AST_BLOCK) {
        CodeGen::visitBlock(std::static_pointer_cast<BlockNode>(node));
    } else if (node->kind == AST_NUMBER) {
        CodeGen::visitNumber(std::static_pointer_cast<NumberNode>(node));
    } else if (node->kind == AST_STRING) {
        CodeGen::visitString(std::static_pointer_cast<StringNode>(node));
    } else if (node->kind == AST_UNARY) {
        CodeGen::visitUnary(std::static_pointer_cast<UnaryNode>(node));
    } else if (node->kind == AST_PARAMETER) {
        CodeGen::visitParameter(std::static_pointer_cast<ParameterNode>(node));
    } else if (node->kind == AST_ARGUMENT) {
        CodeGen::visitArgument(std::static_pointer_cast<ArgumentNode>(node));
    } else if (node->kind == AST_IDENTIFIER) {
        auto identifierNode = std::static_pointer_cast<IdentifierNode>(node);

        std::shared_ptr<IdentifierNode> firstIdentifier = identifierNode;
        while (firstIdentifier->lastIdentifier != nullptr)
            firstIdentifier = firstIdentifier->lastIdentifier;

        CodeGen::visitIdentifier(firstIdentifier);
    } else if (node->kind == AST_ASSIGNMENT) {
        CodeGen::visitAssignment(std::static_pointer_cast<AssignmentNode>(node));
    } else if (node->kind == AST_VARIABLE) {
        CodeGen::visitVariable(std::static_pointer_cast<VariableNode>(node));
    } else if (node->kind == AST_BINARY) {
        CodeGen::visitBinary(std::static_pointer_cast<BinaryNode>(node));
    } else if (node->kind == AST_FUNCTION_CALL) {
        CodeGen::visitFunctionCall(std::static_pointer_cast<FunctionCallNode>(node));
    } else if (node->kind == AST_PARENTHESIZED) {
        CodeGen::visitParenthesized(std::static_pointer_cast<ParenthesizedNode>(node));
    } else if (node->kind == AST_STRUCT_CREATE) {
        CodeGen::visitStructCreate(std::static_pointer_cast<StructCreateNode>(node));
    } else if (node->kind != AST_IMPORT) {
        std::cout << "CodeGen: Unsupported node. " << node->kind << std::endl;
    }
}

void CodeGen::visitRoot(const std::shared_ptr<RootNode> &node) {
    auto moduleName = node->sourcePath;
    moduleName = moduleName.substr(moduleName.rfind('/') + 1, moduleName.length());

    context = LLVMContextCreate();
    module = LLVMModuleCreateWithNameInContext(moduleName.c_str(), context);
    builder = LLVMCreateBuilderInContext(context);

    for (const auto &rootNode : node->nodes)
        CodeGen::visitNode(rootNode);

    std::cout << "~~~~~~~~~~~~~~~~~~" << std::endl << std::endl;
    auto moduleCode = LLVMPrintModuleToString(module);
    std::cout << moduleName << ": " << std::endl << moduleCode << std::endl;
    LLVMDisposeMessage(moduleCode);

    LLVMDisposeModule(module);
}

LLVMValueRef CodeGen::visitFunction(const std::shared_ptr<FunctionNode> &node) {
    auto foundIterator = functions.find(node);
    if (foundIterator != functions.end())
        return foundIterator->second;

    std::vector<LLVMTypeRef> parameters;
    for (auto const &parameter : node->parameters)
        parameters.push_back(CodeGen::visitType(parameter->type));

    auto functionType = LLVMFunctionType(CodeGen::visitType(node->type),
                                         parameters.data(),
                                         parameters.size(),
                                         node->isVariadic);
    if (node->isNative) {
        auto functionRef = LLVMAddFunction(module, node->name->content.c_str(),
                                           functionType);
        functions.emplace(node, functionRef);
        return functionRef;
    }

    // TODO: Make a builtin system
    if (!node->isBuiltin) {
        auto functionRef = LLVMAddFunction(module, node->name->content.c_str(),
                                           functionType);
        functions.emplace(node, functionRef);
        CodeGen::visitBlock(node->block);
        return functionRef;
    }

    return nullptr;
}

LLVMValueRef CodeGen::visitArgument(const std::shared_ptr<ArgumentNode> &node) {
    return CodeGen::visitTyped(node->expression);
}

LLVMTypeRef CodeGen::visitType(const std::shared_ptr<TypeNode> &node) {
    LLVMTypeRef type;
    if (node->isNumeric() && node->isFloating) {
        type = node->bits == 32 ? LLVMFloatTypeInContext(context)
                                : LLVMDoubleTypeInContext(context);
    } else if (node->isNumeric() && !node->isFloating) {
        type = LLVMIntTypeInContext(context, node->bits);
    } else if (node->targetStruct != nullptr) {
        type = CodeGen::visitStruct(node->targetStruct);
    } else if (node->bits == 0) {
        type = LLVMVoidTypeInContext(context);
    } else {
        std::cout << "CodeGen: Unsupported type node. " << node << std::endl;
        exit(EXIT_FAILURE);
    }

    for (auto index = 0; index < node->pointerLevel; index++)
        type = LLVMPointerType(type, 0);
    return type;
}

LLVMTypeRef CodeGen::visitStruct(const std::shared_ptr<StructNode> &node) {
    auto foundIterator = structs.find(node);
    if (foundIterator != structs.end())
        return foundIterator->second;

    auto structRef = LLVMStructCreateNamed(context, node->name->content.c_str());
    structs.emplace(node, structRef);

    // TODO: Make a builtin system
    if (!node->isBuiltin) {
        std::vector<LLVMTypeRef> types;
        for (auto const &variable : node->variables)
            types.push_back(CodeGen::visitType(variable->type));
        LLVMStructSetBody(structRef, types.data(), types.size(), 0);
    }

    return structRef;
}

LLVMValueRef CodeGen::visitParameter(const std::shared_ptr<ParameterNode> &parameterNode) {
    auto functionNode = parameterNode->getParent<FunctionNode>();
    auto functionRef = CodeGen::visitFunction(functionNode);

    for (auto index = 0; index < functionNode->parameters.size(); index++) {
        auto targetParameter = functionNode->parameters.at(index);
        if (std::strcmp(targetParameter->name->content.c_str(),
                        parameterNode->name->content.c_str()) == 0)
            return LLVMGetParam(functionRef, index);
    }

    std::cout << "CodeGen: Couldn't find the parameter." << std::endl;
    exit(EXIT_FAILURE);
}

LLVMBasicBlockRef CodeGen::visitBlock(const std::shared_ptr<BlockNode> &node) {
    auto foundIterator = blocks.find(node);
    if (foundIterator != blocks.end())
        return std::get<0>(foundIterator->second);

    auto functionNode = node->getParent<FunctionNode>();
    auto functionRef = CodeGen::visitFunction(functionNode);

    LLVMBasicBlockRef startBlock = LLVMAppendBasicBlockInContext(context, functionRef,
                                                                 functionNode == node->parent
                                                                 ? "entry" : "");
    LLVMBasicBlockRef returnBlock = nullptr;
    LLVMValueRef returnVariable = nullptr;

    auto hasReturn = false;
    for (const auto &blockNode : node->nodes) {
        if (blockNode->kind != AST_RETURN)
            continue;
        hasReturn = true;
        break;
    }

    CodeGen::setPositionAtEnd(startBlock);

    if (functionNode == node->parent) {
        if (functionNode->type->bits != 0)
            returnVariable = LLVMBuildAlloca(builder, CodeGen::visitType(functionNode->type),
                                             "var_ret");

        returnBlock = LLVMAppendBasicBlockInContext(context, functionRef, "return");
        CodeGen::setPositionAtEnd(returnBlock);

        if (functionNode->type->bits != 0) {
            auto loadedVariable = LLVMBuildLoad(builder, returnVariable, "loaded_ret");
            LLVMBuildRet(builder, loadedVariable);
        } else
            LLVMBuildRetVoid(builder);

        CodeGen::setPositionAtEnd(startBlock);
    }

    auto tuple = std::make_tuple(startBlock, returnVariable, returnBlock);
    blocks.emplace(node, tuple);

    for (const auto &blockNode : node->nodes)
        CodeGen::visitNode(blockNode);

    if (!hasReturn && functionNode == node->parent)
        LLVMBuildBr(builder, returnBlock);

    if (functionNode == node->parent)
        LLVMMoveBasicBlockAfter(returnBlock, LLVMGetLastBasicBlock(functionRef));

    return startBlock;
}

LLVMValueRef CodeGen::visitReturn(const std::shared_ptr<ReturnNode> &node) {
    auto functionNode = node->getParent<FunctionNode>();
    auto blockData = blocks.find(node->getParent<BlockNode>())->second;

    if (functionNode->type->bits == 0) {
        LLVMBuildBr(builder, std::get<2>(blockData));

        return LLVMGetUndef(LLVMVoidType());
    } else {
        auto expression = CodeGen::visitTyped(node->expression);
        auto returnVariable = std::get<1>(blockData);

        LLVMBuildStore(builder, expression, returnVariable);
        LLVMBuildBr(builder, std::get<2>(blockData));

        return expression;
    }
}

LLVMValueRef CodeGen::visitAssignment(const std::shared_ptr<AssignmentNode> &node) {
    auto variableRef = CodeGen::visitIdentifier(node->startIdentifier);
    auto expression = CodeGen::visitTyped(node->expression);

    LLVMBuildStore(builder, expression, variableRef);
    return expression;
}

LLVMValueRef CodeGen::visitNumber(const std::shared_ptr<NumberNode> &node) {
    if (!node->type->isFloating) {
        auto value = std::stoi(node->number->content);
        return LLVMConstInt(CodeGen::visitType(node->type), value, node->type->isSigned);
    }

    return LLVMConstRealOfString(CodeGen::visitType(node->type), node->number->content.c_str());
}

LLVMValueRef CodeGen::visitString(const std::shared_ptr<StringNode> &node) {
    auto stringConstant = LLVMConstString(node->string->content.c_str(),
                                          node->string->content.length(),
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

LLVMValueRef CodeGen::visitUnary(const std::shared_ptr<UnaryNode> &node) {
    auto expression = CodeGen::visitTyped(node->operable);
    if (node->operatorKind == NEGATE)
        return LLVMBuildNeg(builder, expression, "");

    std::cout << "CodeGen: Unsupported unary node. " << node->kind << std::endl;
    exit(EXIT_FAILURE);
}

LLVMValueRef
CodeGen::visitParenthesized(const std::shared_ptr<ParenthesizedNode> &node) {
    return CodeGen::visitTyped(node->expression);
}

LLVMValueRef CodeGen::visitIdentifier(const std::shared_ptr<IdentifierNode> &node) {
    auto typedTarget = std::dynamic_pointer_cast<TypedNode>(node->targetNode);
    if (typedTarget == nullptr) {
        THROW_NODE_ERROR(node->targetNode, "Can't use a non-typed node as an identifier.")
        exit(EXIT_FAILURE);
    }

    auto targetValue = CodeGen::visitTyped(typedTarget);
    if(typedTarget->kind == AST_PARAMETER) {
        auto parameterNode = std::static_pointer_cast<ParameterNode>(typedTarget);
        auto foundIterator = parameters.find(parameterNode);
        if (foundIterator != parameters.end()) {
            targetValue = foundIterator->second;
        } else {
            auto parameterVar = LLVMBuildAlloca(builder, CodeGen::visitType(typedTarget->type), "");
            LLVMBuildStore(builder, targetValue, parameterVar);
            targetValue = parameterVar;

            parameters.emplace(parameterNode, targetValue);
        }
    }

    if (node->isDereference) {
        targetValue = LLVMBuildLoad(builder, targetValue, "");
    } else if (node->isPointer) {
        targetValue = LLVMBuildGEP(builder, targetValue, new LLVMValueRef[0], 0, "");
    }

    auto targetStruct = node->type->targetStruct;
    auto currentIdentifier = node;
    while (currentIdentifier->nextIdentifier != nullptr) {
        currentIdentifier = currentIdentifier->nextIdentifier;

        auto variableNode = std::dynamic_pointer_cast<VariableNode>(currentIdentifier->targetNode);
        typedTarget = variableNode;

        if (variableNode == nullptr) {
            THROW_NODE_ERROR(node->targetNode,
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

    if(node->parent->kind != AST_ASSIGNMENT && !currentIdentifier->isPointer)
        return LLVMBuildLoad(builder, targetValue, "");

    return targetValue;
//    std::cout << (node->parent->kind == AST_ASSIGNMENT) << std::endl;
//
//    std::cout << "~~~~~~~~~~~~~~~~~~" << std::endl << std::endl;
//    auto moduleCode = LLVMPrintModuleToString(module);
//    std::cout << std::endl << moduleCode << std::endl;
//    LLVMDisposeMessage(moduleCode);
//
//    exit(EXIT_FAILURE);
}

LLVMValueRef CodeGen::visitBinary(const std::shared_ptr<BinaryNode> &binaryNode) {
    return nullptr;
//    auto lhsValue = CodeGen::visitOperable(binaryNode->lhs);
//    auto rhsValue = CodeGen::visitOperable(binaryNode->rhs);
//
//    auto floatingPoint = binaryNode->lhs->type->isFloating || binaryNode->rhs->type->isFloating;
//    auto isSigned = !floatingPoint &&
//                    (binaryNode->lhs->type->isSigned || binaryNode->rhs->type->isSigned);
//
//    // TODO: Move the unit translation to the typeresolver.cpp
//    if (floatingPoint) {
//        if (!binaryNode->lhs->type->isFloating) {
//            lhsValue = CodeGen::makeIntToFP(binaryNode->lhs->type,
//                                            CodeGen::visitType(binaryNode->rhs->type),
//                                            lhsValue);
//        } else {
//            rhsValue = CodeGen::makeIntToFP(binaryNode->rhs->type,
//                                            CodeGen::visitType(binaryNode->lhs->type),
//                                            rhsValue);
//        }
//    } else {
//        if (!binaryNode->lhs->type->isSigned) {
//            lhsValue = LLVMBuildIntCast(builder, lhsValue,
//                                        CodeGen::visitType(binaryNode->rhs->type), "");
//        } else {
//            rhsValue = LLVMBuildIntCast(builder, rhsValue,
//                                        CodeGen::visitType(binaryNode->lhs->type), "");
//        }
//    }
//
//    switch (binaryNode->operatorKind) {
//        case ADDITION:
//            return CodeGen::makeAdd(floatingPoint, lhsValue, rhsValue);
//        case MULTIPLICATION:
//            return CodeGen::makeMul(floatingPoint, lhsValue, rhsValue);
//        case DIVISION:
//            return CodeGen::makeDiv(floatingPoint, isSigned, lhsValue, rhsValue);
//        case SUBTRACTION:
//            return CodeGen::makeSub(floatingPoint, lhsValue, rhsValue);
//        case REMAINING:
//            return CodeGen::makeRem(isSigned, lhsValue, rhsValue);
//
//        case LESS_THAN:
//            return CodeGen::makeLT(floatingPoint, isSigned, lhsValue, rhsValue);
//        case GREATER_THAN:
//            return CodeGen::makeGT(floatingPoint, isSigned, lhsValue, rhsValue);
//        case LESS_EQUAL_THAN:
//            return CodeGen::makeLE(floatingPoint, isSigned, lhsValue, rhsValue);
//        case GREATER_EQUAL_THAN:
//            return CodeGen::makeGE(floatingPoint, isSigned, lhsValue, rhsValue);
//
//        case EQUAL:
//            return CodeGen::makeEQ(floatingPoint, lhsValue, rhsValue);
//        case NOT_EQUAL:
//            return CodeGen::makeNE(floatingPoint, lhsValue, rhsValue);
//
//        default:
//            std::cout << "CodeGen: Unsupported binary node. " << binaryNode->kind << std::endl;
//            exit(EXIT_FAILURE);
//    }
}

LLVMValueRef CodeGen::visitFunctionCall(const std::shared_ptr<FunctionCallNode> &functionCallNode) {
    return nullptr;
//    auto functionNode = std::static_pointer_cast<FunctionNode>(functionCallNode->targetNode);
//    // TODO: Make builtin function test
//
//    auto functionRef = CodeGen::visitFunction(functionNode);
//    auto sortedArguments = functionCallNode->getSortedArguments();
//    std::vector<LLVMValueRef> functionArguments(sortedArguments.size());
//
//    for (auto index = 0; index < functionArguments.size(); index++) {
//        auto argument = sortedArguments.at(index);
//        auto expression = CodeGen::visitOperable(argument->expression);
//        functionArguments.insert(functionArguments.begin() + index, expression);
//    }
//
//    return LLVMBuildCall(builder, functionRef, functionArguments.data(),
//                         functionArguments.size(), "");
}

LLVMValueRef CodeGen::visitStructCreate(const std::shared_ptr<StructCreateNode> &structCreateNode) {
    return nullptr;
//    auto structNode = std::static_pointer_cast<StructNode>(structCreateNode->targetNode);
//    auto structRef = CodeGen::visitStruct(structNode);
//
//    // TODO: Make it working so that you can create multiple structs inside
//
//    auto structVariable = LLVMBuildAlloca(builder, structRef, "");
//    auto sortedArguments = structCreateNode->getSortedArguments();
//
//    for(auto index = 0; index < sortedArguments.size(); index++) {
//        auto argument = sortedArguments.at(index);
//        auto variableGEP = LLVMBuildStructGEP(builder, structVariable, index, "")
//
//    }
//
//    return LLVMBuildLoad(builder, structVariable, "");
}

LLVMValueRef CodeGen::visitVariable(const std::shared_ptr<VariableNode> &variableNode) {
    return nullptr;
//    auto foundIterator = variables.find(variableNode);
//    if (foundIterator != variables.end())
//        return foundIterator->second;
//
//    return nullptr;
}

LLVMValueRef CodeGen::visitTyped(const std::shared_ptr<TypedNode> &node) {
    if (node->kind == AST_ARGUMENT) {
        return CodeGen::visitArgument(std::static_pointer_cast<ArgumentNode>(node));
    } else if (node->kind == AST_ASSIGNMENT) {
        return CodeGen::visitAssignment(std::static_pointer_cast<AssignmentNode>(node));
    } else if (node->kind == AST_BINARY) {
        return CodeGen::visitBinary(std::static_pointer_cast<BinaryNode>(node));
    } else if (node->kind == AST_FUNCTION) {
        return CodeGen::visitFunction(std::static_pointer_cast<FunctionNode>(node));
    } else if (node->kind == AST_FUNCTION_CALL) {
        return CodeGen::visitFunctionCall(std::static_pointer_cast<FunctionCallNode>(node));
    } else if (node->kind == AST_IDENTIFIER) {
        auto identifierNode = std::static_pointer_cast<IdentifierNode>(node);

        std::shared_ptr<IdentifierNode> firstIdentifier = identifierNode;
        while (firstIdentifier->lastIdentifier != nullptr)
            firstIdentifier = firstIdentifier->lastIdentifier;

        return CodeGen::visitIdentifier(firstIdentifier);
    } else if (node->kind == AST_NUMBER) {
        return CodeGen::visitNumber(std::static_pointer_cast<NumberNode>(node));
    } else if (node->kind == AST_PARAMETER) {
        return CodeGen::visitParameter(std::static_pointer_cast<ParameterNode>(node));
    } else if (node->kind == AST_PARENTHESIZED) {
        return CodeGen::visitParenthesized(std::static_pointer_cast<ParenthesizedNode>(node));
    } else if (node->kind == AST_RETURN) {
        return CodeGen::visitReturn(std::static_pointer_cast<ReturnNode>(node));
    } else if (node->kind == AST_STRING) {
        return CodeGen::visitString(std::static_pointer_cast<StringNode>(node));
    } else if (node->kind == AST_STRUCT) {
        return LLVMGetUndef(CodeGen::visitStruct(std::static_pointer_cast<StructNode>(node)));
    } else if (node->kind == AST_STRUCT_CREATE) {
        return CodeGen::visitStructCreate(std::static_pointer_cast<StructCreateNode>(node));
    } else if (node->kind == AST_UNARY) {
        return CodeGen::visitUnary(std::static_pointer_cast<UnaryNode>(node));
    } else if (node->kind == AST_VARIABLE) {
        return CodeGen::visitVariable(std::static_pointer_cast<VariableNode>(node));
    }

    std::cout << "CodeGen: Unsupported typed node. " << node->kind << std::endl;
    exit(EXIT_FAILURE);
}

void CodeGen::setPositionAtEnd(const LLVMBasicBlockRef &basicBlock) {
    LLVMPositionBuilderAtEnd(builder, basicBlock);
    currentBlock = basicBlock;
}

LLVMValueRef CodeGen::makeIntToFP(const std::shared_ptr<TypeNode> &typeNode,
                                  const LLVMTypeRef &target,
                                  const LLVMValueRef &value) {
    if (typeNode->isSigned)
        return LLVMBuildSIToFP(builder, value, target, "");
    return LLVMBuildUIToFP(builder, value, target, "");
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
        return LLVMBuildFAdd(builder, lhs, rhs, "");
    return LLVMBuildAdd(builder, lhs, rhs, "");
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
