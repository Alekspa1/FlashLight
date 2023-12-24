package com.exampl3.flashlight.Presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.exampl3.flashlight.Domain.Adapter.ItemListAdapter
import com.exampl3.flashlight.Domain.Item
import com.exampl3.flashlight.Domain.Room.AppDatabase
import com.exampl3.flashlight.databinding.FragmentListBinding


class FragmentList : Fragment(), ItemListAdapter.onLongClick, ItemListAdapter.onClick {
    private lateinit var binding: FragmentListBinding
    private lateinit var viewModel: ViewModelListItem
    private lateinit var adapter: ItemListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelListItem()
        adapter = ItemListAdapter(this, this)
        initRcView()
        setSwipe()
        val db = Room.databaseBuilder(
            view.context,
            AppDatabase::class.java, "database-name"
        ).build()

//        viewModel.listItem.observe(viewLifecycleOwner) {
//            adapter.submitList(it)
//        }
        binding.imButton.setOnClickListener {
            DialogItemList.AlertList(requireContext(), object : DialogItemList.Listener {
                override fun onClick(name: String) {
                    //viewModel.addItem(Item(name))
                    db.userDao().insert(Item(name))
                   // adapter.submitList(db.userDao().getAllUsers())


                }

            }, null)

        }

    }

    private fun initRcView() {
        val rcView = binding.rcView
        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = adapter
    }
    private fun setSwipe() {
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val item = adapter.currentList[viewHolder.adapterPosition]
                    viewModel.deleteItem(item)
                }
            }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rcView)
    }



    companion object {
        fun newInstance() = FragmentList()
    }

    override fun onLongClick(item: Item) {
        viewModel.changeItem(item)
    }

    override fun onClick(id: Int) {
        DialogItemList.AlertList(requireContext(), object : DialogItemList.Listener {
            override fun onClick(name: String) {
                val oldElem = viewModel.getItemId(id)
                viewModel.changeItem(oldElem.copy(name = name, change = !oldElem.change))
            }
        }, id)
    }

}