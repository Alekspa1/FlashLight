package com.exampl3.flashlight.Domain.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ListCategory (
    @PrimaryKey var id: Int?,
    @ColumnInfo(name = "name")val name: String,
)