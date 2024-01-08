package com.exampl3.flashlight.Domain.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.exampl3.flashlight.Data.Const

@Entity
data class Item(
    @PrimaryKey var id: Int?,
    @ColumnInfo(name = "name")val name: String,
    @ColumnInfo(name = "change")val change: Boolean = false,
    )
