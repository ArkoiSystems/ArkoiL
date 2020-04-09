/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler;

import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ArkoiClass implements ICompilerClass
{
    
    @Getter
    @NotNull
    private final ArkoiCompiler arkoiCompiler;
    
    
    @Getter
    @Setter
    @NotNull
    private char[] content;
    
    
    @Getter
    @Setter
    private boolean nativeClass;
    
    
    @Getter
    @Setter
    @NotNull
    private String filePath;
    
    
    @Getter
    @NotNull
    private LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(this);
    
    
    @Getter
    @NotNull
    private SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(this);
    
    
    @Getter
    @NotNull
    private SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(this);
    
    
    public ArkoiClass(@NotNull final ArkoiCompiler arkoiCompiler, @NotNull final String filePath, @NotNull final byte[] content) {
        this.arkoiCompiler = arkoiCompiler;
        this.filePath = filePath;
    
        this.content = new String(content, StandardCharsets.UTF_8).toCharArray();
        this.nativeClass = false;
    }
    
    
    @SneakyThrows
    @Override
    @Nullable
    public ICompilerClass getArkoiFile(@NotNull File file) {
        if (!file.isAbsolute())
            file = new File(new File(this.getFilePath()).getParent(), file.getPath());
        
        for (final ICompilerClass arkoiClass : this.getArkoiCompiler().getArkoiClasses())
            if (arkoiClass.getFilePath().equals(file.getAbsolutePath()))
                return arkoiClass;
        
        if (file.exists()) {
            final ArkoiClass arkoiClass = new ArkoiClass(this.getArkoiCompiler(), file.getAbsolutePath(), Files.readAllBytes(file.toPath()));
            arkoiClass.getLexicalAnalyzer().processStage();
            arkoiClass.getSyntaxAnalyzer().processStage();
            arkoiClass.getSemanticAnalyzer().processStage();
            
            this.getArkoiCompiler().addClass(arkoiClass);
            return arkoiClass;
        }
        return null;
    }
    
}
