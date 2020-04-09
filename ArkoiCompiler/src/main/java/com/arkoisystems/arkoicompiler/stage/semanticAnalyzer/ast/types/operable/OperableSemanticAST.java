/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable;

import com.arkoisystems.arkoicompiler.api.ICompilerSemanticAST;
import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.ArkoiSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.CollectionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.IdentifierCallSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.NumberSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.StringSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.CollectionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.StringSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;

public class OperableSemanticAST<T extends OperableSyntaxAST> extends ArkoiSemanticAST<T>
{
    
    public OperableSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NotNull final T syntaxAST, @NotNull final ASTType astType) {
        super(semanticAnalyzer, lastContainerAST, syntaxAST, astType);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) { }
    
    
    @NotNull
    public TypeKind getTypeKind() {
        return Objects.requireNonNull(this.getSpecifiedOperable(this.getSyntaxAST(), null), this.getFailedSupplier("The operable must not be null.")).getTypeKind();
    }
    
    
    @Nullable
    public OperableSemanticAST<?> getSpecifiedOperable(@NotNull final ICompilerSyntaxAST syntaxAST, @Nullable final List<Class<?>> operablesToCheck) {
        if (syntaxAST instanceof StringSyntaxAST
                && (operablesToCheck == null || operablesToCheck.contains(StringSemanticAST.class))) {
            final StringSyntaxAST stringSyntaxAST = (StringSyntaxAST) syntaxAST;
            return new StringSemanticAST(
                    this.getSemanticAnalyzer(),
                    this.getLastContainerAST(),
                    stringSyntaxAST
            );
        } else if (syntaxAST instanceof NumberSyntaxAST
                && (operablesToCheck == null || operablesToCheck.contains(NumberSemanticAST.class))) {
            final NumberSyntaxAST numberSyntaxAST = (NumberSyntaxAST) syntaxAST;
            return new NumberSemanticAST(
                    this.getSemanticAnalyzer(),
                    this.getLastContainerAST(),
                    numberSyntaxAST
            );
        } else if (syntaxAST instanceof IdentifierCallSyntaxAST
                && (operablesToCheck == null || operablesToCheck.contains(IdentifierCallSemanticAST.class))) {
            final IdentifierCallSyntaxAST identifierCallSyntaxAST = (IdentifierCallSyntaxAST) syntaxAST;
            return new IdentifierCallSemanticAST(
                    this.getSemanticAnalyzer(),
                    this.getLastContainerAST(),
                    identifierCallSyntaxAST
            );
        } else if (syntaxAST instanceof CollectionSyntaxAST
                && (operablesToCheck == null || operablesToCheck.contains(CollectionSemanticAST.class))) {
            final CollectionSyntaxAST collectionSyntaxAST = (CollectionSyntaxAST) syntaxAST;
            return new CollectionSemanticAST(
                    this.getSemanticAnalyzer(),
                    this.getLastContainerAST(),
                    collectionSyntaxAST
            );
        } else if (syntaxAST instanceof BinaryExpressionSyntaxAST
                && (operablesToCheck == null || operablesToCheck.contains(BinaryExpressionSemanticAST.class))) {
            final BinaryExpressionSyntaxAST binaryExpressionSyntaxAST = (BinaryExpressionSyntaxAST) syntaxAST;
            return new BinaryExpressionSemanticAST(
                    this.getSemanticAnalyzer(),
                    this.getLastContainerAST(),
                    binaryExpressionSyntaxAST
            );
        } else if (syntaxAST instanceof AssignmentExpressionSyntaxAST
                && (operablesToCheck == null || operablesToCheck.contains(AssignmentExpressionSemanticAST.class))) {
            final AssignmentExpressionSyntaxAST assignmentExpressionSyntaxAST = (AssignmentExpressionSyntaxAST) syntaxAST;
            return new AssignmentExpressionSemanticAST(
                    this.getSemanticAnalyzer(),
                    this.getLastContainerAST(),
                    assignmentExpressionSyntaxAST
            );
        } else if (syntaxAST instanceof CastExpressionSyntaxAST
                && (operablesToCheck == null || operablesToCheck.contains(CastExpressionSemanticAST.class))) {
            final CastExpressionSyntaxAST castExpressionSyntaxAST = (CastExpressionSyntaxAST) syntaxAST;
            return new CastExpressionSemanticAST(
                    this.getSemanticAnalyzer(),
                    this.getLastContainerAST(),
                    castExpressionSyntaxAST
            );
        } else if (syntaxAST instanceof EqualityExpressionSyntaxAST
                && (operablesToCheck == null || operablesToCheck.contains(EqualityExpressionSemanticAST.class))) {
            final EqualityExpressionSyntaxAST equalityExpressionSyntaxAST = (EqualityExpressionSyntaxAST) syntaxAST;
            return new EqualityExpressionSemanticAST(
                    this.getSemanticAnalyzer(),
                    this.getLastContainerAST(),
                    equalityExpressionSyntaxAST
            );
        } else if (syntaxAST instanceof LogicalExpressionSyntaxAST
                && (operablesToCheck == null || operablesToCheck.contains(LogicalExpressionSemanticAST.class))) {
            final LogicalExpressionSyntaxAST logicalExpressionSyntaxAST = (LogicalExpressionSyntaxAST) syntaxAST;
            return new LogicalExpressionSemanticAST(
                    this.getSemanticAnalyzer(),
                    this.getLastContainerAST(),
                    logicalExpressionSyntaxAST
            );
        } else if (syntaxAST instanceof ParenthesizedExpressionSyntaxAST
                && (operablesToCheck == null || operablesToCheck.contains(ParenthesizedExpressionSemanticAST.class))) {
            final ParenthesizedExpressionSyntaxAST parenthesizedExpressionSyntaxAST = (ParenthesizedExpressionSyntaxAST) syntaxAST;
            return new ParenthesizedExpressionSemanticAST(
                    this.getSemanticAnalyzer(),
                    this.getLastContainerAST(),
                    parenthesizedExpressionSyntaxAST
            );
        } else if (syntaxAST instanceof PostfixExpressionSyntaxAST
                && (operablesToCheck == null || operablesToCheck.contains(PostfixExpressionSemanticAST.class))) {
            final PostfixExpressionSyntaxAST postfixExpressionSyntaxAST = (PostfixExpressionSyntaxAST) syntaxAST;
            return new PostfixExpressionSemanticAST(
                    this.getSemanticAnalyzer(),
                    this.getLastContainerAST(),
                    postfixExpressionSyntaxAST
            );
        } else if (syntaxAST instanceof PrefixExpressionSyntaxAST
                && (operablesToCheck == null || operablesToCheck.contains(PrefixExpressionSemanticAST.class))) {
            final PrefixExpressionSyntaxAST prefixExpressionSyntaxAST = (PrefixExpressionSyntaxAST) syntaxAST;
            return new PrefixExpressionSemanticAST(
                    this.getSemanticAnalyzer(),
                    this.getLastContainerAST(),
                    prefixExpressionSyntaxAST
            );
        } else if (syntaxAST instanceof RelationalExpressionSyntaxAST
                && (operablesToCheck == null || operablesToCheck.contains(RelationalExpressionSemanticAST.class))) {
            final RelationalExpressionSyntaxAST relationalExpressionSyntaxAST = (RelationalExpressionSyntaxAST) syntaxAST;
            return new RelationalExpressionSemanticAST(
                    this.getSemanticAnalyzer(),
                    this.getLastContainerAST(),
                    relationalExpressionSyntaxAST
            );
        }
        return null;
    }
    
}
