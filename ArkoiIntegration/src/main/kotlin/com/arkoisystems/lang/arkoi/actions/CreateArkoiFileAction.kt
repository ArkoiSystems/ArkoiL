/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on March 17, 2020
 * Author єхcsє#5543 aka timo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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