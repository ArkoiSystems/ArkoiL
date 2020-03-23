/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on March 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.lang.arkoi.highlight

import com.arkoisystems.lang.arkoi.ArkoiBundle
import com.arkoisystems.lang.arkoi.icons.ArkoiIcons
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage

class ArkoiColorSettingsPage : ColorSettingsPage {

    private val descriptors = mapOf(
        ArkoiBundle.message("arkoi.colorSettings.operatorSign.displayName") to ArkoiSyntaxHighlighter.operatorSign,
        ArkoiBundle.message("arkoi.colorSettings.parentheses.displayName") to ArkoiSyntaxHighlighter.parentheses,
        ArkoiBundle.message("arkoi.colorSettings.brackets.displayName") to ArkoiSyntaxHighlighter.brackets,
        ArkoiBundle.message("arkoi.colorSettings.braces.displayName") to ArkoiSyntaxHighlighter.braces,
        ArkoiBundle.message("arkoi.colorSettings.dot.displayName") to ArkoiSyntaxHighlighter.dot,
        ArkoiBundle.message("arkoi.colorSettings.semicolon.displayName") to ArkoiSyntaxHighlighter.semicolon,

        ArkoiBundle.message("arkoi.colorSettings.lineComment.displayName") to ArkoiSyntaxHighlighter.lineComment,

        ArkoiBundle.message("arkoi.colorSettings.functionCall") to ArkoiSyntaxHighlighter.functionCall,
        ArkoiBundle.message("arkoi.colorSettings.functionParameter") to ArkoiSyntaxHighlighter.functionParameter,
        ArkoiBundle.message("arkoi.colorSettings.functionDeclaration") to ArkoiSyntaxHighlighter.functionDeclaration,

        ArkoiBundle.message("arkoi.colorSettings.globalVariable") to ArkoiSyntaxHighlighter.globalVariable,
        ArkoiBundle.message("arkoi.colorSettings.localVariable") to ArkoiSyntaxHighlighter.localVariable,

        ArkoiBundle.message("arkoi.colorSettings.string.displayName") to ArkoiSyntaxHighlighter.string,
        ArkoiBundle.message("arkoi.colorSettings.validStringEscape.displayName") to ArkoiSyntaxHighlighter.validStringEscape,

        ArkoiBundle.message("arkoi.colorSettings.badCharacter.displayName") to ArkoiSyntaxHighlighter.badCharacter,
        ArkoiBundle.message("arkoi.colorSettings.keyword.displayName") to ArkoiSyntaxHighlighter.keyword,
        ArkoiBundle.message("arkoi.colorSettings.identifier.displayName") to ArkoiSyntaxHighlighter.identifier,
        ArkoiBundle.message("arkoi.colorSettings.number.displayName") to ArkoiSyntaxHighlighter.number,
        ArkoiBundle.message("arkoi.colorSettings.primitive.displayName") to ArkoiSyntaxHighlighter.primitives
    ).map { AttributesDescriptor(it.key, it.value) }.toTypedArray()

    private val additionalTags = mutableMapOf(
        "badCharacter" to ArkoiSyntaxHighlighter.badCharacter,
        "functionCall" to ArkoiSyntaxHighlighter.functionCall,
        "functionParameter" to ArkoiSyntaxHighlighter.functionParameter,
        "functionDeclaration" to ArkoiSyntaxHighlighter.functionDeclaration,
        "localVariable" to ArkoiSyntaxHighlighter.localVariable,
        "globalVariable" to ArkoiSyntaxHighlighter.globalVariable,
        "validStringEscape" to ArkoiSyntaxHighlighter.validStringEscape
    )

    override fun getIcon() = ArkoiIcons.ARKOI_FILE

    override fun getHighlighter() = ArkoiSyntaxHighlighter()

    override fun getAdditionalHighlightingTagToDescriptorMap() = additionalTags

    override fun getDemoText() = """
        import "./test" as test;
        <badCharacter>/</badCharacter><badCharacter>/</badCharacter><badCharacter>/</badCharacter>
        
        var <globalVariable>test_1</globalVariable> = 1;
        
        var <globalVariable>test_2</globalVariable> = this.<functionCall>greeting</functionCall>();

        var <globalVariable>test_3</globalVariable> = (test_1 - (test_1 += 4 + -10) * 5)i * 2f;

        # this method gets invoked first
        fun <functionDeclaration>main</functionDeclaration><int>(<functionParameter>args</functionParameter>: string[]) {
            var <localVariable>test_1</localVariable> = "Hello <validStringEscape>\n</validStringEscape>World<validStringEscape>\"</validStringEscape>";
            var <localVariable>test_3</localVariable> = this.test_1;
            var <localVariable>test_6</localVariable> = 1;
            
            this.<functionCall>print_greet</functionCall>(test_1);
            return (20++ + -10 * 5f) * 2 ** 3 ** 4 + (test_6 += 1);
        }

        fun <functionDeclaration>print_greet</functionDeclaration><>(<functionParameter>test_argument</functionParameter>: string) = <functionCall>println</functionCall>("%s , %s" % [test_2, test_argument]);

        fun <functionDeclaration>greeting</functionDeclaration><string>() = "Hello World";
    """.trimIndent()

    override fun getAttributeDescriptors() = descriptors

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName() = ArkoiBundle.message("arkoi.fileType.name")

}