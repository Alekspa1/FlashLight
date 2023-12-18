package com.exampl3.flashlight.Data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.exampl3.flashlight.Domain.Item
import com.exampl3.flashlight.Domain.ItemListAction.ItemListRepository

object ItemListRepositoryImpl: ItemListRepository {
    private val listLD = MutableLiveData<List<Item>>()
    private val list = sortedSetOf<Item>({ p0, p1 -> p0.id.compareTo(p1.id) })
    private var count = 0
    init {
        for (i in 0..5){
            addItem(Item("Номер $i"))
        }
    }
    override fun addItem(item: Item) {
        if(item.id == Const.UNDIFINE_ID){
            item.id = count++
        }
        list.add(item)
        update()
    }

    override fun changeItem(item: Item){
        val oldElem = getItemId(item.id)
        deleteItem(oldElem)
        addItem(item)

    }

    override fun deleteItem(item: Item) {
        list.remove(item)
        update()
    }

    override fun getItemList(): LiveData<List<Item>> {
        return listLD
    }

    override fun getItemId(id: Int): Item {
        return list.find { it.id == id }!!
    }
    private fun update(){
        listLD.value = list.toList()
    }

}