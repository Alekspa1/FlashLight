package com.exampl3.flashlight.Presentation

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.R

object DialogItemList {

    private val insertAlarmList =
        arrayOf("Один раз", "Каждый день", "Каждую неделю", "Каждый месяц", "Каждый год")



    fun AlertList(context: Context, listener: Listener, name: String?) {
        val builred = AlertDialog.Builder(context)
        val edName = EditText(context)
        edName.setText(name)
        edName.inputType
        builred.setView(edName)
        val dialog = builred.create()
        dialog.setTitle("Введите название категории")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Готово") { _, _ ->

            if (name == null) {
                if (edName.text.isEmpty()) {
                    Toast.makeText(context, "Поле должно быть заполнено", Toast.LENGTH_SHORT).show()
                } else {
                    listener.onClickItem(edName.text.toString().trim(), null, null, null, null)
                    dialog.dismiss()
                }

            } else {
                listener.onClickItem(edName.text.toString().trim(), null, null, null, null)
                dialog.dismiss()
            }

        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Назад") { _, _ ->
            dialog.dismiss()
        }

        dialog.show()

    }

    fun showExpandedImage(uri: String, context: Context) {

        val dialog = Dialog(context).apply {
            setContentView(R.layout.dialog_expanded_image)
            val image = findViewById<ImageView>(R.id.expandedImage)

            Glide.with(context)
                    .load(uri)
                    .into(image)

            image.setOnClickListener { dismiss() } // закрыть по клику
        }
        dialog.show()
    }

    fun alertItem(context: Context, listener: Listener, item: Item?,
                  model: ViewModelFlashLight,
                  lifecycleOwner: LifecycleOwner,
                  pick:  ActivityResultLauncher<String>) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.dialog_layout, null)
        val editText1 = dialogLayout.findViewById<EditText>(R.id.itemName)
        val editText2 = dialogLayout.findViewById<EditText>(R.id.edDescItemName)
        val imView = dialogLayout.findViewById<ImageView>(R.id.imPhoto)
        val deleteText = dialogLayout.findViewById<TextView>(R.id.tvDel)
        val addPhoto = dialogLayout.findViewById<TextView>(R.id.tvAddPhoto)

        model.uriPhoto.value = ""
        var uriString = ""

        model.uriPhoto.observe(lifecycleOwner) { uri ->
            updateImagePreview(imView, deleteText, addPhoto, uri)
            uriString = uri.toString()
        }
        imView.setOnClickListener {
            showExpandedImage(uriString, context)
        }
        if (item != null) {
            updateImagePreview(imView, deleteText, addPhoto, item.alarmText)
            editText1.setText(item.name)
            editText2.setText(item.desc)
            uriString = item.alarmText
            updateImagePreview(imView, deleteText, addPhoto, uriString)
        } else {
            updateImagePreview(imView, deleteText, addPhoto, "")
            editText1.requestFocus()
            editText1.postDelayed({
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editText1, InputMethodManager.SHOW_IMPLICIT)
            }, 200)
        }

        deleteText.setOnClickListener {
            model.uriPhoto.value = ""
        }

        addPhoto.setOnClickListener {
            pick.launch("image/*")
        }





        builder.setTitle("Сфокусироваться")
        var input1: String
        var input2: String
        builder.setPositiveButton("OK") { dialog, _ ->
            input1 = editText1.text.toString()
            input2 = editText2.text.toString()
            if (item == null) {
                if (input1.isEmpty()) {
                    Toast.makeText(context, "Название не должно быть пустым", Toast.LENGTH_SHORT).show()
                } else {
                    listener.onClickItem(input1.trim(), null, null, input2.trim(), uriString)
                    dialog.dismiss()
                }

            } else {
                listener.onClickItem(input1.trim(), null, item.id, input2.trim(), uriString)
                dialog.dismiss()
            }
        }
        builder.setNeutralButton("Установка будильника") { dialog, _ ->
            input1 = editText1.text.toString()
            input2 = editText2.text.toString()
            if (item == null) {
                if (input1.isEmpty()) {
                    Toast.makeText(context, "Название не должно быть пустым", Toast.LENGTH_SHORT).show()
                } else {
                    listener.onClickItem(input1.trim(), Const.ALARM, null, input2.trim(), uriString)
                    dialog.dismiss()
                }

            } else {
                listener.onClickItem(input1.trim(), Const.ALARM, item.id, input2.trim(), uriString)
                dialog.dismiss()
            }

        }
        builder.setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }
        builder.setView(dialogLayout)
        builder.show()
    }


    private fun updateImagePreview(
        imageView: ImageView,
        deleteBtn: TextView,
        addBtn: TextView,
        uri: String
    ) {
        if (uri.isNotEmpty()) {
            Glide.with(imageView.context)
                .load(uri)
                .into(imageView)
            imageView.visibility = View.VISIBLE
            deleteBtn.visibility = View.VISIBLE
            addBtn.text = "Заменить изображение"
        } else {
            imageView.visibility = View.GONE
            deleteBtn.visibility = View.GONE
            addBtn.text = "Добавить изображение"
        }
    }


    fun AlertDelete(context: Context, delete: ActionTrueOrFalse) {
        val builred = AlertDialog.Builder(context)
        val dialog = builred.create()
        dialog.setTitle("Вы уверены что хотите это удалить?")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Да") { _, _ ->
            delete.onClick(true)
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Нет") { _, _ ->
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
            .setPositiveButton(
                "OK"
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

        builred.setTitle("Выберите тип покупки (Неделя бесплатно)*")
        builred.setSingleChoiceItems(
            product, 0
        ) { _, id ->
            result = id
        }
            .setPositiveButton(
                "OK"
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

    fun settingSort(context: Context, sort: ActionInt) {
        val listSort = arrayOf("По умолчанию","Пользовательская (Можно вручную сортировать дела)")
        var result = 0
        val builred = AlertDialog.Builder(context)

        builred.setTitle("Выберите тип сортировки")
        builred.setSingleChoiceItems(
            listSort, 0
        ) { _, id ->
            result = id
        }
            .setPositiveButton(
                "OK"
            ) { window, _ ->
                sort.onClick(result)
                window.dismiss()
            }
            .setNegativeButton("Отмена") { window, _ ->
                window.cancel()
            }
        builred.create()

        val dialog = builred.create()
        dialog.show()

    }

    fun settingTheme(context: Context, sort: ActionInt) {
        val listSort = arrayOf("Неоновая","Деревянная")
        var result = 0
        val builred = AlertDialog.Builder(context)

        builred.setTitle("Выберите тему")
        builred.setSingleChoiceItems(
            listSort, 0
        ) { _, id ->
            result = id
        }
            .setPositiveButton(
                "OK"
            ) { window, _ ->
                sort.onClick(result)
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
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Авторизоваться") { _, _ ->
            action.onClick(true)
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена") { _, _ ->
            action.onClick(false)
        }
        dialog.show()

    }

    interface Listener {
        fun onClickItem(name: String, action: Int?, id: Int?, desc: String?, uri: String?)
    }

    interface ActionTrueOrFalse {
        fun onClick(flag: Boolean)
    }

    interface ActionInt {
        fun onClick(action: Int)
    }


}