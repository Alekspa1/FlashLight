package com.exampl3.flashlight.Presentation

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import com.exampl3.flashlight.R

object DialogItemList {

    fun nameSitySearchDialog(context: Context, listener: Listener){
        val builred = AlertDialog.Builder(context)
        val edName = EditText(context)
        builred.setView(edName)
        val dialog = builred.create()

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Готово"){_,_->
            listener.onClick(edName.text.toString().trim())

            dialog.dismiss()
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