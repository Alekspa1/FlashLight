package com.exampl3.flashlight.Data

import com.exampl3.flashlight.Domain.Item
import com.exampl3.flashlight.Domain.ItemListAction.ItemListRepository

object ItemListRepositoryImpl: ItemListRepository {
    private val list = mutableListOf<Item>()
    private var count = 0
    init {
        for (i in 0..10){
            addItem(Item("Номер $i", true))
        }
    }
    override fun addItem(item: Item) {
        if(item.id == Const.UNDIFINE_ID){
            item.id = count++
        }
        list.add(item)
    }

    override fun changeItem(item: Item){
        val oldElem = getItemId(item.id)
        deleteItem(oldElem)
        addItem(item)

    }

    override fun deleteItem(item: Item) {
        list.remove(item)
    }

    override fun getItemList(): List<Item> {
        return list.toList()
    }

    override fun getItemId(id: Int): Item {
        return list.find { it.id == id }!!
    }
}