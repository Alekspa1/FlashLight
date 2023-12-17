package com.exampl3.flashlight.Domain.ItemListAction

import androidx.lifecycle.LiveData
import com.exampl3.flashlight.Domain.Item

interface ItemListRepository {
    fun addItem(item: Item)
    fun changeItem(item: Item)
    fun deleteItem(item: Item)
    fun getItemList(): LiveData<List<Item>>

    fun getItemId(id: Int): Item
}