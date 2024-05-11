package com.exampl3.flashlight.Presentation
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
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
import com.exampl3.flashlight.databinding.FragmentNotebookBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FragmentNotebook : Fragment() {
    private lateinit var binding: FragmentNotebookBinding
    private val modelFlashLight: ViewModelFlashLight by activityViewModels()
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
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
            if (result.resultCode == RESULT_OK){
                val oldText = binding.edotebook.text.toString()
                val newText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val finish = "$oldText \n${newText?.get(0)}"
                binding.edotebook.setText(finish)
            }

        }
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
    }


    private fun delete(context: Context){
        DialogItemList.AlertDelete(context, object : DialogItemList.Delete{
            override fun onClick(flag: Boolean) {
                if (flag) binding.edotebook.setText("")
            }
        })
    } // удаляю заметки
    private fun initNoteBook(){
        binding.edotebook.setText(modelFlashLight.getNotebook())
    } // Заполнение из бд



    companion object {
        fun newInstance() = FragmentNotebook()

    }
}