package com.exampl3.flashlight.Presentation

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import android.widget.Toast

object DialogItemList {
    private val insertAlarmList = arrayOf("Один раз","Каждый день","Каждую неделю","Каждый месяц", "Каждый год")

    fun AlertList(context: Context, listener: Listener, name: String?){
        val builred = AlertDialog.Builder(context)
        val edName = EditText(context)
        edName.setText(name)
        edName.inputType
        builred.setView(edName)
        val dialog = builred.create()
        dialog.setTitle("Введите название дела")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Готово"){_,_->

            if (name == null){
                if (edName.text.isEmpty()){
                    Toast.makeText(context, "Поле должно быть заполнено", Toast.LENGTH_SHORT).show()
                } else {
                    listener.onClick(edName.text.toString().trim())
                    dialog.dismiss()
                }

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
    fun AlertDelete(context: Context, delete: Delete) {
        val builred = AlertDialog.Builder(context)
        val dialog = builred.create()
        dialog.setTitle("Вы уверены что хотите это удалить?")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Да"){ _, _->
            delete.onClick(true)
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Нет"){_,_->
            delete.onClick(false)
        }
        dialog.show()

    }
    fun insertAlarm(context: Context, insertAlarm: InsertAlarm) {
        var result = 0

        val builred = AlertDialog.Builder(context)

        builred.setTitle("Как часто повторять?")
        builred.setSingleChoiceItems(
            insertAlarmList, 0
        ) { _, id ->
            result = id

        }
            .setPositiveButton("OK"
            ) { window, _ ->
                insertAlarm.onClick(result)
               window.dismiss()
            }
            .setNegativeButton("Отмена") { window, _ ->
                window.cancel()
            }
        builred.create()

        val dialog = builred.create()
        dialog.show()

    }

    interface Listener{
        fun onClick(name: String)
    }
    interface Delete{
        fun onClick(flag: Boolean)
    }
    interface InsertAlarm{
        fun onClick(result: Int)
    }



}