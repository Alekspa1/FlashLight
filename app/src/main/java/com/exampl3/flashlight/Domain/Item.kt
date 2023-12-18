package com.exampl3.flashlight.Domain

import com.exampl3.flashlight.Data.Const

data class Item(
    val name: String,
    val change: Boolean = false,
    var id: Int = Const.UNDIFINE_ID)
