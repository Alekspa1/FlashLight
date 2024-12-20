package com.exampl3.flashlight.Presentation

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.R

object DialogItemList {
    private val insertAlarmList = arrayOf("Один раз","Каждый день","Каждую неделю","Каждый месяц", "Каждый год")

    fun AlertList(context: Context, listener: Listener, name: String?){
        val builred = AlertDialog.Builder(context)
        val edName = EditText(context)
        edName.setText(name)
        edName.inputType
        builred.setView(edName)
        val dialog = builred.create()
        dialog.setTitle("Введите название категории")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Готово"){_,_->

            if (name == null){
                if (edName.text.isEmpty()){
                    Toast.makeText(context, "Поле должно быть заполнено", Toast.LENGTH_SHORT).show()
                } else {
                    listener.onClickItem(edName.text.toString().trim(), null,null,null)
                    dialog.dismiss()
                }

            } else {
                listener.onClickItem(edName.text.toString().trim(), null,null,null)
                dialog.dismiss()
            }

        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Назад"){_,_->
            dialog.dismiss()
        }

        dialog.show()

    }
    fun alertItem(context: Context, listener: Listener, name: String?, id: Int?, desc: String?){
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.dialog_layout, null)
        val editText1 = dialogLayout.findViewById<EditText>(R.id.edTitleAlert)
        val editText2 = dialogLayout.findViewById<EditText>(R.id.edDescAlert)
        editText1.setText(name)
        editText2.setText(desc)
        builder.setTitle("Введите данные")
        var input1 = editText1.text.toString()
        var input2 = editText2.text.toString()
        builder.setPositiveButton("OK") { dialog, _ ->
            input1 = editText1.text.toString()
            input2 = editText2.text.toString()
            if (name == null){
                if (input1.isEmpty()){
                    Toast.makeText(context, "Поле должно быть заполнено", Toast.LENGTH_SHORT).show()
                } else {
                    listener.onClickItem(input1.trim(), null, id,input2.trim())
                    dialog.dismiss()
                }

            } else {
                listener.onClickItem(input1.trim(), null, id,input2.trim())
                dialog.dismiss()
            }
        }
        builder.setNeutralButton("Установка будильника"){dialog, _ ->
            input1 = editText1.text.toString()
            input2 = editText2.text.toString()
            if (name == null){
                if (input1.isEmpty()){
                    Toast.makeText(context, "Поле должно быть заполнено", Toast.LENGTH_SHORT).show()
                } else {
                    listener.onClickItem(input1.trim(), Const.alarm, id, input2.trim())
                    dialog.dismiss()
                }

            } else {
                listener.onClickItem(input1.trim(),Const.alarm, id, input2.trim())
                dialog.dismiss()
            }

        }
        builder.setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }
        builder.setView(dialogLayout)
        builder.show()
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
        fun onClickItem(name: String, action: Int?, id: Int?, desc: String?)
    }
    interface ActionTrueOrFalse{
        fun onClick(flag: Boolean)
    }
    interface ActionInt{
        fun onClick(result: Int)
    }



}