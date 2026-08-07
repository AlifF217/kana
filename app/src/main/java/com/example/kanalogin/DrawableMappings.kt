package com.example.kanalogin

import com.example.kanalogin.R

object DrawableMappings {
    val drawables = mapOf(
        "Ryo" to R.drawable.ryo,
        "Ice Cube" to R.drawable.icecube,
        "Jack Frost" to R.drawable.jackfrostsmt,
        "Boccher" to R.drawable.bocchimcd
    )

    fun getDrawableId(name: String): Int {
        return drawables[name] ?: android.R.drawable.ic_menu_camera // Default to a placeholder if not found
    }
}
