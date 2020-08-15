////
//// Created by timo on 8/13/20.
////
//
//#include "codegen.h"
//#include "../parser/astnodes.h"
//
////AST_ROOT,
////AST_IMPORT,
////AST_FUNCTION,
////AST_PARAMETER,
////AST_TYPE,
////AST_BLOCK,
////AST_VARIABLE,
////AST_BINARY,
////AST_UNARY,
////AST_PARENTHESIZED,
////AST_NUMBER,
////AST_STRING,
////AST_IDENTIFIER,
////AST_ARGUMENT,
////AST_FUNCTION_CALL,
////AST_STRUCT_CREATE,
////AST_ASSIGNMENT,
////AST_RETURN,
////AST_STRUCT,
//
//void CodeGen::visitNode(const std::shared_ptr<ASTNode> &node) {
//    if (node->kind == AST_ROOT) {
//        CodeGen::visitRoot(std::static_pointer_cast<RootNode>(node));
//    } else if (node->kind == AST_FUNCTION) {
//        CodeGen::visitFunction(std::static_pointer_cast<FunctionNode>(node));
//    } else if (node->kind == AST_TYPE) {
//        CodeGen::visitType(std::static_pointer_cast<TypeNode>(node));
//    } else if (node->kind == AST_STRUCT) {
//        CodeGen::visitStruct(std::static_pointer_cast<StructNode>(node));
//    } else if (node->kind == AST_PARAMETER) {
//        CodeGen::visitParameter(std::static_pointer_cast<ParameterNode>(node));
//    } else if (node->kind == AST_BLOCK) {
//        CodeGen::visitBlock(std::static_pointer_cast<BlockNode>(node));
//    } else if (node->kind == AST_VARIABLE) {
//        CodeGen::visitVariable(std::static_pointer_cast<VariableNode>(node));
//    }  else if (node->kind == AST_ARGUMENT) {
//        CodeGen::visitArgument(std::static_pointer_cast<ArgumentNode>(node));
//    }else if (node->kind == AST_RETURN) {
//        CodeGen::visitReturn(std::static_pointer_cast<ReturnNode>(node));
//    } else if (node->kind == AST_ASSIGNMENT) {
//        CodeGen::visitAssignment(std::static_pointer_cast<AssignmentNode>(node));
//    } else if (node->kind == AST_IDENTIFIER) {
//        CodeGen::visitIdentifier(std::static_pointer_cast<IdentifierNode>(node));
//    } else if (node->kind == AST_NUMBER) {
//        CodeGen::visitNumber(std::static_pointer_cast<NumberNode>(node));
//    } else if (node->kind == AST_STRING) {
//        CodeGen::visitString(std::static_pointer_cast<StringNode>(node));
//    } else if(node->kind == AST_BINARY) {
//        CodeGen::visitBinary(std::static_pointer_cast<BinaryNode>(node));
//    } else if(node->kind == AST_FUNCTION_CALL) {
//        CodeGen::visitFunctionCall(std::static_pointer_cast<FunctionCallNode>(node));
//    }  else if(node->kind == AST_PARENTHESIZED) {
//        CodeGen::visitParenthesized(std::static_pointer_cast<ParenthesizedNode>(node));
//    }  else if(node->kind == AST_STRUCT_CREATE) {
//        CodeGen::visitStructCreate(std::static_pointer_cast<StructCreateNode>(node));
//    }  else if(node->kind == AST_UNARY) {
//        CodeGen::visitUnary(std::static_pointer_cast<UnaryNode>(node));
//    }  else if (node->kind != AST_IMPORT) {
//        std::cout << "CodeGen: Unsupported node. " << node->kind << std::endl;
//    }
//}
//
//void CodeGen::visitRoot(const std::shared_ptr<RootNode> &rootNode) {
//    auto moduleName = rootNode->sourcePath;
//    moduleName = moduleName.substr(moduleName.rfind('/') + 1, moduleName.length());
//
//    context = LLVMContextCreate();
//    module = LLVMModuleCreateWithNameInContext(moduleName.c_str(), context);
//    builder = LLVMCreateBuilderInContext(context);
//
//    for (const auto &node : rootNode->nodes)
//        CodeGen::visitNode(node);
//
//    std::cout << "~~~~~~~~~~~~~~~~~~" << std::endl;
//    auto moduleCode = LLVMPrintModuleToString(module);
//    std::cout << moduleName << ": " << std::endl << moduleCode << std::endl;
//    LLVMDisposeMessage(moduleCode);
//
//    LLVMDisposeModule(module);
//}
//
//LLVMValueRef CodeGen::visitFunction(const std::shared_ptr<FunctionNode> &functionNode) {
//    auto foundIterator = functions.find(functionNode);
//    if (foundIterator != functions.end())
//        return foundIterator->second;
//
//    std::vector<LLVMTypeRef> parameters;
//    for (auto const &parameter : functionNode->parameters)
//        parameters.push_back(CodeGen::visitType(parameter->type));
//
//    auto functionType = LLVMFunctionType(CodeGen::visitType(functionNode->type),
//                                         parameters.data(),
//                                         parameters.size(),
//                                         functionNode->isVariadic);
//    if (functionNode->isNative) {
//        auto functionRef = LLVMAddFunction(module, functionNode->name->content.c_str(),
//                                           functionType);
//        functions.emplace(functionNode, functionRef);
//        return functionRef;
//    }
//
//    // TODO: Make a builtin system
//    if (!functionNode->isBuiltin) {
//        auto functionRef = LLVMAddFunction(module, functionNode->name->content.c_str(),
//                                           functionType);
//        functions.emplace(functionNode, functionRef);
//        CodeGen::visitBlock(functionNode->block);
//        return functionRef;
//    }
//
//    return nullptr;
//}
//
//LLVMTypeRef CodeGen::visitType(const std::shared_ptr<TypeNode> &typeNode) {
//    LLVMTypeRef type;
//    if (typeNode->isNumeric() && typeNode->isFloating) {
//        type = typeNode->bits == 32 ? LLVMFloatTypeInContext(context)
//                                    : LLVMDoubleTypeInContext(context);
//    } else if (typeNode->isNumeric() && !typeNode->isFloating) {
//        type = LLVMIntTypeInContext(context, typeNode->bits);
//    } else if (typeNode->targetStruct != nullptr) {
//        type = CodeGen::visitStruct(typeNode->targetStruct);
//    } else if (typeNode->bits == 0) {
//        type = LLVMVoidTypeInContext(context);
//    } else {
//        std::cout << "CodeGen: Unsupported type node. " << typeNode << std::endl;
//        exit(EXIT_FAILURE);
//    }
//
//    for (auto index = 0; index < typeNode->pointerLevel; index++)
//        type = LLVMPointerType(type, 0);
//    return type;
//}
//
//LLVMTypeRef CodeGen::visitStruct(const std::shared_ptr<StructNode> &structNode) {
//    auto foundIterator = structs.find(structNode);
//    if (foundIterator != structs.end())
//        return foundIterator->second;
//
//    auto structRef = LLVMStructCreateNamed(context, structNode->name->content.c_str());
//    structs.emplace(structNode, structRef);
//
//    // TODO: Make a builtin system
//    if (!structNode->isBuiltin) {
//        std::vector<LLVMTypeRef> types;
//        for (auto const &variable : structNode->variables)
//            types.push_back(CodeGen::visitType(variable->type));
//        LLVMStructSetBody(structRef, types.data(), types.size(), 0);
//    }
//
//    return structRef;
//}
//
//LLVMValueRef CodeGen::visitParameter(const std::shared_ptr<ParameterNode> &parameterNode) {
//    auto functionNode = parameterNode->getParent<FunctionNode>();
//    auto functionRef = CodeGen::visitFunction(functionNode);
//
//    for (auto index = 0; index < functionNode->parameters.size(); index++) {
//        auto targetParameter = functionNode->parameters.at(index);
//        if (std::strcmp(targetParameter->name->content.c_str(),
//                        parameterNode->name->content.c_str()) == 0)
//            return LLVMGetParam(functionRef, index);
//    }
//
//    std::cout << "CodeGen: Couldn't find the parameter." << std::endl;
//    exit(EXIT_FAILURE);
//}
//
//LLVMBasicBlockRef CodeGen::visitBlock(const std::shared_ptr<BlockNode> &blockNode) {
//    auto foundIterator = blocks.find(blockNode);
//    if (foundIterator != blocks.end())
//        return std::get<0>(foundIterator->second);
//
//    auto functionNode = blockNode->getParent<FunctionNode>();
//    auto functionRef = CodeGen::visitFunction(functionNode);
//
//    LLVMBasicBlockRef startBlock = LLVMAppendBasicBlockInContext(context, functionRef,
//                                                                 functionNode == blockNode->parent
//                                                                 ? "entry" : "");
//    LLVMBasicBlockRef returnBlock = nullptr;
//    LLVMValueRef returnVariable = nullptr;
//
//    auto hasReturn = false;
//    for (const auto &node : blockNode->nodes) {
//        if (node->kind != AST_RETURN)
//            continue;
//        hasReturn = true;
//        break;
//    }
//
//    CodeGen::setPositionAtEnd(startBlock);
//
//    if (functionNode == blockNode->parent) {
//        if (functionNode->type->bits != 0)
//            returnVariable = LLVMBuildAlloca(builder, CodeGen::visitType(functionNode->type),
//                                             "ret_var");
//
//        returnBlock = LLVMAppendBasicBlockInContext(context, functionRef, "ret_block");
//        CodeGen::setPositionAtEnd(returnBlock);
//
//        if (functionNode->type->bits != 0) {
//            auto loadedVariable = LLVMBuildLoad(builder, returnVariable, "loaded_ret");
//            LLVMBuildRet(builder, loadedVariable);
//        } else
//            LLVMBuildRetVoid(builder);
//
//        CodeGen::setPositionAtEnd(startBlock);
//    }
//
//    auto tuple = std::make_tuple(startBlock, returnVariable, returnBlock);
//    blocks.emplace(blockNode, tuple);
//
//    for (const auto &node : blockNode->nodes)
//        CodeGen::visitNode(node);
//
//    if (!hasReturn && functionNode == blockNode->parent)
//        LLVMBuildBr(builder, returnBlock);
//
//    if (functionNode == blockNode->parent)
//        LLVMMoveBasicBlockAfter(returnBlock, LLVMGetLastBasicBlock(functionRef));
//
//    return startBlock;
//}
//
//void CodeGen::visitReturn(const std::shared_ptr<ReturnNode> &returnNode) {
//    auto functionNode = returnNode->getParent<FunctionNode>();
//    auto blockData = blocks.find(returnNode->getParent<BlockNode>())->second;
//
//    if (functionNode->type->bits == 0) {
//        LLVMBuildBr(builder, std::get<2>(blockData));
//    } else {
//        auto expression = CodeGen::visitOperable(returnNode->expression);
//        auto returnVariable = std::get<1>(blockData);
//
//        LLVMBuildStore(builder, expression, returnVariable);
//        LLVMBuildBr(builder, std::get<2>(blockData));
//    }
//}
//
//LLVMValueRef CodeGen::visitOperable(const std::shared_ptr<OperableNode> &operableNode) {
//    if (operableNode->kind == AST_ASSIGNMENT) {
//        return CodeGen::visitAssignment(std::static_pointer_cast<AssignmentNode>(operableNode));
//    } else if (operableNode->kind == AST_BINARY) {
//        return CodeGen::visitBinary(std::static_pointer_cast<BinaryNode>(operableNode));
//    } else if (operableNode->kind == AST_FUNCTION_CALL) {
//        return CodeGen::visitFunctionCall(std::static_pointer_cast<FunctionCallNode>(operableNode));
//    } else if (operableNode->kind == AST_IDENTIFIER) {
//        return CodeGen::visitIdentifier(std::static_pointer_cast<IdentifierNode>(operableNode));
//    } else if (operableNode->kind == AST_NUMBER) {
//        return CodeGen::visitNumber(std::static_pointer_cast<NumberNode>(operableNode));
//    } else if (operableNode->kind == AST_PARENTHESIZED) {
//        return CodeGen::visitParenthesized(
//                std::static_pointer_cast<ParenthesizedNode>(operableNode));
//    } else if (operableNode->kind == AST_STRING) {
//        return CodeGen::visitString(std::static_pointer_cast<StringNode>(operableNode));
//    } else if (operableNode->kind == AST_STRUCT_CREATE) {
//        return CodeGen::visitStructCreate(std::static_pointer_cast<StructCreateNode>(operableNode));
//    } else if (operableNode->kind == AST_UNARY) {
//        return CodeGen::visitUnary(std::static_pointer_cast<UnaryNode>(operableNode));
//    }
//
//    std::cout << "CodeGen: Unsupported operable node. " << operableNode->kind << std::endl;
//    exit(EXIT_FAILURE);
//}
//
//LLVMValueRef CodeGen::visitAssignment(const std::shared_ptr<AssignmentNode> &assignmentNode) {
//    auto variableRef = CodeGen::visitIdentifier(assignmentNode->startIdentifier);
//    auto expression = CodeGen::visitOperable(assignmentNode->expression);
//
//    LLVMBuildStore(builder, expression, variableRef);
//    return expression;
//}
//
//LLVMValueRef CodeGen::visitIdentifier(const std::shared_ptr<IdentifierNode> &identifierNode) {
//    return nullptr
//}
//
//LLVMValueRef CodeGen::visitNumber(const std::shared_ptr<NumberNode> &numberNode) {
//    if (!numberNode->type->isFloating) {
//        auto value = std::stoi(numberNode->number->content);
//        return LLVMConstInt(CodeGen::visitType(numberNode->type), value,
//                            numberNode->type->isSigned);
//    } else {
//        return LLVMConstRealOfString(CodeGen::visitType(numberNode->type),
//                                     numberNode->number->content.c_str());
//    }
//}
//
//LLVMValueRef CodeGen::visitString(const std::shared_ptr<StringNode> &stringNode) {
//    auto stringConstant = LLVMConstString(stringNode->string->content.c_str(),
//                                          stringNode->string->content.length(),
//                                          false);
//
//    auto stringVariable = LLVMAddGlobal(module, LLVMTypeOf(stringConstant), "");
//    LLVMSetLinkage(stringVariable, LLVMPrivateLinkage);
//    LLVMSetUnnamedAddr(stringVariable, LLVMGlobalUnnamedAddr);
//    LLVMSetInitializer(stringVariable, stringConstant);
//    LLVMSetAlignment(stringVariable, 1);
//
//    return LLVMConstInBoundsGEP(stringVariable, &(new LLVMValueRef[]{
//            LLVMConstInt(LLVMIntTypeInContext(context, 32), 0, true),
//            LLVMConstInt(LLVMIntTypeInContext(context, 32), 0, true),
//    })[0], 2);
//}
//
//LLVMValueRef CodeGen::visitBinary(const std::shared_ptr<BinaryNode> &binaryNode) {
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
//}
//
//LLVMValueRef CodeGen::visitUnary(const std::shared_ptr<UnaryNode> &unaryNode) {
//    auto expression = CodeGen::visitOperable(unaryNode->operable);
//    if (unaryNode->operatorKind == NEGATE)
//        return LLVMBuildNeg(builder, expression, "");
//
//    std::cout << "CodeGen: Unsupported unary node. " << unaryNode->kind << std::endl;
//    exit(EXIT_FAILURE);
//}
//
//LLVMValueRef
//CodeGen::visitParenthesized(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode) {
//    return CodeGen::visitOperable(parenthesizedNode->expression);
//}
//
//LLVMValueRef CodeGen::visitFunctionCall(const std::shared_ptr<FunctionCallNode> &functionCallNode) {
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
//}
//
//LLVMValueRef CodeGen::visitStructCreate(const std::shared_ptr<StructCreateNode> &structCreateNode) {
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
//}
//
//
//LLVMValueRef CodeGen::visitArgument(const std::shared_ptr<ArgumentNode> &argumentNode) {
//    return CodeGen::visitOperable(argumentNode->expression);
//}
//
//LLVMValueRef CodeGen::visitVariable(const std::shared_ptr<VariableNode> &variableNode) {
//    auto foundIterator = variables.find(variableNode);
//    if (foundIterator != variables.end())
//        return foundIterator->second;
//
//    return nullptr;
//}
//
//void CodeGen::setPositionAtEnd(const LLVMBasicBlockRef &basicBlock) {
//    LLVMPositionBuilderAtEnd(builder, basicBlock);
//    currentBlock = basicBlock;
//}
//
//LLVMValueRef CodeGen::makeIntToFP(const std::shared_ptr<TypeNode> &typeNode,
//                                  const LLVMTypeRef &target,
//                                  const LLVMValueRef &value) {
//    if (typeNode->isSigned)
//        return LLVMBuildSIToFP(builder, value, target, "");
//    return LLVMBuildUIToFP(builder, value, target, "");
//}
//
//LLVMValueRef CodeGen::makeAdd(bool floatingPoint, const LLVMValueRef &rhs,
//                              const LLVMValueRef &lhs) {
//    if (floatingPoint)
//        return LLVMBuildFAdd(builder, lhs, rhs, "");
//    return LLVMBuildAdd(builder, lhs, rhs, "");
//}
//
//LLVMValueRef CodeGen::makeMul(bool floatingPoint, const LLVMValueRef &rhs,
//                              const LLVMValueRef &lhs) {
//    if (floatingPoint)
//        return LLVMBuildFAdd(builder, lhs, rhs, "");
//    return LLVMBuildAdd(builder, lhs, rhs, "");
//}
//
//LLVMValueRef CodeGen::makeDiv(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
//                              const LLVMValueRef &lhs) {
//    if (floatingPoint)
//        return LLVMBuildFDiv(builder, lhs, rhs, "");
//    if (isSigned)
//        return LLVMBuildSDiv(builder, lhs, rhs, "");
//    return LLVMBuildUDiv(builder, lhs, rhs, "");
//}
//
//LLVMValueRef CodeGen::makeSub(bool floatingPoint, const LLVMValueRef &rhs,
//                              const LLVMValueRef &lhs) {
//    if (floatingPoint)
//        return LLVMBuildFSub(builder, lhs, rhs, "");
//    return LLVMBuildSub(builder, lhs, rhs, "");
//}
//
//LLVMValueRef CodeGen::makeRem(bool isSigned, const LLVMValueRef &rhs, const LLVMValueRef &lhs) {
//    if (isSigned)
//        return LLVMBuildSRem(builder, lhs, rhs, "");
//    return LLVMBuildURem(builder, lhs, rhs, "");
//}
//
//LLVMValueRef CodeGen::makeLT(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
//                             const LLVMValueRef &lhs) {
//    if (floatingPoint)
//        LLVMBuildFCmp(builder, LLVMRealOLT, lhs, rhs, "");
//    if (isSigned)
//        LLVMBuildICmp(builder, LLVMIntSLT, lhs, rhs, "");
//    return LLVMBuildICmp(builder, LLVMIntULT, lhs, rhs, "");
//}
//
//LLVMValueRef CodeGen::makeGT(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
//                             const LLVMValueRef &lhs) {
//    if (floatingPoint)
//        LLVMBuildFCmp(builder, LLVMRealOGT, lhs, rhs, "");
//    if (isSigned)
//        LLVMBuildICmp(builder, LLVMIntSGT, lhs, rhs, "");
//    return LLVMBuildICmp(builder, LLVMIntUGT, lhs, rhs, "");
//}
//
//LLVMValueRef CodeGen::makeLE(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
//                             const LLVMValueRef &lhs) {
//    if (floatingPoint)
//        LLVMBuildFCmp(builder, LLVMRealOLE, lhs, rhs, "");
//    if (isSigned)
//        LLVMBuildICmp(builder, LLVMIntSLE, lhs, rhs, "");
//    return LLVMBuildICmp(builder, LLVMIntULE, lhs, rhs, "");
//}
//
//LLVMValueRef CodeGen::makeGE(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
//                             const LLVMValueRef &lhs) {
//    if (floatingPoint)
//        LLVMBuildFCmp(builder, LLVMRealOGE, lhs, rhs, "");
//    if (isSigned)
//        LLVMBuildICmp(builder, LLVMIntSGE, lhs, rhs, "");
//    return LLVMBuildICmp(builder, LLVMIntUGE, lhs, rhs, "");
//}
//
//LLVMValueRef CodeGen::makeEQ(bool floatingPoint, const LLVMValueRef &rhs, const LLVMValueRef &lhs) {
//    if (floatingPoint)
//        return LLVMBuildFCmp(builder, LLVMRealOEQ, lhs, rhs, "");
//    return LLVMBuildICmp(builder, LLVMIntEQ, lhs, rhs, "");
//}
//
//LLVMValueRef CodeGen::makeNE(bool floatingPoint, const LLVMValueRef &rhs, const LLVMValueRef &lhs) {
//    if (floatingPoint)
//        return LLVMBuildFCmp(builder, LLVMRealONE, lhs, rhs, "");
//    return LLVMBuildICmp(builder, LLVMIntNE, lhs, rhs, "");
//}
