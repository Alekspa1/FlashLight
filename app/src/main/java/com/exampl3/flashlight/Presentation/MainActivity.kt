package com.exampl3.flashlight.Presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction()
            .replace(R.id.place_holder, FragmentList.newInstance())
            .commit()


    }
}