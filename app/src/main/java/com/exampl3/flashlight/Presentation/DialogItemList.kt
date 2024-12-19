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
    fun AlertDelete(context: Context, delete: ActionTrueOrFalse) {
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
    fun insertAlarm(context: Context, insertAlarm: ActionInt) {
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

    fun insertBilling(context: Context, billing: ActionInt, product: Array<String?>) {
        var result = 0
        val builred = AlertDialog.Builder(context)

        builred.setTitle("Выберите тип покупки")
        builred.setSingleChoiceItems(
            product, 0
        ) { _, id ->
            result = id
        }
            .setPositiveButton("OK"
            ) { window, _ ->
                billing.onClick(result)
                window.dismiss()
            }
            .setNegativeButton("Отмена") { window, _ ->
                window.cancel()
            }
        builred.create()

        val dialog = builred.create()
        dialog.show()

    }

    fun openAuth(context: Context, action: ActionTrueOrFalse) {
        val builred = AlertDialog.Builder(context)
        val dialog = builred.create()
        dialog.setTitle("Вы не авторизованы")
        dialog.setMessage("Для продолжения вам необходимо авторизоваться в RUSTORE")
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Авторизоваться"){ _, _->
            action.onClick(true)
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена"){_,_->
            action.onClick(false)
        }
        dialog.show()

    }

    interface Listener{
        fun onClick(name: String)
    }
    interface ActionTrueOrFalse{
        fun onClick(flag: Boolean)
    }
    interface ActionInt{
        fun onClick(result: Int)
    }



}