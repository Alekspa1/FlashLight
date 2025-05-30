package com.exampl3.flashlight.Presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.exampl3.flashlight.Presentation.FragmentCalendar
import com.exampl3.flashlight.Presentation.FragmentList
import com.exampl3.flashlight.Presentation.FragmentNotebook

private val listFrag = listOf(
    FragmentNotebook.newInstance(),
    FragmentList.newInstance(),
    FragmentCalendar.newInstance()
)

class VpAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return listFrag.size
    }

    override fun createFragment(position: Int): Fragment {
        return listFrag[position]
    }
}