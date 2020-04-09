/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast;

import com.arkoisystems.arkoicompiler.ArkoiError;
import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.api.ICompilerSemanticAST;
import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class ArkoiSemanticAST<T extends ICompilerSyntaxAST> implements ICompilerSemanticAST<T>
{
    
    @Getter
    @Nullable
    private final ICompilerSemanticAST<?> lastContainerAST;
    
    
    @Getter
    @Nullable
    private final SemanticAnalyzer semanticAnalyzer;
    
    
    @Getter
    @NotNull
    private final T syntaxAST;
    
    
    @Getter
    @NotNull
    private final ASTType astType;
    
    
    @Getter
    private boolean failed;
    
    
    @Getter
    @Nullable
    private AbstractToken startToken, endToken;
    
    
    public ArkoiSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NotNull final T syntaxAST, @NotNull final ASTType astType) {
        this.lastContainerAST = lastContainerAST;
        this.semanticAnalyzer = semanticAnalyzer;
        this.syntaxAST = syntaxAST;
        this.astType = astType;
        
        this.startToken = syntaxAST.getStartToken();
        this.endToken = syntaxAST.getEndToken();
    }
    
    
    @Override
    public abstract void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents);
    
    
    @Override
    public void initialize() { }
    
    
    @Nullable
    @Override
    public <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final AbstractToken abstractToken, @NotNull final String message, @NotNull final Object... arguments) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        this.getSemanticAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(new int[][] { { abstractToken.getStart(), abstractToken.getEnd() } })
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.failed();
        return errorSource;
    }
    
    
    @Nullable
    @Override
    public <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final ICompilerSyntaxAST[] compilerSyntaxASTs, @NotNull final String message, @NotNull final Object... arguments) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        this.getSemanticAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(Arrays.stream(compilerSyntaxASTs)
                        .map(compilerSyntaxAST -> new int[] {
                                Objects.requireNonNull(compilerSyntaxAST.getStartToken(), this.getFailedSupplier("compilerSyntaxAST.startToken must not be null")).getStart(),
                                Objects.requireNonNull(compilerSyntaxAST.getEndToken(), this.getFailedSupplier("compilerSyntaxAST.endToken must not be null")).getEnd()
                        })
                        .toArray(size -> new int[size][1])
                )
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.failed();
        return errorSource;
    }
    
    
    @Nullable
    @Override
    public <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final ICompilerSyntaxAST compilerSyntaxAST, @NotNull final String message, @NotNull final Object... arguments) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        this.getSemanticAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(new int[][] { {
                        Objects.requireNonNull(compilerSyntaxAST.getStartToken(), this.getFailedSupplier("compilerSyntaxAST.startToken must not be null")).getStart(),
                        Objects.requireNonNull(compilerSyntaxAST.getEndToken(), this.getFailedSupplier("compilerSyntaxAST.endToken must not be null")).getEnd()
                } })
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.failed();
        return errorSource;
    }
    
    
    @Nullable
    @Override
    public <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final ICompilerSemanticAST<?>[] compilerSemanticASTs, @NotNull final String message, @NotNull final Object... arguments) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        this.getSemanticAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(Arrays.stream(compilerSemanticASTs)
                        .map(compilerSemanticAST -> new int[] {
                                Objects.requireNonNull(compilerSemanticAST.getStartToken(), this.getFailedSupplier("compilerSemanticAST.startToken must not be null")).getStart(),
                                Objects.requireNonNull(compilerSemanticAST.getEndToken(), this.getFailedSupplier("compilerSemanticAST.endToken must not be null")).getEnd()
                        })
                        .toArray(size -> new int[size][1])
                )
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.failed();
        return errorSource;
    }
    
    
    @Nullable
    @Override
    public <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final ICompilerSemanticAST<?> compilerSemanticAST, @NotNull final String message, @NotNull final Object... arguments) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        this.getSemanticAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(new int[][] { {
                        Objects.requireNonNull(compilerSemanticAST.getStartToken(), this.getFailedSupplier("compilerSemanticAST.startToken must not be null")).getStart(),
                        Objects.requireNonNull(compilerSemanticAST.getEndToken(), this.getFailedSupplier("compilerSemanticAST.endToken must not be null")).getEnd()
                } })
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.failed();
        return errorSource;
    }
    
    
    @NotNull
    @Override
    public Supplier<String> getFailedSupplier(@NotNull final String message) {
        return () -> {
            this.failed();
            return message;
        };
    }
    
    
    @Override
    public void failed() {
        this.failed = true;
    }
    
    
    @Override
    public String toString() {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(byteArrayOutputStream);
        this.printSemanticAST(printStream, "");
        return byteArrayOutputStream.toString();
    }
    
    
    @Override
    public int hashCode() {
        return Objects.hash(
                this.getLastContainerAST(),
                this.getSyntaxAST(),
                this.getAstType(),
                
                Objects.requireNonNull(this.getStartToken(), this.getFailedSupplier("startToken must not be null.")).getStart(),
                Objects.requireNonNull(this.getEndToken(), this.getFailedSupplier("endToken must not be null.")).getEnd()
        );
    }
    
}
