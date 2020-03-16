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

        ArkoiBundle.message("arkoi.colorSettings.badCharacter.displayName") to ArkoiSyntaxHighlighter.badCharacter,
        ArkoiBundle.message("arkoi.colorSettings.keyword.displayName") to ArkoiSyntaxHighlighter.keyword,
        ArkoiBundle.message("arkoi.colorSettings.identifier.displayName") to ArkoiSyntaxHighlighter.identifier,
        ArkoiBundle.message("arkoi.colorSettings.number.displayName") to ArkoiSyntaxHighlighter.number,
        ArkoiBundle.message("arkoi.colorSettings.primitive.displayName") to ArkoiSyntaxHighlighter.primitives,
        ArkoiBundle.message("arkoi.colorSettings.string.displayName") to ArkoiSyntaxHighlighter.string
    ).map { AttributesDescriptor(it.key, it.value) }.toTypedArray()

    private val additionalTags = mutableMapOf(
        "badCharacter" to ArkoiSyntaxHighlighter.badCharacter
    )

    override fun getIcon() = ArkoiIcons.ARKOI_FILE

    override fun getHighlighter() = ArkoiSyntaxHighlighter()

    override fun getAdditionalHighlightingTagToDescriptorMap() = additionalTags

    override fun getDemoText() = """
        import "./test" as test;
        <badCharacter>*</badCharacter><badCharacter>-</badCharacter> <badCharacter>ß</badCharacter> <badCharacter>22</badCharacter>
        var test_1 = 1;
        var test_2 = this.greeting();

        var test_3 = (test_1 - (test_1 += 4 + -10) * 5)i * 2f;

        # this method gets invoked first
        fun main<int>(args: string[]) {
            var test_1 = "Hello";
            var test_3 = this.test_1;
            var test_6 = 1;
            
            this.print_greet(test_1);
            return (20++ + -10 * 5f) * 2 ** 3 ** 4 + (test_6 += 1);
        }

        fun print_greet<>(test_argument: string) = println("%s , %s" % [test_2, test_argument]);

        fun greeting<string>() = "Hello World";
    """.trimIndent()

    override fun getAttributeDescriptors() = descriptors

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName() = ArkoiBundle.message("arkoi.fileType.name")

}