/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on March 10, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.lang.arkoi

import com.arkoisystems.lang.arkoi.icons.ArkoiIcons
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.VirtualFile

object ArkoiFileType : LanguageFileType(ArkoiLanguage) {

    override fun getName(): String = ArkoiBundle.message("arkoi.fileType.name")

    override fun getIcon() = ArkoiIcons.ARKOI_FILE

    override fun getDefaultExtension() = ArkoiBundle.message("arkoi.fileType.defaultExtension")

    override fun getCharset(file: VirtualFile, content: ByteArray) = ArkoiBundle.message("arkoi.fileType.charset")

    override fun getDescription() = ArkoiBundle.message("arkoi.fileType.description")

}