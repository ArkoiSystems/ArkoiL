/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on March 16, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.lang.arkoi

import com.intellij.CommonBundle
import com.intellij.reference.SoftReference
import org.jetbrains.annotations.PropertyKey
import java.lang.ref.Reference
import java.util.*


object ArkoiBundle {

    private const val path = "bundle.ArkoiBundle"

    private var ourBundle: Reference<ResourceBundle>? = null

    fun message(
        @PropertyKey(resourceBundle = path) key: String,
        vararg params: Any
    ) = CommonBundle.message(getBundle(), key, *params)

    private fun getBundle(): ResourceBundle {
        var bundle = SoftReference.dereference(ourBundle)
        return if (bundle == null) {
            bundle = ResourceBundle.getBundle(path)
            ourBundle = SoftReference(bundle)
            bundle
        } else bundle
    }

}