package com.exampl3.flashlight.Domain

import android.content.Context
import android.widget.Toast

fun ToastFun(contex: Context, value: String){
    Toast.makeText(contex, value, Toast.LENGTH_SHORT).show()
}