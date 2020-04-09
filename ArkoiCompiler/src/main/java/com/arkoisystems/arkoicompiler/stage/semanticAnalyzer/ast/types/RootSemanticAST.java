/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.ArkoiSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.FunctionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.ImportSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.VariableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.RootSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RootSemanticAST extends ArkoiSemanticAST<RootSyntaxAST>
{
    
    @Getter
    @NotNull
    private final List<ImportSemanticAST> importStorage = new ArrayList<>();
    
    
    @Getter
    @NotNull
    private final List<VariableSemanticAST> variableStorage = new ArrayList<>();
    
    
    @Getter
    @NotNull
    private final List<FunctionSemanticAST> functionStorage = new ArrayList<>();
    
    
    public RootSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @NotNull final RootSyntaxAST rootSyntaxAST) {
        super(semanticAnalyzer, null, rootSyntaxAST, ASTType.ROOT);
        
        for (final FunctionSyntaxAST functionSyntaxAST : rootSyntaxAST.getFunctionStorage()) {
            final FunctionSemanticAST functionSemanticAST
                    = new FunctionSemanticAST(semanticAnalyzer, this, functionSyntaxAST);
            this.functionStorage.add(functionSemanticAST);
        }
    }
    
    
    @Override
    public void initialize() {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        final HashMap<String, ArkoiSemanticAST<?>> names = new HashMap<>();
        this.getSyntaxAST().getSortedStorage().forEach(arkoiSyntaxAST -> {
            if (arkoiSyntaxAST instanceof FunctionSyntaxAST)
                return;
            
            if (arkoiSyntaxAST instanceof ImportSyntaxAST) {
                final ImportSyntaxAST importSyntaxAST = (ImportSyntaxAST) arkoiSyntaxAST;
                final ImportSemanticAST importSemanticAST
                        = new ImportSemanticAST(this.getSemanticAnalyzer(), this, importSyntaxAST);
                
                final IdentifierToken importName = importSemanticAST.getImportName();
                if (names.containsKey(importName.getTokenContent())) {
                    final ArkoiSemanticAST<?> alreadyExistAST = names.get(importName.getTokenContent());
                    this.addError(
                            null,
                            this.getSemanticAnalyzer().getArkoiClass(),
                            new ArkoiSemanticAST[] {
                                    alreadyExistAST,
                                    importSemanticAST
                            },
                            SemanticErrorType.IMPORT_NAME_ALREADY_TAKEN,
                            (alreadyExistAST instanceof ImportSemanticAST) ? "an import" : "a variable"
                    );
                } else
                    names.put(importName.getTokenContent(), importSemanticAST);
                
                importSemanticAST.getImportTargetClass();
                
                if (importSemanticAST.isFailed())
                    this.failed();
                this.importStorage.add(importSemanticAST);
            } else if (arkoiSyntaxAST instanceof VariableSyntaxAST) {
                final VariableSyntaxAST variableSyntaxAST = (VariableSyntaxAST) arkoiSyntaxAST;
                final VariableSemanticAST variableSemanticAST
                        = new VariableSemanticAST(this.getSemanticAnalyzer(), this, variableSyntaxAST);
                
                variableSemanticAST.getVariableAnnotations();
                
                final IdentifierToken variableName = variableSemanticAST.getVariableName();
                if (names.containsKey(variableName.getTokenContent())) {
                    final ArkoiSemanticAST<?> alreadyExistAST = names.get(variableName.getTokenContent());
                    this.addError(
                            null,
                            this.getSemanticAnalyzer().getArkoiClass(),
                            new ArkoiSemanticAST[] {
                                    alreadyExistAST,
                                    variableSemanticAST
                            },
                            SemanticErrorType.VARIABLE_NAME_ALREADY_TAKEN,
                            (alreadyExistAST instanceof ImportSemanticAST) ? "an import" : "a variable"
                    );
                } else
                    names.put(variableName.getTokenContent(), variableSemanticAST);
                
                variableSemanticAST.getVariableExpression();
                
                if (variableSemanticAST.isFailed())
                    this.failed();
                this.variableStorage.add(variableSemanticAST);
            }
        });
        
        for (final FunctionSemanticAST functionSemanticAST : this.getFunctionStorage()) {
            functionSemanticAST.getFunctionAnnotations();
            
            final String functionDescription = functionSemanticAST.getFunctionDescription();
            if (names.containsKey(functionDescription)) {
                final ArkoiSemanticAST<?> alreadyExistAST = names.get(functionDescription);
                this.addError(
                        null,
                        this.getSemanticAnalyzer().getArkoiClass(),
                        new ArkoiSemanticAST[] {
                                alreadyExistAST,
                                functionSemanticAST
                        },
                        SemanticErrorType.FUNCTION_DESC_ALREADY_EXISTS
                );
            } else names.put(functionDescription, functionSemanticAST);
            
            functionSemanticAST.getFunctionBlock();
            
            if (functionSemanticAST.isFailed())
                this.failed();
        }
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── imports: " + (this.getImportStorage().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getImportStorage().size(); index++) {
            final ArkoiSemanticAST<?> arkoiSemanticAST = this.getImportStorage().get(index);
            if (index == this.getImportStorage().size() - 1) {
                printStream.println(indents + "│   └── " + arkoiSemanticAST.getClass().getSimpleName());
                arkoiSemanticAST.printSemanticAST(printStream, indents + "│       ");
            } else {
                printStream.println(indents + "│   ├── " + arkoiSemanticAST.getClass().getSimpleName());
                arkoiSemanticAST.printSemanticAST(printStream, indents + "│   │   ");
                printStream.println(indents + "│   │   ");
            }
        }
        printStream.println(indents + "│");
        printStream.println(indents + "├── variables: " + (this.getVariableStorage().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getVariableStorage().size(); index++) {
            final ArkoiSemanticAST<?> arkoiSemanticAST = this.getVariableStorage().get(index);
            if (index == this.getVariableStorage().size() - 1) {
                printStream.println(indents + "│   └── " + arkoiSemanticAST.getClass().getSimpleName());
                arkoiSemanticAST.printSemanticAST(printStream, indents + "│       ");
            } else {
                printStream.println(indents + "│   ├── " + arkoiSemanticAST.getClass().getSimpleName());
                arkoiSemanticAST.printSemanticAST(printStream, indents + "│   │   ");
                printStream.println(indents + "│   │   ");
            }
        }
        printStream.println(indents + "│");
        printStream.println(indents + "└── functions: " + (this.getFunctionStorage().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getFunctionStorage().size(); index++) {
            final ArkoiSemanticAST<?> arkoiSemanticAST = this.getFunctionStorage().get(index);
            if (index == this.getFunctionStorage().size() - 1) {
                printStream.println(indents + "    └── " + arkoiSemanticAST.getClass().getSimpleName());
                arkoiSemanticAST.printSemanticAST(printStream, indents + "        ");
            } else {
                printStream.println(indents + "    ├── " + arkoiSemanticAST.getClass().getSimpleName());
                arkoiSemanticAST.printSemanticAST(printStream, indents + "    │   ");
                printStream.println(indents + "    │   ");
            }
        }
    }
    
    
    @Nullable
    public ArkoiSemanticAST<?> findIdentifier(@NotNull final IdentifierToken identifierToken) {
        for (final ImportSemanticAST importSemanticAST : this.getImportStorage()) {
            Objects.requireNonNull(importSemanticAST.getImportName());
            
            if (importSemanticAST.getImportName().getTokenContent().equals(identifierToken.getTokenContent()))
                return importSemanticAST;
        }
        
        for (final VariableSemanticAST variableSemanticAST : this.getVariableStorage()) {
            Objects.requireNonNull(variableSemanticAST.getVariableName());
            
            if (variableSemanticAST.getVariableName().getTokenContent().equals(identifierToken.getTokenContent()))
                return variableSemanticAST;
        }
        return null;
    }
    
}