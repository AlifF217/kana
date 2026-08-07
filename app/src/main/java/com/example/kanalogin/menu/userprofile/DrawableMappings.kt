package com.example.kanalogin.menu.userprofile

import com.example.kanalogin.R

object DrawableMappings {
    val drawables = mapOf(
        "Boccher" to R.drawable.bocchimcd,
        "Ryo" to R.drawable.ryo,
        "Nijika" to R.drawable.nijika,
        "Kita" to R.drawable.kita1,
        "Seika 1" to R.drawable.seika1,
        "Seika 2" to R.drawable.seika2,
        "Kikuri" to R.drawable.kikuri,
        "Yui" to R.drawable.yui,
        "Ritsu" to R.drawable.ritsu,
        "Mio" to R.drawable.mio,
        "Tsumugi 1" to R.drawable.tsumugi1,
        "Tsumugi 2" to R.drawable.tsumugi2,
        "Azusa" to R.drawable.azusa,
        "Azunyan 1" to R.drawable.azusa2,
        "Azunyan 2" to R.drawable.azusa3,
        "Ice Cube" to R.drawable.icecube,
        "Jack Frost" to R.drawable.jackfrostsmt,

        "Ä̸̛̛̹̫̣̓̋q̴̧̛͕̑̿͑́̄͒̚̕̕m̵̝̮̙̙̼̰̟̼̲̉͑͂͛͒̈a̵̧̺̬͚͚̠͋̾́a̸͓̯̜̤̦̳̖̳̭̮͛̌̂̃͂̃̄r̴̪̩̠̭̝̮̼̗͉̈́̋̍͛̏̈́̕" to R.drawable.aqmar
    )

    fun getDrawableId(name: String): Int {
        return drawables[name] ?: android.R.drawable.ic_menu_camera // Default to a placeholder if not found
    }
}
