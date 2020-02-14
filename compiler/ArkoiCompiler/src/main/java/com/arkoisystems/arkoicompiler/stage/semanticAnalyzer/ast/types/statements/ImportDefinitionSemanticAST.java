package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportDefinitionSyntaxAST;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka timo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
@Setter
public class ImportDefinitionSemanticAST extends AbstractSemanticAST<ImportDefinitionSyntaxAST>
{
    
    private ArkoiClass importTargetClass;
    
    public ImportDefinitionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final ImportDefinitionSyntaxAST importDefinitionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, importDefinitionSyntaxAST, ASTType.IMPORT_DEFINITION);
    }
    
    @SneakyThrows
    public ArkoiClass getImportTargetClass() {
        if (this.importTargetClass == null) {
            final String filePath = new File(this.getSemanticAnalyzer().getArkoiClass().getArkoiCompiler().getWorkingDirectory() + File.separator +
                    this.getSyntaxAST().getImportFilePath().getTokenContent() + ".ark").getCanonicalPath();
        
            final ArkoiClass arkoiClass = this.getSemanticAnalyzer().getArkoiClass().getArkoiCompiler().getArkoiClasses().get(filePath);
            if (arkoiClass == null) {
                this.getSemanticAnalyzer().errorHandler().addError(new SyntaxASTError<>(this.getSyntaxAST(), "Couldn't analyze this import because the compiler doesn't know any class with this path."));
                return null;
            }
            return (this.importTargetClass = arkoiClass);
        }
        return this.importTargetClass;
    }
    
    public IdentifierToken getImportName() {
        return this.getSyntaxAST().getImportName();
    }
    
}
