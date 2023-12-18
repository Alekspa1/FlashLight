package com.exampl3.flashlight.Presentation

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import android.widget.Toast

object DialogItemList {

    fun AlertList(context: Context, listener: Listener){
        val builred = AlertDialog.Builder(context)
        val edName = EditText(context)
        builred.setView(edName)
        val dialog = builred.create()
        dialog.setTitle("Введите название дела")

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Готово"){_,_->
            if (edName.text.isEmpty()){
                edName.error = "Поле должно быть заполнено"
                Toast.makeText(context, "Поле должно быть заполнено", Toast.LENGTH_SHORT).show()
            } else {
                listener.onClick(edName.text.toString().trim())
                dialog.dismiss()
            }

        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Назад"){_,_->
            dialog.dismiss()
        }

        dialog.show()

    }
    interface Listener{
        fun onClick(name: String)
    }
}