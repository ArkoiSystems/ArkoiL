/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on March 10, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.lang.arkoi.psi

import com.arkoisystems.lang.arkoi.ArkoiLanguage
import com.intellij.psi.tree.IElementType

open class ArkoiTokenType(debugName: String) : IElementType(debugName.toLowerCase(), ArkoiLanguage)