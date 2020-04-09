/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on April 07, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements;

import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.api.ICompilerSemanticAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.ArkoiSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.PrintStream;
import java.util.Objects;

public class ImportSemanticAST extends ArkoiSemanticAST<ImportSyntaxAST>
{
    
    @Getter
    @NotNull
    private final IdentifierToken importName = this.checkImportName();
    
    
    @Getter
    @Nullable
    private final ICompilerClass importTargetClass = this.checkImportTargetClass();
    
    
    public ImportSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NotNull final ImportSyntaxAST syntaxAST) {
        super(semanticAnalyzer, lastContainerAST, syntaxAST, ASTType.IMPORT);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.printf("%s├── name: %s%n", indents, this.getImportName().getTokenContent());
        printStream.printf("%s└── path: %s%n", indents, this.getImportTargetClass() != null ? this.getImportTargetClass().getFilePath() : null);
    }
    
    
    @NotNull
    private IdentifierToken checkImportName() {
        Objects.requireNonNull(this.getSyntaxAST().getImportName(), this.getFailedSupplier("syntaxAST.importName must not be null."));
        return this.getSyntaxAST().getImportName();
    }
    
    
    @Nullable
    private ICompilerClass checkImportTargetClass() {
        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null."));
        Objects.requireNonNull(this.getSemanticAnalyzer().getArkoiClass(), this.getFailedSupplier("semanticAnalyzer.arkoiClass must not be null."));
        Objects.requireNonNull(this.getSyntaxAST().getImportFilePath(), this.getFailedSupplier("syntaxAST.importFilePath must not be null."));
        
        final File targetFile = new File(this.getSyntaxAST().getImportFilePath().getTokenContent() + ".ark");
        final ICompilerClass compilerClass = this.getSemanticAnalyzer().getArkoiClass().getArkoiFile(targetFile);
        if (compilerClass == null) {
            this.addError(
                    null,
                    this.getSemanticAnalyzer().getArkoiClass(),
                    this.getSyntaxAST().getImportFilePath(),
                    "13"
            );
        }
        return compilerClass;
    }
    
}
