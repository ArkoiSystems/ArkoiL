/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on March 16, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.lang.arkoi.actions

import com.arkoisystems.lang.arkoi.ArkoiBundle
import com.arkoisystems.lang.arkoi.icons.ArkoiIcons
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory

class CreateArkoiFileAction : CreateFileFromTemplateAction(
    ArkoiBundle.message("arkoi.actions.createFile.text"),
    ArkoiBundle.message("arkoi.fileType.description"),
    ArkoiIcons.ARKOI_FILE
) {

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?) = ArkoiBundle.message("arkoi.actions.createFile.0", newName)

    override fun buildDialog(
        project: Project?,
        directory: PsiDirectory?,
        builder: CreateFileFromTemplateDialog.Builder?
    ) {
        builder
            ?.setTitle(ArkoiBundle.message("arkoi.actions.createFile.title"))
            ?.addKind(ArkoiBundle.message("arkoi.fileType.description"), ArkoiIcons.ARKOI_FILE, "Arkoi File")
    }

}