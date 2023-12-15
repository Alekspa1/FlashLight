package com.exampl3.flashlight.Data

import com.exampl3.flashlight.Domain.Item
import com.exampl3.flashlight.Domain.ItemListRepository

object ItemListRepositoryImpl: ItemListRepository {
    private val list = mutableListOf<Item>()
    override fun addItem(item: Item) {
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