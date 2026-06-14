package com.exampl3.flashlight.Presentation


import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Domain.ToastFun
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val modelFlashLight: ViewModelFlashLight by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setupBackButtonHandler()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.getStringExtra(Const.REBOOT)?.let { message ->
            // Показываем тост напрямую, без отправки во ViewModel
            ToastFun(this, message)

            // Обязательно удаляем экстра-данные, чтобы тост не вылезал при повороте экрана!
            intent.removeExtra(Const.REBOOT)
        }


    }



    private fun setupBackButtonHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navController = findNavController(R.id.fragmentContainerView2)
                val currentFragment = navController.currentDestination?.id

                // Список фрагментов где нужно сворачивать приложение
                val fragmentsToMinimize = setOf(
                    R.id.fragmentMain,
                )

                if (currentFragment in fragmentsToMinimize) {
                    // Сворачиваем приложение
                    moveTaskToBack(true)
                } else {
                    // Стандартное поведение - назад по навигации
                    navController.popBackStack()
                }
            }
        })
    }
}

