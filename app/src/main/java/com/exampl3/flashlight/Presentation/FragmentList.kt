package com.exampl3.flashlight.Presentation


import android.content.Context
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData

import androidx.recyclerview.widget.LinearLayoutManager

import androidx.room.Room
import com.exampl3.flashlight.Data.Const
import com.exampl3.flashlight.Domain.Adapter.ItemListAdapter
import com.exampl3.flashlight.Domain.Room.Item
import com.exampl3.flashlight.Domain.Room.GfgDatabase
import com.exampl3.flashlight.databinding.FragmentListBinding


class FragmentList : Fragment(), ItemListAdapter.onLongClick, ItemListAdapter.onClick {
    private lateinit var binding: FragmentListBinding
    private lateinit var adapter: ItemListAdapter
    private lateinit var db: GfgDatabase
    private lateinit var modelFlashLight: ViewModelFlashLight

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        modelFlashLight = ViewModelFlashLight()
        initDb(view.context)
        initRcView()

        binding.imButton.setOnClickListener {
            DialogItemList.AlertList(requireContext(), object : DialogItemList.Listener {
                override fun onClick(name: String) {
                    Thread {
                        db.CourseDao().insertAll(Item(null, name))
                    }.start()
                }
            }, null)

        }

    }
    private fun initDb(context: Context) {
        db = Room.databaseBuilder(
            context,
            GfgDatabase::class.java, "db"
        ).build()
    }
    private fun initRcView() {
        adapter = ItemListAdapter(this, this)
        val rcView = binding.rcView
        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = adapter
        db.CourseDao().getAll().asLiveData().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

    }


    override fun onLongClick(item: Item) {
                DialogItemList.AlertList(requireContext(), object : DialogItemList.Listener {
            override fun onClick(name: String) {
                Thread {
                    db.CourseDao().update(item.copy(name = name))
                }.start()
            }
        }, item.name)

    }
    override fun onClick(item: Item, action: Int) {
        if (action == Const.change){
            Thread {
                view?.let { modelFlashLight.turnVibro(it.context, 100) }
                db.CourseDao().update(item.copy(change = !item.change))
            }.start()
        } else {
            if (item.change) {
                Thread {
                    view?.let { modelFlashLight.turnVibro(it.context, 100) }
                    db.CourseDao().delete(item)
                }.start()
            } else{ delete(requireContext(), item)

            }
        }


    }
    private fun delete(context: Context, item: Item){
        DialogItemList.AlertDelete(context, object : DialogItemList.Delete{
            override fun onClick(flag: Boolean) {
                if (flag) {
                    Thread {
                        view?.let { modelFlashLight.turnVibro(it.context, 100) }
                        db.CourseDao().delete(item)
                    }.start()
                }
            }
        })
    } // удаляю заметки
    companion object {
        fun newInstance() = FragmentList()
    }

}