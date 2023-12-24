package com.exampl3.flashlight.Domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.exampl3.flashlight.Data.Const

@Entity
data class Item(
    @ColumnInfo(name = "name")val name: String,
    @ColumnInfo(name = "change")val change: Boolean = false,
    @PrimaryKey var id: Int = Const.UNDIFINE_ID)
