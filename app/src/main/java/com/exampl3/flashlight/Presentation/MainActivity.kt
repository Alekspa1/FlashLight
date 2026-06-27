package com.exampl3.flashlight.Presentation


import android.content.Intent
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
import ru.rustore.sdk.pay.IntentInteractor
import ru.rustore.sdk.pay.RuStorePayClient


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val intentInteractor: IntentInteractor by lazy {
        RuStorePayClient.instance.getIntentInteractor()
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            intentInteractor.proceedIntent(intent)
        }
        setupBackButtonHandler()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intentInteractor.proceedIntent(intent)
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

