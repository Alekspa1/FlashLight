package com.exampl3.flashlight.Domain

interface ItemListRepository {
    fun addItem(item: Item)
    fun changeItem(item: Item)
    fun deleteItem(item: Item)
    fun getItemList(): List<Item>

    fun getItemId(id: Int): Item
}