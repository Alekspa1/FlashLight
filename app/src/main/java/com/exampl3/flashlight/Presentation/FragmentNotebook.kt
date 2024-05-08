package com.exampl3.flashlight.Presentation
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.exampl3.flashlight.Data.Const
import com.exampl3.flashlight.databinding.FragmentNotebookBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FragmentNotebook : Fragment() {
    private lateinit var binding: FragmentNotebookBinding
    private val modelFlashLight: ViewModelFlashLight by activityViewModels()
//    private lateinit var pref: SharedPreferences
//    private lateinit var greetings: String
    @Inject
    lateinit var voiceIntent: Intent



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotebookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        greetings = "Дорогие пользователи! \nВвиду особенности некоторых моделей телефонов," +
//                " установленные напоминания сбиваются после перезагрузки устройства," +
//                " если вы столкнулись с такой проблемой, вам необходимо в настройках приложения," +
//                " включить автозапуск приложения или разрешить приложению работать в фоновом режиме. " +
//                "Либо повторно входить в приложение после перезагрузки, чтобы напоминания обновились."
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
            if (result.resultCode == RESULT_OK){
                val oldText = binding.edotebook.text.toString()
                val newText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val finish = "$oldText \n${newText?.get(0)}"
                binding.edotebook.setText(finish)
            }

        }
//        pref = this.requireActivity().getSharedPreferences("TABLE", Context.MODE_PRIVATE)
        initNoteBook()
        binding.imDelete.setOnClickListener {
            delete(view.context)
        }
        binding.imageView2.setOnClickListener {
            try {
                launcher.launch(voiceIntent)
            }
            catch (e: Exception){
                Toast.makeText(view.context, "Голосовой ввод пока недоступен для вашего устройства", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onStop() {
        super.onStop()
        val notebook = binding.edotebook.text.trim()
        modelFlashLight.saveNoteBook(notebook.toString())
//        val edit = pref.edit()
//        edit.putString(Const.keyNoteBook, notebook.toString())
//        edit.apply()
    }


    private fun delete(context: Context){
        DialogItemList.AlertDelete(context, object : DialogItemList.Delete{
            override fun onClick(flag: Boolean) {
                if (flag) binding.edotebook.setText("")
            }
        })
    } // удаляю заметки
    private fun initNoteBook(){
       // binding.edotebook.setText(pref.getString(Const.keyNoteBook,greetings))
        binding.edotebook.setText(modelFlashLight.getNotebook())
    } // Заполнение из бд



    companion object {
        fun newInstance() = FragmentNotebook()

    }
}