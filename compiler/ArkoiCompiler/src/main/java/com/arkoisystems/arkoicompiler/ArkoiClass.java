/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.utils.Variables;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.nio.charset.StandardCharsets;

/**
 * {@link ArkoiClass} provides methods and variables for the compilation of the given
 * contents. It is getting used inside {@link ArkoiCompiler} to add new files to the
 * compiler. Also you can output this class in a JSON like view with the method {@link
 * ArkoiClass#toString()}.
 */
@Getter
@Setter
public class ArkoiClass
{
    
    /**
     * The {@link ArkoiCompiler} in which the {@link ArkoiClass} got created.
     */
    private final ArkoiCompiler arkoiCompiler;
    
    
    /**
     * The content which is used to compile everything. Usually it is just used inside the
     * {@link LexicalAnalyzer} because only there the content is needed to parse the
     * {@link com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken}'s.
     */
    private final String content;
    
    
    /**
     * Defines if the {@link ArkoiClass} is a native class or not. This is used if you
     * want to get default functions from the natives.
     */
    @Expose
    private final boolean nativeClass;
    
    
    /**
     * The {@link LexicalAnalyzer} is used to generate the {@link AbstractToken} list for
     * later use in the {@link SyntaxAnalyzer}.
     */
    @Expose
    private LexicalAnalyzer lexicalAnalyzer;
    
    
    /**
     * The {@link SyntaxAnalyzer} is used to generate the AST (Abstract Syntax Tree) for
     * later use in the {@link SemanticAnalyzer}.
     */
    @Expose
    private SyntaxAnalyzer syntaxAnalyzer;
    
    
    /**
     * The {@link SemanticAnalyzer} is used to generate an enhanced AST (Abstract Syntax
     * Tree) for later use.
     */
    @Expose
    private SemanticAnalyzer semanticAnalyzer;
    
    
    /**
     * This will construct a new {@link ArkoiClass} with the given parameters. You also
     * can specify if the {@link ArkoiClass} should be a native class or not. This is
     * getting used inside the {@link ArkoiCompiler} when adding new native files.
     *
     * @param arkoiCompiler
     *         the {@link ArkoiCompiler} which should get used to compile the given
     *         contents.
     * @param content
     *         the {@code byte[]} is used for the compilation of everything. It will be
     *         passed to the {@link LexicalAnalyzer} where it will produce a {@link
     *         java.util.List} with all tokens.
     * @param nativeClass
     *         the flag if the {@link ArkoiClass} should be a native class or not.
     */
    public ArkoiClass(@NonNull final ArkoiCompiler arkoiCompiler, @NonNull final byte[] content, final boolean nativeClass) {
        this.arkoiCompiler = arkoiCompiler;
        this.nativeClass = nativeClass;
    
        this.content = new String(content, StandardCharsets.UTF_8);
    }
    
    
    /**
     * This will construct a new {@link ArkoiClass} with the given parameters. It will set
     * the native flag to false, so if you want to create a native {@link ArkoiClass} you
     * should use the other constructor ({@link ArkoiClass#ArkoiClass(ArkoiCompiler,
     * byte[], boolean)}).
     *
     * @param arkoiCompiler
     *         the {@link ArkoiCompiler} which should get used to compile the given
     *         contents.
     * @param content
     *         the {@link byte[]} is used for the compilation of everything. It will be
     *         passed to the {@link LexicalAnalyzer} where it will produce a {@link
     *         java.util.List} with all tokens.
     */
    public ArkoiClass(@NonNull final ArkoiCompiler arkoiCompiler, @NonNull final byte[] content) {
        this.arkoiCompiler = arkoiCompiler;
    
        this.content = new String(content, StandardCharsets.UTF_8);
        this.nativeClass = false;
    }
    
    
    /**
     * Initializes the {@link LexicalAnalyzer} which will produce a {@link AbstractToken}
     * list for later use in the {@link SyntaxAnalyzer}.
     */
    public void initializeLexical() {
        this.lexicalAnalyzer = new LexicalAnalyzer(this);
    }
    
    
    /**
     * Initializes the {@link SyntaxAnalyzer} which will parse a AST (Abstract Syntax
     * Tree) for later use in the {@link SemanticAnalyzer}. Also it will check if the
     * {@link LexicalAnalyzer} got initialized or he will throw an exception because the
     * {@link SyntaxAnalyzer} needs the {@link com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken}
     * list.
     */
    public void initializeSyntax() {
        if (this.lexicalAnalyzer == null) {
            throw new NullPointerException("You need to initialize the LexicalAnalyzer before initializing the SyntaxAnalyzer, because it needs the output of the LexicalAnalyzer.");
        } else this.syntaxAnalyzer = new SyntaxAnalyzer(this);
    }
    
    
    /**
     * Initializes the {@link SemanticAnalyzer} which will produce a next AST (Abstract
     * Syntax Tree) with simplified construction. Also it will check if the {@link
     * SyntaxAnalyzer} got initialized or he will throw an exception because the {@link
     * SyntaxAnalyzer} needs the old AST.
     */
    public void initializeSemantic() {
        if (this.syntaxAnalyzer == null) {
            throw new NullPointerException("You need to initialize the SyntaxAnalyzer before initializing the SemanticAnalyzer, because it needs the output of the SyntaxAnalyzer.");
        } else this.semanticAnalyzer = new SemanticAnalyzer(this);
    }
    
    
    /**
     * Returns this class as a JSON based {@link String} with all exposed variables etc.
     * With this functionality you can better see problems or check each stage for its
     * correctness.
     *
     * @return this class as a JSON based {@link String}.
     */
    @Override
    public String toString() {
        return Variables.GSON.toJson(this);
    }
    
}
