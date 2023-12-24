package com.exampl3.flashlight.Domain.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GFG(
    @PrimaryKey val courseID: Int? = null,
    @ColumnInfo(name = "courseName") val name: String? = null,
    @ColumnInfo(name = "courseID") val email: String?= null,
    @ColumnInfo(name = "coursePrice") val avatar: String?= null
)

