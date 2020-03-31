/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler;

import com.arkoisystems.arkoicompiler.api.IClassHandler;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * {@link ArkoiClass} provides methods and variables for the compilation of the given
 * contents. It is getting used inside {@link ArkoiCompiler} to add new files to the
 * compiler. Also you can output this class in a JSON like view with the method {@link
 * ArkoiClass#toString()}.
 */
public class ArkoiClass implements IClassHandler
{
    
    /**
     * The {@link ArkoiCompiler} in which the {@link ArkoiClass} got created.
     */
    @Getter
    @NotNull
    private final ArkoiCompiler arkoiCompiler;
    
    
    @Getter
    @Setter
    @Nullable
    private IClassHandler customHandler;
    
    
    /**
     * The content which is used to compile everything. Usually it is just used inside the
     * {@link LexicalAnalyzer} because only there the content is needed to parse the
     * {@link AbstractToken}s.
     */
    @Getter
    @NotNull
    private final char[] content;
    
    
    /**
     * Defines if the {@link ArkoiClass} is a native class or not. This is used if you
     * want to get default functions from the natives.
     */
    @Getter
    private final boolean nativeClass;
    
    
    /**
     * Defines the path to the file which contains the data {@link #content}.
     */
    @Getter
    @Setter
    @NotNull
    private String filePath;
    
    
    /**
     * The {@link LexicalAnalyzer} is used to generate the {@link AbstractToken} list for
     * later use in the {@link SyntaxAnalyzer}.
     */
    @Getter
    @NotNull
    private LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(this);
    
    
    /**
     * The {@link SyntaxAnalyzer} is used to generate the AST (Abstract Syntax Tree) for
     * later use in the {@link SemanticAnalyzer}.
     */
    @Getter
    @NotNull
    private SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(this);
    
    
    /**
     * The {@link SemanticAnalyzer} is used to generate an enhanced AST (Abstract Syntax
     * Tree) for later use.
     */
    @Getter
    @NotNull
    private SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(this);
    
    
    /**
     * This will construct a new {@link ArkoiClass} with the given parameters. You also
     * can specify if the {@link ArkoiClass} should be a native class or not. This is
     * getting used inside the {@link ArkoiCompiler} when adding new native files.
     *
     * @param arkoiCompiler
     *         the {@link ArkoiCompiler} which is used used to compile the given
     *         contents.
     * @param filePath
     *         the path to the file which contains this declared content.
     * @param content
     *         the {@code byte[]} is used for the compilation of everything. It will be
     *         passed to the {@link LexicalAnalyzer} where it will produce a {@link
     *         java.util.List} with all tokens.
     * @param nativeClass
     *         the flag if the {@link ArkoiClass} should be a native class or not.
     */
    public ArkoiClass(@NotNull final ArkoiCompiler arkoiCompiler, @NotNull final String filePath, @NotNull final byte[] content, final boolean nativeClass) {
        this.arkoiCompiler = arkoiCompiler;
        this.nativeClass = nativeClass;
        this.filePath = filePath;
        
        this.content = new String(content, StandardCharsets.UTF_8).toCharArray();
    }
    
    
    /**
     * This will construct a new {@link ArkoiClass} with the given parameters. It will set
     * the native flag to false, so if you want to create a native {@link ArkoiClass} you
     * should use the other constructor ({@link ArkoiClass#ArkoiClass(ArkoiCompiler,
     * String, byte[], boolean)}).
     *
     * @param arkoiCompiler
     *         the {@link ArkoiCompiler} which is used used to compile the given
     *         contents.
     * @param filePath
     *         the path to the file which contains this declared content.
     * @param content
     *         the {@link byte[]} is used for the compilation of everything. It will be
     *         passed to the {@link LexicalAnalyzer} where it will produce a {@link
     *         java.util.List} with all tokens.
     */
    public ArkoiClass(@NotNull final ArkoiCompiler arkoiCompiler, @NotNull final String filePath, @NotNull final byte[] content) {
        this.arkoiCompiler = arkoiCompiler;
        this.filePath = filePath;
    
        this.content = new String(content, StandardCharsets.UTF_8).toCharArray();
        this.nativeClass = false;
    }
    
    @SneakyThrows
    @Override
    public @Nullable ArkoiClass getArkoiFile(@NotNull String filePath) {
        if (this.getCustomHandler() != null)
            return this.getCustomHandler().getArkoiFile(filePath);
        
        filePath = new File(new File(this.getFilePath()).getParent(), filePath).getCanonicalPath();
        for (final ArkoiClass arkoiClass : this.getArkoiCompiler().getArkoiClasses())
            if (arkoiClass.getFilePath().equals(filePath))
                return arkoiClass;
        return null;
    }
    
    
    /**
     * Returns a unique hash for this {@link ArkoiClass}. This hash is used when comparing
     * for errors etc.
     *
     * @return a unique hash for this {@link ArkoiClass}.
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(this.isNativeClass(), this.getFilePath());
        result = 31 * result + Arrays.hashCode(getContent());
        return result;
    }
    
}
