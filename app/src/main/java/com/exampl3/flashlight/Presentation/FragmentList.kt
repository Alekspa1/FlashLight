package com.exampl3.flashlight.Presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.exampl3.flashlight.Data.ItemListRepositoryImpl
import com.exampl3.flashlight.Domain.Adapter.ItemListAdapter
import com.exampl3.flashlight.Domain.Item
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.FragmentListBinding


class FragmentList : Fragment() {
    private lateinit var binding: FragmentListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        binding.imButton.setOnClickListener {
            DialogItemList.nameSitySearchDialog(requireContext())
        }

    }
    private fun initRcView(){
        val rcView = binding.rcView
        val adapter = ItemListAdapter()
        adapter.submitList(ItemListRepositoryImpl.getItemList())
        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = adapter
    }

    companion object {

        fun newInstance() = FragmentList()
    }
}