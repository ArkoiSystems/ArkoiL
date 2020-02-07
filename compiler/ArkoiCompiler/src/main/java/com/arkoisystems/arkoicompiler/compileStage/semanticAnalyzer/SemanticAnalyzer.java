package com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.compileStage.ICompileStage;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.ErrorHandler;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.AbstractSemantic;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.RootSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.RootAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.lang.reflect.Constructor;

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
@Getter
public class SemanticAnalyzer implements ICompileStage
{
    
    private final ArkoiClass arkoiClass;
    
    @Expose
    private final SemanticErrorHandler errorHandler;
    
    @Expose
    private final RootSemantic rootSemantic;
    
    public SemanticAnalyzer(final ArkoiClass arkoiClass) throws Exception {
        this.arkoiClass = arkoiClass;
        
        this.errorHandler = new SemanticErrorHandler();
        this.rootSemantic = RootSemantic.class.getDeclaredConstructor(AbstractSemantic.class, RootAST.class).newInstance(null, arkoiClass.getSyntaxAnalyzer().getRootAST());
    }
    
    @Override
    public boolean processStage() {
        return this.rootSemantic.analyse(this);
    }
    
    @Override
    public ErrorHandler errorHandler() {
        return this.errorHandler;
    }
    
    public boolean analyseSemanticClass(final AbstractSemantic<?> lastContainerSemantic, final AbstractAST<?> abstractAST) throws Exception {
        return this.analyseSemanticClass(lastContainerSemantic, abstractAST, this);
    }
    
    public boolean analyseSemanticClass(final AbstractSemantic<?> lastContainerSemantic, final AbstractAST<?> abstractAST, final SemanticAnalyzer semanticAnalyzer) throws Exception {
        String genericName = abstractAST.getClass().getGenericSuperclass().getTypeName();
        genericName = genericName.substring(genericName.indexOf("<") + 1, genericName.length() - 1);
        final String[] splittedName = genericName.split(", ");
        genericName = splittedName[splittedName.length - 1];
        
        if (genericName.endsWith("AbstractSemantic<?>"))
            return true;
        
        final Class<?> clazz = Class.forName(genericName);
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterCount() != 2)
                continue;
            if (constructor.getParameterTypes()[0].equals(AbstractSemantic.class) && constructor.getParameterTypes()[1].equals(abstractAST.getClass())) {
                final Object object = constructor.newInstance(lastContainerSemantic, abstractAST);
                if (object instanceof AbstractSemantic)
                    return ((AbstractSemantic<?>) object).analyse(semanticAnalyzer);
            }
        }
        return false;
    }
    
}
