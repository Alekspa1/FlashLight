package com.exampl3.flashlight.Presentation

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.exampl3.flashlight.Data.Const
import com.exampl3.flashlight.databinding.FragmentNotebookBinding



class FragmentNotebook : Fragment() {
    private lateinit var binding: FragmentNotebookBinding
    private lateinit var pref: SharedPreferences
    private lateinit var model: ViewModelNoteBook



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotebookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = ViewModelNoteBook()
        pref = this.requireActivity().getSharedPreferences("TABLE", Context.MODE_PRIVATE)
        initNoteBook()


        binding.imDelete.setOnClickListener {
            delete(view.context)
        }
        binding.imSetting.setOnClickListener {
            binding.edotebook.textSize = model.sizeNoteBook(binding.edotebook.textSize)
            Log.d("MyLog", "Отправляю Шрифт ${model.sizeNoteBook(binding.edotebook.textSize)}")
        }
    }
    override fun onStop() {
        super.onStop()
        val notebook = binding.edotebook.text.trim()
        val size = binding.edotebook.textSize
        val edit = pref.edit()
        edit.putString(Const.keyNoteBook, notebook.toString())
        edit.putFloat(Const.keyNoteBookSize,size)

        edit.apply()
    }


    private fun delete(context: Context){
        DialogItemList.AlertDelete(context, object : DialogItemList.Delete{
            override fun onClick(flag: Boolean) {
                if (flag) binding.edotebook.setText("")
            }
        })
    } // удаляю заметки
    private fun initNoteBook(){
        binding.edotebook.setText(pref.getString(Const.keyNoteBook, ""))
        binding.edotebook.textSize = pref.getFloat(Const.keyNoteBookSize, 75F)/3
    } // Заполнение из бд


    companion object {
        fun newInstance() = FragmentNotebook()

    }
}