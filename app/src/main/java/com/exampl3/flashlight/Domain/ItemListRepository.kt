package com.exampl3.flashlight.Domain

interface ItemListRepository {
    fun addItem(item: Item)
    fun changeItem(id: Int): Item
    fun deleteItem(item: Item)
    fun getItemList(): List<Item>
}