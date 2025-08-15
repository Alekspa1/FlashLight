package com.exampl3.flashlight.Domain.repository

import android.view.View
import android.widget.TextView
import com.exampl3.flashlight.Const

interface ThemeRepository {

    fun view(map: Map<Const.Action, Map<View, Int>>)
}