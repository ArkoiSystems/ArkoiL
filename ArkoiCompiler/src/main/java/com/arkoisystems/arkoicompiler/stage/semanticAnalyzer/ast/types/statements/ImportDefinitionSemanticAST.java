/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.PrintStream;
import java.util.Objects;

public class ImportDefinitionSemanticAST extends AbstractSemanticAST<ImportDefinitionSyntaxAST>
{
    
    @Nullable
    private ArkoiClass importTargetClass;
    
    
    public ImportDefinitionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final AbstractSemanticAST<?> lastContainerAST, @NonNull final ImportDefinitionSyntaxAST importDefinitionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, importDefinitionSyntaxAST, ASTType.IMPORT_DEFINITION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── name: " + this.getImportName().getTokenContent());
        printStream.println(indents + "└── path: " + (this.getImportTargetClass() != null ? this.getImportTargetClass().getFilePath() : null));
    }
    
    
    @SneakyThrows
    @Nullable
    public ArkoiClass getImportTargetClass() {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        if (this.importTargetClass == null) {
            final String filePath = new File(this.getSemanticAnalyzer().getArkoiClass().getArkoiCompiler().getWorkingDirectory() + File.separator +
                    this.getSyntaxAST().getImportFilePath().getTokenContent() + ".ark").getCanonicalPath();
            
            this.importTargetClass = this.getSemanticAnalyzer().getArkoiClass().getArkoiCompiler().getArkoiClasses().get(filePath);
            if (this.importTargetClass == null) {
                this.addError(
                        this.getSemanticAnalyzer().getArkoiClass(),
                        this.getSyntaxAST(),
                        SemanticErrorType.IMPORT_INVALID_PATH
                );
                return null;
            }
        }
        return this.importTargetClass;
    }
    
    
    @NotNull
    public IdentifierToken getImportName() {
        return this.getSyntaxAST().getImportName();
    }
    
}
