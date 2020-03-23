/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.AssignmentOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.PrintStream;
import java.util.Optional;

public class AssignmentExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private AbstractOperableSyntaxAST<?> leftSideOperable;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private AssignmentOperatorType assignmentOperatorType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private AbstractOperableSyntaxAST<?> rightSideOperable;
    
    
    protected AssignmentExpressionSyntaxAST(@NonNull final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ASSIGNMENT_EXPRESSION);
    }
    
    
    public AssignmentExpressionSyntaxAST(@NonNull final SyntaxAnalyzer syntaxAnalyzer, @NonNull final AbstractOperableSyntaxAST<?> leftSideOperable, @NonNull final AssignmentOperatorType assignmentOperatorType) {
        super(syntaxAnalyzer, ASTType.ASSIGNMENT_EXPRESSION);
        
        this.assignmentOperatorType = assignmentOperatorType;
        this.leftSideOperable = leftSideOperable;
        
        this.setStart(this.leftSideOperable.getStart());
    }
    
    
    @Override
    public Optional<? extends AbstractOperableSyntaxAST<?>> parseAST(@NonNull final AbstractSyntaxAST parentAST) {
        if (this.getAssignmentOperatorType() == AssignmentOperatorType.ASSIGN) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.EQUAL) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken().getStart(),
                        this.getSyntaxAnalyzer().peekToken(1).getEnd(),
                        "Couldn't parse the assignment expression because the left side expression isn't followed by an equal sign."
                );
                return Optional.empty();
            }
            this.getSyntaxAnalyzer().nextToken();
        } else {
            if (this.getAssignmentOperatorType() == AssignmentOperatorType.ADD_ASSIGN) {
                if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PLUS) == null) {
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            this.getSyntaxAnalyzer().currentToken().getStart(),
                            this.getSyntaxAnalyzer().peekToken(1).getEnd(),
                            "Couldn't parse the add assignment expression because the left side expression isn't followed by a plus."
                    );
                    return Optional.empty();
                }
            } else if (this.getAssignmentOperatorType() == AssignmentOperatorType.SUB_ASSIGN) {
                if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.MINUS) == null) {
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            this.getSyntaxAnalyzer().currentToken().getStart(),
                            this.getSyntaxAnalyzer().peekToken(1).getEnd(),
                            "Couldn't parse the sub assignment expression because the left side expression isn't followed by a minus."
                    );
                    return Optional.empty();
                }
            } else if (this.getAssignmentOperatorType() == AssignmentOperatorType.MUL_ASSIGN) {
                if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.ASTERISK) == null) {
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            this.getSyntaxAnalyzer().currentToken().getStart(),
                            this.getSyntaxAnalyzer().peekToken(1).getEnd(),
                            "Couldn't parse the mul assignment expression because the left side expression isn't followed by an asterisk."
                    );
                    return Optional.empty();
                }
            } else if (this.getAssignmentOperatorType() == AssignmentOperatorType.DIV_ASSIGN) {
                if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.SLASH) == null) {
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            this.getSyntaxAnalyzer().currentToken().getStart(),
                            this.getSyntaxAnalyzer().peekToken(1).getEnd(),
                            "Couldn't parse the div assignment expression because the left side expression isn't followed by a slash."
                    );
                    return Optional.empty();
                }
            } else if (this.getAssignmentOperatorType() == AssignmentOperatorType.MOD_ASSIGN) {
                if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PERCENT) == null) {
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            this.getSyntaxAnalyzer().currentToken().getStart(),
                            this.getSyntaxAnalyzer().peekToken(1).getEnd(),
                            "Couldn't parse the mod assignment expression because the left side expression isn't followed by a percent sign."
                    );
                    return Optional.empty();
                }
            }
            this.getSyntaxAnalyzer().nextToken();
            
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.EQUAL) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken().getStart(),
                        this.getSyntaxAnalyzer().peekToken(1).getEnd(),
                        "Couldn't parse the assignment expression because the the first operator isn't followed by an equal sign."
                );
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.EQUAL, false) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken().getStart(),
                        this.getSyntaxAnalyzer().peekToken(1, false).getEnd(),
                        "Couldn't parse the assignment expression because the the first operator and the equal sign is separated by an whitespace."
                );
            }
            this.getSyntaxAnalyzer().nextToken(2);
        }
        
        final Optional<? extends AbstractOperableSyntaxAST<?>> optionalRightSideAST = this.parseAdditive(parentAST);
        if (optionalRightSideAST.isEmpty())
            return Optional.empty();
        this.rightSideOperable = optionalRightSideAST.get();
        this.setEnd(this.rightSideOperable.getEnd());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + this.getLeftSideOperable().getClass().getSimpleName());
        this.getLeftSideOperable().printSyntaxAST(printStream, indents + "│        ");
        printStream.println(indents + "├── operator: " + this.getAssignmentOperatorType());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + this.getRightSideOperable().getClass().getSimpleName());
        this.getRightSideOperable().printSyntaxAST(printStream, indents + "        ");
    }
    
    
    public static AssignmentExpressionSyntaxASTBuilder builder(final SyntaxAnalyzer syntaxAnalyzer) {
        return new AssignmentExpressionSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static AssignmentExpressionSyntaxASTBuilder builder() {
        return new AssignmentExpressionSyntaxASTBuilder();
    }
    
    
    public static class AssignmentExpressionSyntaxASTBuilder
    {
        
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        private AbstractOperableSyntaxAST<?> leftSideOperable;
        
        
        private AssignmentOperatorType assignmentOperatorType;
        
        
        private AbstractOperableSyntaxAST<?> rightSideOperable;
        
        
        private int start, end;
        
        
        public AssignmentExpressionSyntaxASTBuilder(SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public AssignmentExpressionSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public AssignmentExpressionSyntaxASTBuilder left(final AbstractOperableSyntaxAST<?> leftSideOperable) {
            this.leftSideOperable = leftSideOperable;
            return this;
        }
        
        
        public AssignmentExpressionSyntaxASTBuilder operator(final AssignmentOperatorType assignmentOperatorType) {
            this.assignmentOperatorType = assignmentOperatorType;
            return this;
        }
        
        
        public AssignmentExpressionSyntaxASTBuilder right(final AbstractOperableSyntaxAST<?> rightSideOperable) {
            this.rightSideOperable = rightSideOperable;
            return this;
        }
        
        
        public AssignmentExpressionSyntaxASTBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public AssignmentExpressionSyntaxASTBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public AssignmentExpressionSyntaxAST build() {
            final AssignmentExpressionSyntaxAST assignmentExpressionSyntaxAST = new AssignmentExpressionSyntaxAST(this.syntaxAnalyzer);
            assignmentExpressionSyntaxAST.setAssignmentOperatorType(this.assignmentOperatorType);
            assignmentExpressionSyntaxAST.setRightSideOperable(this.rightSideOperable);
            assignmentExpressionSyntaxAST.setLeftSideOperable(this.leftSideOperable);
            assignmentExpressionSyntaxAST.setStart(this.start);
            assignmentExpressionSyntaxAST.setEnd(this.end);
            return assignmentExpressionSyntaxAST;
        }
        
    }
    
}
