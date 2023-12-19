package com.exampl3.flashlight.Domain.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class VpAdapter(fragment: FragmentActivity, private val list: List<Fragment>) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return list.size
    }
    override fun createFragment(position: Int): Fragment {
        return list[position]}
}